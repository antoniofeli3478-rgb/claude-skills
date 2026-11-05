package br.com.daniel.meufinanceiro.assistant

import br.com.daniel.meufinanceiro.data.ActionStatus
import br.com.daniel.meufinanceiro.data.AssistantAction
import br.com.daniel.meufinanceiro.data.BoxKind
import br.com.daniel.meufinanceiro.data.Category
import br.com.daniel.meufinanceiro.data.FinanceRepository
import br.com.daniel.meufinanceiro.data.PayMethod
import br.com.daniel.meufinanceiro.data.SourceType
import br.com.daniel.meufinanceiro.data.Split
import br.com.daniel.meufinanceiro.data.TxnType
import br.com.daniel.meufinanceiro.data.WorkEntry
import br.com.daniel.meufinanceiro.domain.Alerts
import br.com.daniel.meufinanceiro.domain.BillState
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.FinanceCalc
import br.com.daniel.meufinanceiro.domain.Keys
import br.com.daniel.meufinanceiro.domain.Ledger
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.domain.Period
import br.com.daniel.meufinanceiro.domain.PeriodKind
import br.com.daniel.meufinanceiro.domain.QuickEntryParser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

/**
 * The real functions the assistant can call. Same code for the Claude assistant (tool calling)
 * and for the offline assistant. Every number comes from the database through [FinanceCalc].
 *
 * Safety policy:
 *  - READ tools run directly.
 *  - SIMPLE writes (register expense / income / work) run directly only if Antônio enabled
 *    "registros simples sem confirmação"; otherwise they wait for confirmation.
 *  - IMPORTANT writes (edit, delete, limits, goals, boxes, categories, name, loans) ALWAYS wait for
 *    confirmation and show the previous and the new value.
 *  - Every proposed/executed action is stored in assistant_actions and can be undone when possible.
 */
class AssistantTools(private val repo: FinanceRepository, private val today: () -> Long = { Dates.today() }) {

    enum class Kind { READ, SIMPLE, IMPORTANT }

    data class Param(val type: String, val description: String, val enum: List<String>? = null)
    data class Spec(val name: String, val description: String, val params: Map<String, Param>, val required: List<String>, val kind: Kind)

    sealed interface Outcome {
        val json: String
        data class Done(override val json: String, val actionId: Long? = null) : Outcome
        data class NeedsConfirmation(override val json: String, val actionId: Long, val summary: String) : Outcome
        data class Error(override val json: String) : Outcome
    }

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    private val period = mapOf(
        "inicio" to Param("string", "Data inicial dd/mm/aaaa (opcional)"),
        "fim" to Param("string", "Data final dd/mm/aaaa (opcional)"),
        "periodo" to Param("string", "Atalho de período", listOf("hoje", "semana", "mes", "mes_anterior", "ano", "tudo")),
    )

    val specs: List<Spec> = listOf(
        Spec("resumo_financeiro", "Situação atual: saldo livre (o que pode gastar), dinheiro reservado nas caixinhas, patrimônio, fatura, contas do mês, a receber, empréstimos e alertas. Use para 'quanto posso gastar', 'como estou'.", emptyMap(), emptyList(), Kind.READ),
        Spec("consultar_movimentacoes", "Lista e soma movimentações reais (entradas, despesas, transferências) com filtros. Use para 'quanto gastei com X', 'quais foram meus gastos'.", period + mapOf(
            "tipo" to Param("string", "Filtro de tipo", listOf("despesa", "entrada", "transferencia", "todos")),
            "categoria" to Param("string", "Nome da categoria (opcional)"),
            "texto" to Param("string", "Texto que aparece na descrição (ex.: uber) (opcional)"),
            "limite" to Param("integer", "Máximo de itens listados (padrão 30)"),
        ), emptyList(), Kind.READ),
        Spec("consultar_lucro", "Rentabilidade por fonte de renda no período: faturamento, custos ligados ao trabalho, lucro líquido, margem, horas e lucro por hora.", period, emptyList(), Kind.READ),
        Spec("consultar_cartao", "Cartão de crédito: fatura atual, anterior e próxima, valor pago, falta pagar, parcelamentos, compras futuras, datas.", emptyMap(), emptyList(), Kind.READ),
        Spec("consultar_caixinhas", "Caixinhas (dinheiro reservado): saldo, meta, quanto falta e previsão.", emptyMap(), emptyList(), Kind.READ),
        Spec("consultar_contas_a_pagar", "Compromissos do mês: atrasadas, a vencer, pagas.", emptyMap(), emptyList(), Kind.READ),
        Spec("relatorio", "Relatório do período: receitas, custos de trabalho, lucro, gastos pessoais, transporte, gastos por categoria, entradas que não são renda.", period, emptyList(), Kind.READ),
        Spec("listar_categorias_e_fontes", "Lista categorias de despesa, fontes de renda, contas e caixinhas cadastradas (use antes de registrar se tiver dúvida no nome).", emptyMap(), emptyList(), Kind.READ),
        Spec("registrar_despesa", "Registra uma despesa real. Se for custo de um trabalho (ex.: Uber para ir ao Tio Rex), informe fonte_trabalho.", mapOf(
            "valor" to Param("number", "Valor em reais (ex.: 25.5)"),
            "descricao" to Param("string", "Descrição curta"),
            "categoria" to Param("string", "Nome exato de uma categoria existente"),
            "forma_pagamento" to Param("string", "Forma", listOf("pix", "dinheiro", "debito", "credito", "transferencia", "outros")),
            "data" to Param("string", "dd/mm/aaaa (padrão hoje)"),
            "parcelas" to Param("integer", "Parcelas no crédito (padrão 1)"),
            "conta" to Param("string", "Nome da conta (opcional)"),
            "fonte_trabalho" to Param("string", "Nome da fonte de renda se for custo de trabalho (opcional)"),
        ), listOf("valor", "descricao", "categoria", "forma_pagamento"), Kind.SIMPLE),
        Spec("registrar_entrada", "Registra dinheiro recebido. Para família/empréstimo/ajuda/reembolso use a fonte correspondente (não é renda).", mapOf(
            "valor" to Param("number", "Valor em reais"),
            "fonte" to Param("string", "Nome exato de uma fonte de renda existente"),
            "descricao" to Param("string", "Descrição (opcional)"),
            "data" to Param("string", "dd/mm/aaaa (padrão hoje)"),
            "conta" to Param("string", "Conta que recebeu (opcional)"),
        ), listOf("valor", "fonte"), Kind.SIMPLE),
        Spec("registrar_trabalho", "Registra um trabalho/turno (Tio Rex, recreação, pipoca...) com valor que é seu, horas e quanto já recebeu.", mapOf(
            "fonte" to Param("string", "Nome da fonte de renda"),
            "valor_meu" to Param("number", "Quanto desse trabalho é seu, em reais"),
            "horas" to Param("number", "Horas trabalhadas (opcional)"),
            "recebido" to Param("number", "Quanto já recebeu agora (0 se pendente)"),
            "data" to Param("string", "dd/mm/aaaa (padrão hoje)"),
            "descricao" to Param("string", "Evento/descrição (opcional)"),
        ), listOf("fonte", "valor_meu", "recebido"), Kind.SIMPLE),
        Spec("registrar_emprestimo", "Registra dinheiro EMPRESTADO recebido (entra na conta, não é renda, fica a devolver).", mapOf(
            "pessoa" to Param("string", "Quem emprestou"), "valor" to Param("number", "Valor em reais"),
            "conta" to Param("string", "Conta que recebeu (opcional)"), "data" to Param("string", "dd/mm/aaaa (padrão hoje)"),
        ), listOf("pessoa", "valor"), Kind.IMPORTANT),
        Spec("editar_movimentacao", "Altera uma movimentação existente (use o id de consultar_movimentacoes).", mapOf(
            "id" to Param("string", "Id da movimentação (ex.: t12 ou p3)"),
            "valor" to Param("number", "Novo valor (opcional)"), "descricao" to Param("string", "Nova descrição (opcional)"),
            "data" to Param("string", "Nova data dd/mm/aaaa (opcional)"), "categoria" to Param("string", "Nova categoria (opcional)"),
        ), listOf("id"), Kind.IMPORTANT),
        Spec("excluir_movimentacao", "Exclui uma movimentação (entrada, despesa ou compra no cartão).", mapOf("id" to Param("string", "Id (ex.: t12 ou p3)")), listOf("id"), Kind.IMPORTANT),
        Spec("criar_categoria", "Cria uma categoria de despesa.", mapOf("nome" to Param("string", "Nome")), listOf("nome"), Kind.IMPORTANT),
        Spec("alterar_saldo_minimo", "Altera o saldo mínimo do alerta de saldo livre.", mapOf("valor" to Param("number", "Novo mínimo em reais")), listOf("valor"), Kind.IMPORTANT),
        Spec("alterar_meta_caixinha", "Altera a meta de uma caixinha.", mapOf("caixinha" to Param("string", "Nome da caixinha"), "meta" to Param("number", "Nova meta em reais (0 = sem meta)")), listOf("caixinha", "meta"), Kind.IMPORTANT),
        Spec("mover_caixinha", "Guarda dinheiro livre numa caixinha (valor positivo) ou retira de volta para o livre (valor negativo).", mapOf("caixinha" to Param("string", "Nome"), "valor" to Param("number", "Reais; negativo para retirar")), listOf("caixinha", "valor"), Kind.IMPORTANT),
        Spec("alterar_nome_usuario", "Altera o nome usado no app.", mapOf("nome" to Param("string", "Nome")), listOf("nome"), Kind.IMPORTANT),
        Spec("confirmar_acao", "Executa uma ação pendente SOMENTE depois que o usuário disse claramente que confirma.", mapOf("acao_id" to Param("integer", "Id da ação pendente")), listOf("acao_id"), Kind.READ),
        Spec("cancelar_acao", "Cancela uma ação pendente.", mapOf("acao_id" to Param("integer", "Id da ação pendente")), listOf("acao_id"), Kind.READ),
        Spec("desfazer_acao", "Desfaz uma ação já executada pela assistente, quando possível.", mapOf("acao_id" to Param("integer", "Id da ação")), listOf("acao_id"), Kind.IMPORTANT),
    )

    fun spec(name: String) = specs.firstOrNull { it.name == name }

    /* ======================= entry point ======================= */

    suspend fun run(name: String, args: JsonObject): Outcome = try {
        val s = spec(name) ?: return Outcome.Error(err("Ferramenta desconhecida: $name"))
        val ledger = repo.snapshot()
        when (s.kind) {
            Kind.READ -> read(name, args, ledger)
            Kind.SIMPLE -> if (ledger.settingBool(Keys.ASSISTANT_AUTO_SIMPLE)) propose(name, args, ledger, autoExecute = true) else propose(name, args, ledger, autoExecute = false)
            Kind.IMPORTANT -> propose(name, args, ledger, autoExecute = false)
        }
    } catch (e: IllegalArgumentException) {
        Outcome.Error(err(e.message ?: "Dados inválidos"))
    } catch (e: Exception) {
        Outcome.Error(err("Falha ao executar: ${e.message}"))
    }

    private fun err(msg: String) = buildJsonObject { put("erro", msg); put("instrucao", "Explique o problema ao usuário e pergunte o que falta. Não invente dados.") }.toString()

    /* ======================= helpers ======================= */

    private fun JsonObject.str(k: String) = this[k]?.jsonPrimitive?.contentOrNull?.trim()?.takeIf { it.isNotEmpty() }
    private fun JsonObject.num(k: String) = this[k]?.jsonPrimitive?.let { it.doubleOrNull ?: it.contentOrNull?.replace(',', '.')?.toDoubleOrNull() }
    private fun JsonObject.int(k: String) = this[k]?.jsonPrimitive?.let { it.intOrNull ?: it.contentOrNull?.toIntOrNull() }
    private fun cents(v: Double) = Math.round(v * 100)

    private fun periodOf(a: JsonObject): Period {
        val t = today()
        val s = a.str("inicio")?.let { Dates.parse(it, t) }
        val e = a.str("fim")?.let { Dates.parse(it, t) }
        if (s != null || e != null) return Period.of(PeriodKind.CUSTOM, t, s ?: e, e ?: s)
        return when (a.str("periodo")) {
            "hoje" -> Period.of(PeriodKind.TODAY, t)
            "semana" -> Period.of(PeriodKind.WEEK, t)
            "mes_anterior" -> Period.of(PeriodKind.LAST_MONTH, t)
            "ano" -> Period(PeriodKind.CUSTOM, Dates.day(t).withDayOfYear(1).toEpochDay(), t)
            "tudo" -> Period(PeriodKind.CUSTOM, 0, t + 3650)
            else -> Period.of(PeriodKind.MONTH, t)
        }
    }

    private fun <T> findByName(list: List<T>, name: String?, getName: (T) -> String, what: String): T {
        require(!name.isNullOrBlank()) { "Informe $what." }
        val n = QuickEntryParser.normalize(name)
        val exact = list.filter { QuickEntryParser.normalize(getName(it)) == n }
        if (exact.size == 1) return exact[0]
        val partial = list.filter { QuickEntryParser.normalize(getName(it)).contains(n) || n.contains(QuickEntryParser.normalize(getName(it))) }
        if (partial.size == 1) return partial[0]
        require(partial.isNotEmpty()) { "Não encontrei $what \"$name\". Opções: " + list.joinToString { getName(it) } }
        throw IllegalArgumentException("\"$name\" é ambíguo para $what. Opções: " + partial.joinToString { getName(it) })
    }

    private fun method(m: String?): String = when (QuickEntryParser.normalize(m ?: "")) {
        "pix" -> PayMethod.PIX; "dinheiro", "especie" -> PayMethod.CASH; "debito" -> PayMethod.DEBIT
        "credito", "cartao" -> PayMethod.CREDIT; "transferencia" -> PayMethod.TRANSFER; "outros", "outro" -> PayMethod.OTHER
        else -> throw IllegalArgumentException("Forma de pagamento não informada ou inválida: use pix, dinheiro, débito, crédito ou transferência.")
    }

    private fun fmtPos(c: FinanceCalc) = c.position().let { p ->
        buildJsonObject {
            put("saldo_livre_agora", Money.format(p.freeCents))
            put("livre_apos_contas_do_mes", Money.format(p.freeAfterBillsCents))
            put("dinheiro_reservado_caixinhas", Money.format(p.reservedCents))
            put("patrimonio_total", Money.format(p.patrimonyCents))
            put("dinheiro_nas_contas", Money.format(p.accountsCents))
            put("fatura_cartao_em_aberto", Money.format(p.cardDebtCents))
            put("emprestimos_a_devolver", Money.format(p.loansCents))
            put("contas_do_mes_nao_pagas", Money.format(p.unpaidBillsCents))
            put("saldo_minimo_configurado", Money.format(c.minBalance()))
            put("regra", "Saldo livre = contas − caixinhas − cartão comprometido. Dinheiro reservado NUNCA é disponível para gastar.")
        }
    }

    /* ======================= READ ======================= */

    private suspend fun read(name: String, a: JsonObject, l: Ledger): Outcome {
        val c = FinanceCalc(l, today())
        val out: JsonElement = when (name) {
            "resumo_financeiro" -> buildJsonObject {
                put("hoje", Dates.format(today()))
                put("posicao", fmtPos(c))
                val m = c.report(Period.of(PeriodKind.MONTH, today()))
                put("mes_atual", buildJsonObject {
                    put("receitas_de_trabalho_e_vendas", Money.format(m.incomeCents)); put("lucro_liquido", Money.format(m.netProfitCents))
                    put("custos_de_trabalho", Money.format(m.workCostsCents)); put("gastos_pessoais", Money.format(m.ownExpenseCents))
                    put("entradas_que_nao_sao_renda", Money.format(m.otherInCents))
                })
                put("a_receber", Money.format(c.pendingTotal()))
                put("contas", buildJsonArray { c.activeAccounts.forEach { acc -> add(buildJsonObject { put("nome", acc.name); put("saldo", Money.format(c.accountBalance(acc.id))); put("fora_das_caixinhas", Money.format(c.accountOutsideBoxes(acc.id))) }) } })
                put("alertas", buildJsonArray { Alerts.compute(c).take(5).forEach { add(JsonPrimitive(it.title + ": " + it.body)) } })
            }
            "consultar_movimentacoes" -> {
                val p = periodOf(a)
                val tipo = a.str("tipo") ?: "todos"
                val cat = a.str("categoria")?.let { QuickEntryParser.normalize(it) }
                val txt = a.str("texto")?.let { QuickEntryParser.normalize(it) }
                val lim = (a.int("limite") ?: 30).coerceIn(1, 100)
                data class Row(val id: String, val date: Long, val tipo: String, val desc: String, val cat: String, val v: Long, val work: Boolean)
                val rows = mutableListOf<Row>()
                if (tipo == "todos" || tipo == "despesa") c.expenseEvents().filter { it.date in p }.forEach { e ->
                    rows += Row(e.txnId?.let { "t$it" } ?: "p${e.purchaseId}", e.date, if (e.viaCard) "despesa_credito" else "despesa", e.description, l.category(e.categoryId)?.name ?: "", e.amountCents, e.isWorkCost)
                }
                if (tipo == "todos" || tipo == "entrada") l.txns.filter { it.type == TxnType.INCOME && it.date in p }.forEach { t ->
                    rows += Row("t${t.id}", t.date, if (c.isEarned(t)) "entrada_renda" else "entrada_nao_renda", t.description, l.source(c.sourceOf(t))?.name ?: "", t.amountCents, false)
                }
                if (tipo == "todos" || tipo == "transferencia") l.txns.filter { it.type in setOf(TxnType.TRANSFER, TxnType.CARD_PAYMENT, TxnType.LOAN_PAYMENT, TxnType.ADJUSTMENT) && it.date in p }.forEach { t ->
                    rows += Row("t${t.id}", t.date, t.type.lowercase(), t.description, "", t.amountCents, false)
                }
                val f = rows.filter { r -> (cat == null || QuickEntryParser.normalize(r.cat).contains(cat)) && (txt == null || QuickEntryParser.normalize(r.desc).contains(txt)) }
                    .sortedByDescending { it.date }
                buildJsonObject {
                    put("periodo", p.label)
                    put("quantidade", f.size)
                    put("total", Money.format(f.sumOf { it.v }))
                    put("total_ligado_a_trabalho", Money.format(f.filter { it.work }.sumOf { it.v }))
                    putJsonArray("itens") { f.take(lim).forEach { r -> add(buildJsonObject { put("id", r.id); put("data", Dates.format(r.date)); put("tipo", r.tipo); put("descricao", r.desc); put("categoria_ou_fonte", r.cat); put("valor", Money.format(r.v)); if (r.work) put("custo_de_trabalho", true) }) } }
                    if (f.size > lim) put("obs", "Mostrando $lim de ${f.size}.")
                }
            }
            "consultar_lucro" -> {
                val p = periodOf(a)
                val prof = c.profitability(p).filter { it.revenueCents != 0L || it.costsCents != 0L }
                buildJsonObject {
                    put("periodo", p.label)
                    put("faturamento_total", Money.format(prof.sumOf { it.revenueCents }))
                    put("custos_total", Money.format(prof.sumOf { it.costsCents }))
                    put("lucro_liquido_total", Money.format(prof.sumOf { it.netCents }))
                    putJsonArray("por_fonte") {
                        prof.sortedByDescending { it.netCents }.forEach { sp -> add(buildJsonObject {
                            put("fonte", sp.name); put("faturamento", Money.format(sp.revenueCents)); put("custos", Money.format(sp.costsCents))
                            put("lucro_liquido", Money.format(sp.netCents)); put("margem", sp.marginPct?.let { "$it%" } ?: "—")
                            put("horas", sp.hours); put("lucro_por_hora", sp.netPerHourCents?.let { Money.format(it) } ?: "sem horas registradas")
                            put("trabalhos", sp.jobs); put("a_receber", Money.format(sp.pendingCents))
                        }) }
                    }
                    if (prof.isEmpty()) put("obs", "Sem trabalhos ou vendas registrados neste período.")
                }
            }
            "consultar_cartao" -> {
                val card = l.mainCard ?: return Outcome.Done(err("Nenhum cartão cadastrado."))
                val k = c.currentInvoiceKey(card)
                fun inv(key: Int) = c.invoice(card, key).let { i -> buildJsonObject {
                    put("mes", Dates.monthLabel(key)); put("total", Money.format(i.totalCents)); put("pago", Money.format(i.paymentsCents)); put("falta_pagar", Money.format(i.remainingCents))
                    put("fecha", Dates.format(i.closingDate)); put("vence", i.dueDate?.let { Dates.format(it) } ?: "vencimento não configurado")
                    putJsonArray("compras") { i.items.forEach { it2 -> add(JsonPrimitive("${it2.purchase.description} ${if (it2.purchase.installments > 1) "(${it2.installment.number}/${it2.purchase.installments}) " else ""}${Money.format(it2.installment.amountCents)}")) } }
                } }
                buildJsonObject {
                    put("cartao", card.name); put("melhor_dia_compra", card.bestPurchaseDay); put("fechamento", card.closingDay)
                    put("fatura_anterior", inv(Dates.ymPlus(k, -1))); put("fatura_atual", inv(k)); put("proxima_fatura", inv(Dates.ymPlus(k, 1)))
                    put("total_em_aberto_todas_as_faturas", Money.format(c.cardDebt(card.id)))
                    put("compras_futuras_depois_da_proxima", Money.format(c.futureCommitments(card)))
                    putJsonArray("parcelamentos") { l.purchases.filter { it.cardId == card.id && it.installments > 1 }.forEach { p -> add(JsonPrimitive("${p.description}: ${p.installments}x de ${Money.format(p.totalCents / p.installments)}, faltam ${l.installments.count { it.purchaseId == p.id && it.invoiceKey > k }} depois da atual")) } }
                    card.limitCents?.let { put("limite_disponivel_do_cartao", Money.format(it - c.cardDebt(card.id))); put("aviso", "Limite do cartão NÃO é dinheiro disponível.") }
                }
            }
            "consultar_caixinhas" -> buildJsonObject {
                put("total_reservado", Money.format(c.reserved()))
                putJsonArray("caixinhas") { c.reserveBoxes.forEach { b -> val g = c.goal(b); add(buildJsonObject {
                    put("nome", b.name); put("saldo", Money.format(g.balanceCents)); put("meta", g.goalCents?.let { Money.format(it) } ?: "sem meta")
                    g.missingCents?.let { put("falta", Money.format(it)) }; g.monthsToGoal?.let { put("meses_para_meta_no_ritmo_atual", Math.ceil(it).toInt()) }
                    put("ritmo_mensal", Money.format(g.monthlyPaceCents)); g.goalNote?.let { put("obs", it) }
                }) } }
            }
            "consultar_contas_a_pagar" -> buildJsonObject {
                putJsonArray("contas") { c.billStatuses().forEach { b -> add(buildJsonObject { put("nome", b.bill.name); put("valor", Money.format(b.expectedCents)); put("vencimento", b.dueDate?.let { Dates.format(it) } ?: "sem vencimento"); put("situacao", when (b.state) { BillState.PAID -> "paga"; BillState.OVERDUE -> "atrasada"; BillState.DUE_SOON -> "vence em breve"; BillState.UPCOMING -> "a vencer"; BillState.NO_DATE -> "sem data" }) }) } }
                put("falta_pagar_no_mes", Money.format(c.billsUnpaid()))
                put("desativadas", l.bills.count { !it.active })
            }
            "relatorio" -> {
                val p = periodOf(a)
                val r = c.report(p)
                buildJsonObject {
                    put("periodo", p.label)
                    put("receitas_brutas_trabalho_e_vendas", Money.format(r.incomeCents))
                    put("faturamento_dos_trabalhos", Money.format(r.grossRevenueCents)); put("custos_dos_trabalhos", Money.format(r.profitCostsCents)); put("lucro_liquido", Money.format(r.netProfitCents))
                    put("gastos_pessoais", Money.format(r.ownExpenseCents)); put("gastos_com_transporte", Money.format(r.transportCents))
                    put("reinvestido_estoque_nfc", Money.format(r.nfcStockCents)); put("entradas_que_nao_sao_renda", Money.format(r.otherInCents))
                    putJsonArray("gastos_por_categoria") { r.expenseByCategory.forEach { add(JsonPrimitive("${l.category(it.first)?.name ?: "Sem categoria"}: ${Money.format(it.second)}")) } }
                    put("posicao_hoje", fmtPos(c))
                }
            }
            "listar_categorias_e_fontes" -> buildJsonObject {
                putJsonArray("categorias") { l.categories.filter { !it.archived }.forEach { add(JsonPrimitive(it.name)) } }
                putJsonArray("fontes_de_renda") { l.sources.filter { !it.archived }.forEach { add(JsonPrimitive(it.name + if (!it.isEarned || it.type in SourceType.notEarned) " (não é renda)" else "")) } }
                putJsonArray("contas") { l.accounts.filter { !it.archived }.forEach { add(JsonPrimitive(it.name)) } }
                putJsonArray("caixinhas") { c.reserveBoxes.forEach { add(JsonPrimitive(it.name)) } }
            }
            "confirmar_acao" -> return confirm(a.int("acao_id") ?: return Outcome.Error(err("Informe acao_id")))
            "cancelar_acao" -> return cancel(a.int("acao_id") ?: return Outcome.Error(err("Informe acao_id")))
            else -> return Outcome.Error(err("Ferramenta desconhecida"))
        }
        return Outcome.Done(out.toString())
    }

    /* ======================= WRITE: propose ======================= */

    /** Validates the arguments, builds the human summary (old -> new) and stores the action. */
    private suspend fun propose(name: String, a: JsonObject, l: Ledger, autoExecute: Boolean): Outcome {
        val c = FinanceCalc(l, today())
        val summary: String = when (name) {
            "registrar_despesa" -> {
                val v = cents(a.num("valor") ?: throw IllegalArgumentException("Informe o valor."))
                require(v > 0) { "Valor precisa ser maior que zero." }
                val cat = findByName(l.categories.filter { !it.archived }, a.str("categoria"), { it.name }, "a categoria")
                val m = method(a.str("forma_pagamento"))
                val src = a.str("fonte_trabalho")?.let { findByName(l.sources.filter { s -> !s.archived && s.isEarned }, it, { s -> s.name }, "a fonte de renda") }
                val warn = if (m != PayMethod.CREDIT) c.lowBalanceWarning(l.dailyAccount?.id, -v) else null
                "Registrar despesa de ${Money.format(v)}: ${a.str("descricao") ?: cat.name} · ${cat.name} · ${PayMethod.label(m)}" +
                    (a.int("parcelas")?.takeIf { it > 1 }?.let { " em ${it}x" } ?: "") + (src?.let { " · custo do trabalho ${it.name}" } ?: "") +
                    " · ${a.str("data") ?: "hoje"}" + (warn?.let { ". Atenção: $it" } ?: "")
            }
            "registrar_entrada" -> {
                val v = cents(a.num("valor") ?: throw IllegalArgumentException("Informe o valor."))
                require(v > 0) { "Valor precisa ser maior que zero." }
                val src = findByName(l.sources.filter { !it.archived }, a.str("fonte"), { it.name }, "a fonte")
                val earned = src.isEarned && src.type !in SourceType.notEarned
                "Registrar entrada de ${Money.format(v)} · ${src.name}" + if (earned) " (renda, dividida nas caixinhas)" else " (não é renda, não entra no lucro)"
            }
            "registrar_trabalho" -> {
                val src = findByName(l.sources.filter { !it.archived && it.type != SourceType.NFC && it.isEarned }, a.str("fonte"), { it.name }, "a fonte de renda")
                val mine = cents(a.num("valor_meu") ?: throw IllegalArgumentException("Informe quanto é seu."))
                val rec = cents(a.num("recebido") ?: throw IllegalArgumentException("Informe quanto já recebeu (0 se pendente)."))
                "Registrar trabalho em ${src.name}: ${Money.format(mine)}" + (a.num("horas")?.let { " · ${it}h" } ?: "") +
                    if (rec >= mine) " · recebido" else " · recebido ${Money.format(rec)}, pendente ${Money.format(mine - rec)}"
            }
            "registrar_emprestimo" -> {
                val v = cents(a.num("valor") ?: throw IllegalArgumentException("Informe o valor."))
                "Registrar empréstimo recebido de ${a.str("pessoa") ?: throw IllegalArgumentException("Informe quem emprestou.")}: ${Money.format(v)} (entra na conta, não é renda, fica a devolver)"
            }
            "editar_movimentacao" -> {
                val id = a.str("id") ?: throw IllegalArgumentException("Informe o id.")
                val (oldDesc, oldVal, oldDate) = describe(id, l)
                val changes = buildList {
                    a.num("valor")?.let { add("valor ${Money.format(oldVal)} → ${Money.format(cents(it))}") }
                    a.str("descricao")?.let { add("descrição \"$oldDesc\" → \"$it\"") }
                    a.str("data")?.let { add("data ${Dates.format(oldDate)} → $it") }
                    a.str("categoria")?.let { add("categoria → ${findByName(l.categories, it, { c2 -> c2.name }, "a categoria").name}") }
                }
                require(changes.isNotEmpty()) { "Diga o que mudar (valor, descrição, data ou categoria)." }
                "Editar \"$oldDesc\": " + changes.joinToString(", ")
            }
            "excluir_movimentacao" -> {
                val (d, v, dt) = describe(a.str("id") ?: throw IllegalArgumentException("Informe o id."), l)
                "Excluir \"$d\" de ${Money.format(v)} (${Dates.format(dt)})"
            }
            "criar_categoria" -> {
                val n = a.str("nome") ?: throw IllegalArgumentException("Informe o nome.")
                require(l.categories.none { QuickEntryParser.normalize(it.name) == QuickEntryParser.normalize(n) }) { "A categoria \"$n\" já existe." }
                "Criar a categoria \"$n\""
            }
            "alterar_saldo_minimo" -> "Alterar o saldo mínimo de ${Money.format(c.minBalance())} para ${Money.format(cents(a.num("valor") ?: throw IllegalArgumentException("Informe o valor.")))}"
            "alterar_meta_caixinha" -> {
                val b = findByName(c.reserveBoxes, a.str("caixinha"), { it.name }, "a caixinha")
                val nv = cents(a.num("meta") ?: throw IllegalArgumentException("Informe a meta."))
                "Alterar a meta de ${b.name} de ${b.goalCents?.let { Money.format(it) } ?: "sem meta"} para ${if (nv <= 0) "sem meta" else Money.format(nv)}"
            }
            "mover_caixinha" -> {
                val b = findByName(c.reserveBoxes, a.str("caixinha"), { it.name }, "a caixinha")
                val v = cents(a.num("valor") ?: throw IllegalArgumentException("Informe o valor."))
                require(v != 0L) { "Valor não pode ser zero." }
                if (v > 0) "Guardar ${Money.format(v)} em ${b.name} (saldo livre ${Money.format(c.position().freeCents)} → ${Money.format(c.position().freeCents - v)})"
                else "Retirar ${Money.format(-v)} de ${b.name} para o saldo livre (caixinha ${Money.format(c.boxBalance(b.id))} → ${Money.format(c.boxBalance(b.id) + v)})"
            }
            "alterar_nome_usuario" -> "Alterar o nome de \"${l.userName}\" para \"${a.str("nome") ?: throw IllegalArgumentException("Informe o nome.")}\""
            "desfazer_acao" -> {
                val act = repo.action((a.int("acao_id") ?: throw IllegalArgumentException("Informe acao_id")).toLong()) ?: throw IllegalArgumentException("Ação não encontrada.")
                require(act.status == ActionStatus.DONE) { "Só dá para desfazer ações já executadas." }
                "Desfazer: ${act.summary}"
            }
            else -> throw IllegalArgumentException("Ferramenta desconhecida")
        }
        val id = repo.saveAction(AssistantAction(tool = name, summary = summary, status = ActionStatus.PENDING, payload = a.toString()))
        return if (autoExecute) confirm(id.toInt())
        else Outcome.NeedsConfirmation(buildJsonObject {
            put("status", "aguardando_confirmacao"); put("acao_id", id); put("resumo", summary)
            put("instrucao", "Mostre o resumo e pergunte se o usuário confirma. Só chame confirmar_acao depois de um 'sim' claro dele.")
        }.toString(), id, summary)
    }

    private fun describe(id: String, l: Ledger): Triple<String, Long, Long> {
        val n = id.drop(1).toLongOrNull() ?: throw IllegalArgumentException("Id inválido: $id")
        return when (id.firstOrNull()) {
            't' -> l.txns.firstOrNull { it.id == n }?.let { Triple(it.description, it.amountCents, it.date) }
            'p' -> l.purchases.firstOrNull { it.id == n }?.let { Triple(it.description, it.totalCents, it.date) }
            else -> null
        } ?: throw IllegalArgumentException("Movimentação $id não encontrada. Consulte as movimentações para pegar o id.")
    }

    /* ======================= WRITE: execute / undo ======================= */

    suspend fun confirm(actionId: Int): Outcome {
        val act = repo.action(actionId.toLong()) ?: return Outcome.Error(err("Ação $actionId não encontrada."))
        if (act.status != ActionStatus.PENDING) return Outcome.Error(err("A ação $actionId não está pendente (situação: ${act.status})."))
        return try {
            val undo = execute(act.tool, Json.parseToJsonElement(act.payload).jsonObject, repo.snapshot())
            repo.saveAction(act.copy(status = ActionStatus.DONE, undo = undo.toString(), result = "ok"))
            Outcome.Done(buildJsonObject { put("status", "executado"); put("acao_id", act.id); put("resumo", act.summary) }.toString(), act.id)
        } catch (e: Exception) {
            repo.saveAction(act.copy(status = ActionStatus.FAILED, result = e.message))
            Outcome.Error(err("Não foi possível executar: ${e.message}"))
        }
    }

    suspend fun cancel(actionId: Int): Outcome {
        val act = repo.action(actionId.toLong()) ?: return Outcome.Error(err("Ação não encontrada."))
        if (act.status == ActionStatus.PENDING) repo.saveAction(act.copy(status = ActionStatus.CANCELLED))
        return Outcome.Done(buildJsonObject { put("status", "cancelado"); put("acao_id", act.id) }.toString())
    }

    /** Executes and returns the undo information. */
    private suspend fun execute(tool: String, a: JsonObject, l: Ledger): JsonObject {
        val c = FinanceCalc(l, today())
        val t = today()
        fun date() = a.str("data")?.let { Dates.parse(it, t) ?: throw IllegalArgumentException("Data inválida: ${a.str("data")}") } ?: t
        fun account(default: Long?) = a.str("conta")?.let { findByName(l.accounts.filter { !it.archived }, it, { x -> x.name }, "a conta").id } ?: default
        return when (tool) {
            "registrar_despesa" -> {
                val cat = findByName(l.categories.filter { !it.archived }, a.str("categoria"), { it.name }, "a categoria")
                val m = method(a.str("forma_pagamento"))
                val src = a.str("fonte_trabalho")?.let { findByName(l.sources.filter { s -> !s.archived && s.isEarned }, it, { s -> s.name }, "a fonte de renda") }
                val d = date()
                val work = src?.let { s -> l.works.filter { it.sourceId == s.id && kotlin.math.abs(it.date - d) <= 1 }.minByOrNull { kotlin.math.abs(it.date - d) } }
                val acc = account(if (m == PayMethod.CASH) l.cashAccount?.id else l.dailyAccount?.id)
                val r = repo.addExpense(FinanceRepository.ExpenseInput(cents(a.num("valor")!!), d, a.str("descricao") ?: cat.name, cat.id, m,
                    if (m == PayMethod.CREDIT) null else acc, l.mainCard?.id, a.int("parcelas") ?: 1, workId = work?.id, sourceId = src?.id))
                buildJsonObject { r.txnId?.let { put("txn", it) }; r.purchaseId?.let { put("purchase", it) } }
            }
            "registrar_entrada" -> {
                val src = findByName(l.sources.filter { !it.archived }, a.str("fonte"), { it.name }, "a fonte")
                val id = repo.addIncome(cents(a.num("valor")!!), date(), a.str("descricao") ?: src.name, account(l.dailyAccount?.id) ?: error("Sem conta"), src.id, Split.Default)
                buildJsonObject { put("txn", id) }
            }
            "registrar_trabalho" -> {
                val src = findByName(l.sources.filter { !it.archived && it.type != SourceType.NFC && it.isEarned }, a.str("fonte"), { it.name }, "a fonte de renda")
                val w = repo.saveWork(WorkEntry(sourceId = src.id, date = date(), title = a.str("descricao") ?: "", hours = a.num("horas") ?: src.defaultHours,
                    myShareCents = cents(a.num("valor_meu")!!)), cents(a.num("recebido") ?: 0.0), l.dailyAccount?.id)
                buildJsonObject { put("work", w) }
            }
            "registrar_emprestimo" -> buildJsonObject { put("loan", repo.addLoan(a.str("pessoa")!!, cents(a.num("valor")!!), date(), account(l.dailyAccount?.id) ?: error("Sem conta"))) }
            "editar_movimentacao" -> {
                val id = a.str("id")!!
                val n = id.drop(1).toLong()
                val newCat = a.str("categoria")?.let { findByName(l.categories, it, { x -> x.name }, "a categoria").id }
                if (id.startsWith("t")) {
                    val old = l.txns.first { it.id == n }
                    val nv = a.num("valor")?.let { cents(it) } ?: old.amountCents
                    val nd = a.str("data")?.let { Dates.parse(it, t) } ?: old.date
                    val ndesc = a.str("descricao") ?: old.description
                    val newTxn: Long? = when (old.type) {
                        TxnType.EXPENSE -> repo.replaceExpense(old.id, null, FinanceRepository.ExpenseInput(nv, nd, ndesc, newCat ?: old.categoryId, old.method, old.accountId,
                            boxId = old.boxId, recurring = old.recurring, notes = old.notes, billId = old.billId, billMonth = old.billMonth, nfcBatchId = old.nfcBatchId, workId = old.workId, sourceId = old.sourceId)).txnId
                        TxnType.INCOME -> repo.replaceIncome(old.id, nv, nd, ndesc, old.accountId!!, old.sourceId, if (old.distMode == "NONE") Split.None else Split.Default, old.notes)
                        else -> repo.replaceSimple(old.copy(amountCents = nv, date = nd, description = ndesc))
                    }
                    buildJsonObject { newTxn?.let { put("txn", it) }; put("restore_txn", json.encodeToJsonElement(br.com.daniel.meufinanceiro.data.Txn.serializer(), old)) }
                } else {
                    val old = l.purchases.first { it.id == n }
                    val r = repo.replaceExpense(null, old.id, FinanceRepository.ExpenseInput(a.num("valor")?.let { cents(it) } ?: old.totalCents, a.str("data")?.let { Dates.parse(it, t) } ?: old.date,
                        a.str("descricao") ?: old.description, newCat ?: old.categoryId, PayMethod.CREDIT, null, old.cardId, old.installments, old.boxId, old.recurring, old.notes, old.billId, old.billMonth, old.nfcBatchId, old.workId, old.sourceId))
                    buildJsonObject { r.purchaseId?.let { put("purchase", it) }; put("restore_purchase", json.encodeToJsonElement(br.com.daniel.meufinanceiro.data.CardPurchase.serializer(), old)) }
                }
            }
            "excluir_movimentacao" -> {
                val id = a.str("id")!!
                val n = id.drop(1).toLong()
                if (id.startsWith("t")) {
                    val old = l.txns.first { it.id == n }
                    repo.deleteTxn(n)
                    buildJsonObject { put("restore_txn", json.encodeToJsonElement(br.com.daniel.meufinanceiro.data.Txn.serializer(), old)); put("deleted", true) }
                } else {
                    val old = l.purchases.first { it.id == n }
                    repo.deletePurchase(n)
                    buildJsonObject { put("restore_purchase", json.encodeToJsonElement(br.com.daniel.meufinanceiro.data.CardPurchase.serializer(), old)); put("deleted", true) }
                }
            }
            "criar_categoria" -> buildJsonObject { put("category", repo.saveCategory(Category(name = a.str("nome")!!, color = 0xFF6250D6, sortOrder = l.categories.size))) }
            "alterar_saldo_minimo" -> {
                val old = l.setting(Keys.MIN_BALANCE)
                repo.setSetting(Keys.MIN_BALANCE, cents(a.num("valor")!!).toString())
                buildJsonObject { put("setting", Keys.MIN_BALANCE); old?.let { put("old", it) } }
            }
            "alterar_nome_usuario" -> {
                val old = l.setting(Keys.USER_NAME)
                repo.setSetting(Keys.USER_NAME, a.str("nome")!!)
                buildJsonObject { put("setting", Keys.USER_NAME); old?.let { put("old", it) } }
            }
            "alterar_meta_caixinha" -> {
                val b = findByName(c.reserveBoxes, a.str("caixinha"), { it.name }, "a caixinha")
                val nv = cents(a.num("meta")!!)
                repo.saveBox(b.copy(goalCents = nv.takeIf { it > 0 }))
                buildJsonObject { put("box", b.id); b.goalCents?.let { put("old_goal", it) } }
            }
            "mover_caixinha" -> {
                val b = findByName(c.reserveBoxes, a.str("caixinha"), { it.name }, "a caixinha")
                repo.boxDeposit(b.id, cents(a.num("valor")!!), t, "Pela assistente")
                buildJsonObject { put("box_move_box", b.id); put("box_move_value", cents(a.num("valor")!!)) }
            }
            "desfazer_acao" -> {
                val act = repo.action(a.int("acao_id")!!.toLong())!!
                undo(act)
                repo.saveAction(act.copy(status = ActionStatus.UNDONE))
                buildJsonObject { put("undone", act.id) }
            }
            else -> throw IllegalArgumentException("Ferramenta desconhecida")
        }
    }

    /** Reverts an executed action using the stored undo data. */
    suspend fun undo(act: AssistantAction) {
        val u = act.undo?.let { Json.parseToJsonElement(it).jsonObject } ?: throw IllegalArgumentException("Esta ação não pode ser desfeita.")
        val l = repo.snapshot()
        u["txn"]?.jsonPrimitive?.longOrNull?.let { repo.deleteTxn(it) }
        u["purchase"]?.jsonPrimitive?.longOrNull?.let { repo.deletePurchase(it) }
        u["work"]?.jsonPrimitive?.longOrNull?.let { repo.deleteWork(it) }
        u["loan"]?.jsonPrimitive?.longOrNull?.let { repo.deleteLoan(it) }
        u["category"]?.jsonPrimitive?.longOrNull?.let { id -> l.category(id)?.let { repo.saveCategory(it.copy(archived = true)) } }
        u["setting"]?.jsonPrimitive?.contentOrNull?.let { key -> repo.setSetting(key, u["old"]?.jsonPrimitive?.contentOrNull) }
        u["box"]?.jsonPrimitive?.longOrNull?.let { id -> l.box(id)?.let { repo.saveBox(it.copy(goalCents = u["old_goal"]?.jsonPrimitive?.longOrNull)) } }
        u["box_move_box"]?.jsonPrimitive?.longOrNull?.let { id -> repo.boxDeposit(id, -(u["box_move_value"]!!.jsonPrimitive.longOrNull!!), today(), "Desfeito") }
        u["restore_txn"]?.let { el ->
            val old = json.decodeFromJsonElement(br.com.daniel.meufinanceiro.data.Txn.serializer(), el)
            // (the edited version was already removed above through "txn"/"purchase"); recreate the original
            when (old.type) {
                TxnType.EXPENSE -> repo.addExpense(FinanceRepository.ExpenseInput(old.amountCents, old.date, old.description, old.categoryId, old.method, old.accountId, boxId = old.boxId,
                    recurring = old.recurring, notes = old.notes, billId = old.billId, billMonth = old.billMonth, nfcBatchId = old.nfcBatchId, workId = old.workId, sourceId = old.sourceId))
                TxnType.INCOME -> repo.addIncome(old.amountCents, old.date, old.description, old.accountId!!, old.sourceId, if (old.distMode == "NONE") Split.None else Split.Default, old.workId, old.nfcSaleId, old.notes, old.loanId)
                else -> repo.replaceSimple(old.copy(id = 0))
            }
        }
        u["restore_purchase"]?.let { el ->
            val old = json.decodeFromJsonElement(br.com.daniel.meufinanceiro.data.CardPurchase.serializer(), el)
            repo.addExpense(FinanceRepository.ExpenseInput(old.totalCents, old.date, old.description, old.categoryId, PayMethod.CREDIT, null, old.cardId, old.installments, old.boxId, old.recurring, old.notes, old.billId, old.billMonth, old.nfcBatchId, old.workId, old.sourceId))
        }
    }

    /** JSON schema for the Claude API tool definitions. */
    fun schemaOf(s: Spec): Map<String, Any> = mapOf(
        "properties" to s.params.mapValues { (_, p) -> buildMap<String, Any> { put("type", p.type); put("description", p.description); p.enum?.let { put("enum", it) } } },
        "required" to s.required,
    )

}
