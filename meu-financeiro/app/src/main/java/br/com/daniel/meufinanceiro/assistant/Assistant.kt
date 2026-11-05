package br.com.daniel.meufinanceiro.assistant

import br.com.daniel.meufinanceiro.data.ActionStatus
import br.com.daniel.meufinanceiro.data.ChatMessage
import br.com.daniel.meufinanceiro.data.FinanceRepository
import br.com.daniel.meufinanceiro.data.PayMethod
import br.com.daniel.meufinanceiro.data.SourceType
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.FinanceCalc
import br.com.daniel.meufinanceiro.domain.Health
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.domain.Period
import br.com.daniel.meufinanceiro.domain.PeriodKind
import br.com.daniel.meufinanceiro.domain.QuickEntryParser
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import com.anthropic.core.JsonValue
import com.anthropic.core.jsonMapper
import com.anthropic.errors.AnthropicServiceException
import com.anthropic.errors.BadRequestException
import com.anthropic.errors.RateLimitException
import com.anthropic.errors.UnauthorizedException
import com.anthropic.models.messages.ContentBlockParam
import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.MessageParam
import com.anthropic.models.messages.OutputConfig
import com.anthropic.models.messages.Tool
import com.anthropic.models.messages.ToolResultBlockParam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import java.time.Duration

/**
 * Financial assistant. Two engines over the SAME tools ([AssistantTools]):
 *  - Claude (when an API key is configured): free conversation with real tool calling.
 *  - Offline: understands the most common questions and commands without internet.
 * The conversation is stored in chat_messages (persistent history).
 */
class Assistant(private val repo: FinanceRepository, private val apiKey: () -> String?, private val today: () -> Long = { Dates.today() }) {
    val tools = AssistantTools(repo, today)

    companion object { const val MODEL = "claude-opus-5-5" }

    /** Handles one user message: stores it, answers, stores the answer (and pending-action cards). */
    suspend fun send(text: String): Unit {
        val userText = text.trim()
        if (userText.isEmpty()) return
        val history = repo.chat()
        repo.addChat(ChatMessage(role = "user", text = userText))
        val key = apiKey()
        val pendings = mutableListOf<AssistantTools.Outcome.NeedsConfirmation>()
        val reply = if (!key.isNullOrBlank()) {
            claude(key, history, userText, pendings) ?: ("(Sem conexão com o Claude agora. Respondi no modo offline.)\n\n" + offline(userText, pendings))
        } else offline(userText, pendings)
        repo.addChat(ChatMessage(role = "assistant", text = reply))
        pendings.forEach { repo.addChat(ChatMessage(role = "system", text = it.summary, actionId = it.actionId)) }
    }

    suspend fun confirmFromUi(actionId: Long) {
        val o = tools.confirm(actionId.toInt())
        repo.addChat(ChatMessage(role = "assistant", text = if (o is AssistantTools.Outcome.Done) "Feito. ${repo.action(actionId)?.summary ?: ""}" else "Não consegui executar: ${errorText(o.json)}"))
    }

    suspend fun cancelFromUi(actionId: Long) {
        tools.cancel(actionId.toInt())
        repo.addChat(ChatMessage(role = "assistant", text = "Cancelado. Nada foi alterado."))
    }

    suspend fun undoFromUi(actionId: Long) {
        val act = repo.action(actionId) ?: return
        try {
            tools.undo(act)
            repo.saveAction(act.copy(status = ActionStatus.UNDONE))
            repo.addChat(ChatMessage(role = "assistant", text = "Desfeito: ${act.summary}"))
        } catch (e: Exception) {
            repo.addChat(ChatMessage(role = "assistant", text = "Não foi possível desfazer: ${e.message}"))
        }
    }

    private fun errorText(json: String) = runCatching { Json.parseToJsonElement(json).jsonObject["erro"]?.toString()?.trim('"') }.getOrNull() ?: json

    /* ============================ Claude ============================ */

    private fun systemPrompt(name: String) = """Você é a assistente financeira pessoal de $name, dentro do app privado "Meu Financeiro" no celular dele.
Hoje é ${Dates.format(today())}. Moeda: real (R$). Responda SEMPRE em português do Brasil, curto, claro e direto, como uma amiga que entende de finanças.

Contexto de $name: renda variável (recreador no Tio Rex Restaurante, recreação em festas e eventos, pipoca e algodão-doce em eventos, e um pequeno negócio de plaquinhas NFC). Ele separa cada entrada em 50% gastos / 25% reserva de emergência / 25% para sair do Brasil (tem cidadania italiana). Objetivos: computador para edição e 3D (R$ 4.000 a R$ 6.000), reserva de emergência e a mudança para a Europa.

Regras obrigatórias:
1. Use SOMENTE dados obtidos pelas ferramentas. Nunca invente valores, datas, nomes ou fatos. Se faltar dado, diga isso.
2. Dinheiro reservado (caixinhas) NUNCA é dinheiro disponível para gastar. "Quanto posso gastar" = saldo livre (use resumo_financeiro). Limite do cartão não é dinheiro.
3. Receita é diferente de lucro: lucro = faturamento − custos ligados ao trabalho. Dinheiro de família, empréstimo, ajuda e reembolso não é renda.
4. Para registrar, editar, excluir ou mudar configurações, use as ferramentas. Quando a ferramenta responder "aguardando_confirmacao", mostre o resumo e pergunte se ele confirma. Só chame confirmar_acao depois de um "sim" claro na mensagem seguinte. Nunca confirme por conta própria.
5. Nunca execute nada com ambiguidade (categoria, valor, forma de pagamento, trabalho relacionado). Pergunte antes. Um Uber pessoal não é custo de trabalho: só associe a um trabalho se ele disser.
6. Para encontrar ids de movimentações, use consultar_movimentacoes antes de editar ou excluir.
7. Não use saudações nem invente informações pessoais. Chame-o de $name só se for natural.
8. Formatação: frases curtas; listas com "-" quando ajudar. Sem tabelas."""

    internal fun toolDefs(): List<Tool> = tools.specs.map { s ->
        val schema = tools.schemaOf(s)
        @Suppress("UNCHECKED_CAST")
        val props = schema["properties"] as Map<String, Map<String, Any>>
        Tool.builder().name(s.name).description(s.description)
            .inputSchema(Tool.InputSchema.builder()
                .properties(Tool.InputSchema.Properties.builder().apply { props.forEach { (k, v) -> putAdditionalProperty(k, JsonValue.from(v)) } }.build())
                .required(s.required)
                .build())
            .build()
    }

    /** Returns the answer, or null when the API could not be reached (caller falls back to offline). */
    private suspend fun claude(key: String, history: List<ChatMessage>, userText: String, pendings: MutableList<AssistantTools.Outcome.NeedsConfirmation>): String? = withContext(Dispatchers.IO) {
        val client = AnthropicOkHttpClient.builder().apiKey(key.trim()).timeout(Duration.ofMinutes(3)).build()
        try {
            val name = repo.snapshot().userName
            val b = MessageCreateParams.builder().model(MODEL).maxTokens(16000L).system(systemPrompt(name))
                .outputConfig(OutputConfig.builder().effort(OutputConfig.Effort.MEDIUM).build())
            toolDefs().forEach { b.addTool(it) }
            // Previous conversation as plain text turns (last 20), alternating user/assistant.
            val turns = history.filter { it.role == "user" || it.role == "assistant" }.takeLast(20).toMutableList()
            while (turns.isNotEmpty() && turns.first().role != "user") turns.removeAt(0)
            var lastRole: String? = null
            for (m in turns) {
                if (m.role == lastRole) continue
                if (m.role == "user") b.addUserMessage(m.text) else b.addAssistantMessage(m.text)
                lastRole = m.role
            }
            if (lastRole == "user") b.addAssistantMessage("(sem resposta)")
            b.addUserMessage(userText)

            var finalText = ""
            for (step in 0 until 8) {
                val resp = try { client.messages().create(b.build()) } catch (e: BadRequestException) { throw e }
                b.addMessage(resp)
                val text = resp.content().mapNotNull { it.text().orElse(null)?.text() }.joinToString("\n").trim()
                if (text.isNotEmpty()) finalText = text
                val uses = resp.content().mapNotNull { it.toolUse().orElse(null) }
                if (uses.isEmpty()) break
                val results = uses.map { tu ->
                    val args: JsonObject = runCatching {
                        Json.parseToJsonElement(jsonMapper().writeValueAsString(tu._input())).jsonObject
                    }.getOrElse { JsonObject(emptyMap()) }
                    val o = tools.run(tu.name(), args)
                    if (o is AssistantTools.Outcome.NeedsConfirmation) pendings += o
                    ContentBlockParam.ofToolResult(ToolResultBlockParam.builder().toolUseId(tu.id()).content(o.json).isError(o is AssistantTools.Outcome.Error).build())
                }
                b.addMessage(MessageParam.ofUserOfContentBlockParams(results))
            }
            finalText.ifBlank { "Pronto." }
        } catch (e: UnauthorizedException) {
            "A chave da API do Claude foi recusada. Confira em Configurações → Inteligência artificial."
        } catch (e: RateLimitException) {
            "Muitas mensagens seguidas para o Claude. Espere um minuto e tente de novo."
        } catch (e: AnthropicServiceException) {
            null
        } catch (e: Exception) {
            null
        } finally {
            runCatching { client.close() }
        }
    }

    /* ============================ Offline ============================ */

    private val yes = Regex("^(sim|pode|pode sim|confirmo|confirma|confirmar|ok|isso|manda|beleza|claro|s)[.! ]*$")
    private val no = Regex("^(nao|não|cancela|cancelar|deixa|deixa pra la|esquece|n)[.! ]*$")

    private fun periodFrom(n: String): Period {
        val t = today()
        return when {
            n.contains("hoje") -> Period.of(PeriodKind.TODAY, t)
            n.contains("semana") -> Period.of(PeriodKind.WEEK, t)
            n.contains("mes passado") || n.contains("mes anterior") || n.contains("ultimo mes") -> Period.of(PeriodKind.LAST_MONTH, t)
            n.contains("ano") -> Period(PeriodKind.CUSTOM, Dates.day(t).withDayOfYear(1).toEpochDay(), t)
            else -> Period.of(PeriodKind.MONTH, t)
        }
    }

    private suspend fun offline(text: String, pendings: MutableList<AssistantTools.Outcome.NeedsConfirmation>): String {
        val n = QuickEntryParser.normalize(text).trim()
        val l = repo.snapshot()
        val c = FinanceCalc(l, today())
        val lastPending = repo.actions().firstOrNull { it.status == ActionStatus.PENDING }

        if (lastPending != null && yes.matches(n)) {
            val o = tools.confirm(lastPending.id.toInt())
            return if (o is AssistantTools.Outcome.Done) "Feito. ${lastPending.summary}." else "Não consegui: ${errorText(o.json)}"
        }
        if (lastPending != null && no.matches(n)) { tools.cancel(lastPending.id.toInt()); return "Cancelado. Nada foi alterado." }

        suspend fun propose(tool: String, args: JsonObject): String = when (val o = tools.run(tool, args)) {
            is AssistantTools.Outcome.NeedsConfirmation -> { pendings += o; "${o.summary}.\nConfirma? Responda \"sim\" ou toque em Confirmar." }
            is AssistantTools.Outcome.Done -> "Feito."
            is AssistantTools.Outcome.Error -> errorText(o.json)
        }

        // ---- commands ----
        Regex("(limite|saldo minimo|minimo).*?(\\d+[.,]?\\d*)").find(n)?.takeIf { Regex("\\b(mud|alter|coloc|defin|troc|ajust|pass)").containsMatchIn(n) }?.let {
            return propose("alterar_saldo_minimo", buildJsonObject { put("valor", it.groupValues[2].replace(',', '.').toDouble()) })
        }
        Regex("meta (d[aoe] |para (a |o )?)?(.+?) (para|pra|em) (r\\$ ?)?(\\d+[.,]?\\d*)").find(n)?.let {
            return propose("alterar_meta_caixinha", buildJsonObject { put("caixinha", it.groupValues[3].trim()); put("meta", it.groupValues[6].replace(".", "").replace(',', '.').toDouble()) })
        }
        Regex("(meu nome e|me chame de|me chama de) (.+)").find(n)?.let {
            val original = text.substringAfterLast(" de ").substringAfterLast(" é ").trim().ifBlank { it.groupValues[2] }
            return propose("alterar_nome_usuario", buildJsonObject { put("nome", original.replaceFirstChar { ch -> ch.uppercase() }) })
        }
        if (n.startsWith("desfaz") || n.contains("desfazer")) {
            val last = repo.actions().firstOrNull { it.status == ActionStatus.DONE } ?: return "Não há ação da assistente para desfazer."
            return propose("desfazer_acao", buildJsonObject { put("acao_id", last.id) })
        }

        // ---- questions ----
        val p = c.position()
        if (Regex("quanto (eu )?posso gastar|saldo livre|quanto (eu )?tenho (livre|pra gastar|para gastar)|posso gastar|disponivel").containsMatchIn(n)) {
            return buildString {
                append("Você pode gastar ${Money.format(p.freeAfterBillsCents)}")
                if (p.unpaidBillsCents > 0) append(" depois de pagar as contas do mês (${Money.format(p.unpaidBillsCents)}). Livre agora: ${Money.format(p.freeCents)}")
                append(".\n")
                append("- Dinheiro nas contas: ${Money.format(p.accountsCents)}\n")
                append("- Reservado nas caixinhas (não conta): ${Money.format(p.reservedCents)}\n")
                if (p.cardDebtCents > 0) append("- Já comprometido no cartão: ${Money.format(p.cardDebtCents)}\n")
                if (p.freeCents < c.minBalance()) append("Atenção: está abaixo do seu mínimo de ${Money.format(c.minBalance())}.")
            }.trim()
        }
        if (n.contains("como estou") || n.contains("como esta minha") || n.contains("resumo")) return Health.asText(c)
        if (Regex("qual trabalho|mais compensa|compensa mais|rende mais|da mais dinheiro|melhor trabalho|mais lucro").containsMatchIn(n)) {
            val per = periodFrom(n).let { if (it.kind == PeriodKind.MONTH && !n.contains("mes")) Period(PeriodKind.CUSTOM, today() - 89, today()) else it }
            val prof = c.profitability(per).filter { it.revenueCents > 0 }.sortedByDescending { it.netPerHourCents ?: it.netCents }
            if (prof.isEmpty()) return "Ainda não há trabalhos registrados em ${per.label.lowercase()} para comparar."
            return "Comparando ${if (per.kind == PeriodKind.CUSTOM) "os últimos 90 dias" else per.label.lowercase()} (lucro já descontando custos):\n" +
                prof.joinToString("\n") { "- ${it.name}: lucro ${Money.format(it.netCents)}" + (it.netPerHourCents?.let { h -> ", ${Money.format(h)}/h em ${br.com.daniel.meufinanceiro.ui.screens.fmtHours(it.hours)}" } ?: "") + " (faturou ${Money.format(it.revenueCents)}, custos ${Money.format(it.costsCents)})" }
        }
        if (n.contains("lucr") || n.contains("ganhei") && n.startsWith("quanto")) {
            val per = periodFrom(n)
            val r = c.report(per)
            return "Em ${per.label.lowercase()}: você faturou ${Money.format(r.grossRevenueCents)} em trabalhos e vendas e teve ${Money.format(r.profitCostsCents)} em custos. Lucro líquido: ${Money.format(r.netProfitCents)}." +
                (r.profit.filter { it.revenueCents > 0 }.takeIf { it.isNotEmpty() }?.let { "\n" + it.joinToString("\n") { sp -> "- ${sp.name}: ${Money.format(sp.netCents)}" } } ?: "")
        }
        Regex("(quanto )?gastei( com| de| em)? ?(.*)").find(n)?.takeIf { n.startsWith("quanto") }?.let { m ->
            val per = periodFrom(n)
            val what = m.groupValues[3].replace("?", " ").replace(Regex("\\b(este|esse|neste|nesse|no|na|essa|esta|nessa|nesta|mes|semana|hoje|passado|anterior|ano|ultimo)\\b"), " ").trim()
            val ev = c.report(per).expenseEvents
            val f = if (what.isBlank()) ev else ev.filter { e -> QuickEntryParser.normalize(e.description).contains(what) || QuickEntryParser.normalize(l.category(e.categoryId)?.name ?: "").contains(what) }
            val tot = f.sumOf { it.amountCents }
            val work = f.filter { it.isWorkCost }.sumOf { it.amountCents }
            return "Em ${per.label.lowercase()} você gastou ${Money.format(tot)}" + (if (what.isNotBlank()) " com $what" else "") + " (${f.size} lançamento(s))." +
                (if (work > 0) " Desse total, ${Money.format(work)} foram custos ligados aos seus trabalhos." else "")
        }
        if (Regex("fatura|cartao|parcel").containsMatchIn(n)) {
            val card = l.mainCard ?: return "Nenhum cartão cadastrado."
            val k = c.currentInvoiceKey(card)
            val inv = c.invoice(card, k)
            val next = c.invoice(card, Dates.ymPlus(k, 1))
            val parc = l.purchases.filter { it.cardId == card.id && it.installments > 1 }
            return "Fatura atual (${Dates.monthLabel(k)}): ${Money.format(inv.totalCents)}, falta pagar ${Money.format(inv.remainingCents)}. Vence: ${inv.dueDate?.let { Dates.format(it) } ?: "configure o vencimento"}.\n" +
                "Próxima fatura até agora: ${Money.format(next.totalCents)}. Total em aberto no cartão: ${Money.format(c.cardDebt(card.id))}." +
                (if (parc.isNotEmpty()) "\nParcelados: " + parc.joinToString { "${it.description} ${it.installments}x" } else "")
        }
        if (Regex("caixinha|meta|guardad|reserva|computador|sair do brasil").containsMatchIn(n) && n.contains("?").or(n.startsWith("quanto"))) {
            return "Reservado: ${Money.format(c.reserved())}\n" + c.reserveBoxes.joinToString("\n") { b -> val g = c.goal(b); "- ${b.name}: ${Money.format(g.balanceCents)}" + (g.goalCents?.let { " de ${Money.format(it)}" } ?: "") }
        }
        if (Regex("contas a pagar|vencimento|contas do mes|boletos?").containsMatchIn(n)) {
            val st = c.billStatuses()
            return if (st.isEmpty()) "Nenhuma conta ativa. Ative suas contas em Mais → Contas a pagar."
            else "Contas do mês (falta pagar ${Money.format(c.billsUnpaid())}):\n" + st.joinToString("\n") { "- ${it.bill.name}: ${Money.format(it.expectedCents)} · ${it.dueDate?.let { d -> Dates.formatShort(d) } ?: "sem data"} · ${it.state.name.lowercase()}" }
        }

        // ---- registrations through the quick-entry parser ----
        val q = QuickEntryParser.parse(text, l.categories, l.sources, today())
        if (q.amountCents != null) {
            val reais = q.amountCents / 100.0
            val data = Dates.format(q.date)
            return when (q.kind) {
                QuickEntryParser.Kind.LOAN -> propose("registrar_emprestimo", buildJsonObject { put("pessoa", q.person ?: "alguém"); put("valor", reais); put("data", data) })
                QuickEntryParser.Kind.WORK_INCOME -> propose("registrar_trabalho", buildJsonObject { put("fonte", l.source(q.sourceId)?.name ?: ""); put("valor_meu", reais); put("recebido", reais); put("data", data) })
                QuickEntryParser.Kind.FAMILY -> propose("registrar_entrada", buildJsonObject { put("valor", reais); put("fonte", l.sourceOfType(SourceType.FAMILY)?.name ?: "Dinheiro da família"); put("descricao", q.description); put("data", data) })
                QuickEntryParser.Kind.INCOME -> if (q.sourceId == null) "Entendi uma entrada de ${Money.format(q.amountCents)}, mas não sei de onde veio. É de trabalho (qual?), da família, empréstimo ou reembolso?"
                    else propose("registrar_entrada", buildJsonObject { put("valor", reais); put("fonte", l.source(q.sourceId)?.name ?: ""); put("data", data) })
                QuickEntryParser.Kind.NFC_SALE -> "Para registrar a venda de plaquinhas (com custo e estoque certinhos), use o lançamento rápido da tela Início ou Venda NFC. Entendi: ${q.quantity ?: 1} un. a ${q.unitPriceCents?.let { Money.format(it) } ?: "?"}."
                else -> {
                    val missing = q.missing.toMutableList()
                    if (q.method == null) missing += "a forma de pagamento (pix, dinheiro, débito ou crédito)"
                    if (missing.isNotEmpty()) "Entendi ${Money.format(q.amountCents)} (${q.description}), mas falta: ${missing.joinToString(", ")}. Escreva de novo com essa informação, ex.: \"${q.description.lowercase()} ${Money.toInput(q.amountCents)} pix\"" +
                        (if (q.workCost) " ou diga \"para o Tio Rex\" / \"recreação\" / \"pipoca\"." else ".")
                    else propose("registrar_despesa", buildJsonObject {
                        put("valor", reais); put("descricao", q.description); put("categoria", l.category(q.categoryId)?.name ?: "")
                        put("forma_pagamento", when (q.method) { PayMethod.CASH -> "dinheiro"; PayMethod.DEBIT -> "debito"; PayMethod.CREDIT -> "credito"; PayMethod.TRANSFER -> "transferencia"; PayMethod.PIX -> "pix"; else -> "outros" })
                        put("data", data); if (q.installments > 1) put("parcelas", q.installments)
                        q.workSourceId?.let { put("fonte_trabalho", l.source(it)?.name ?: "") }
                    })
                }
            }
        }

        val hasKey = !apiKey().isNullOrBlank()
        return "Ainda não entendi esse pedido no modo offline." + (if (!hasKey) " Para conversar livremente, configure a chave do Claude em Configurações → Inteligência artificial." else "") +
            "\nPosso responder, por exemplo:\n- Quanto posso gastar?\n- Quanto lucrei esta semana?\n- Quanto gastei com uber este mês?\n- Qual trabalho compensa mais?\n- Quanto está minha fatura?\n- Como estou financeiramente?\n- Mude meu saldo mínimo para 50\n- Gastei 25 com almoço no pix"
    }

    @Suppress("unused") private val keepImports = listOf(JsonPrimitive(""), FinanceRepository::class)
}
