package br.com.daniel.meufinanceiro.ai

import br.com.daniel.meufinanceiro.data.BoxKind
import br.com.daniel.meufinanceiro.domain.Analyst
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.FinanceCalc
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.domain.Period
import br.com.daniel.meufinanceiro.domain.PeriodKind
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import com.anthropic.core.JsonValue
import com.anthropic.errors.AnthropicServiceException
import com.anthropic.errors.BadRequestException
import com.anthropic.errors.UnauthorizedException
import com.anthropic.errors.RateLimitException
import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.OutputConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration

/**
 * Optional deep analysis with Claude. OFF by default. Only aggregated numbers are sent
 * (totals per category/source/month, box balances, goals) — never customer names, notes or descriptions.
 */
object ClaudeAnalyst {
    const val MODEL = "claude-opus-5-5"

    private fun system(name: String) = """Você é o consultor financeiro pessoal de $name, dentro do app privado "Meu Financeiro".
Contexto fixo de $name:
- Renda variável, sem salário fixo: recreador no Tio Rex Restaurante (turnos), recreação em festas/eventos, venda de pipoca e algodão-doce em eventos, e um pequeno negócio de plaquinhas NFC de acrílico para estabelecimentos.
- Estratégia atual: dividir cada entrada em 50% gastos pessoais, 25% reserva de emergência e 25% para sair do Brasil (tem cidadania italiana, horizonte de 3 a 5 anos).
- O dinheiro do estoque NFC é do negócio e não é lucro pessoal.
- Metas: computador para edição de vídeo, 3D e jogos (R$ 4.000 a R$ 6.000), reserva de emergência e a mudança para a Itália/Europa.
- Mantém saldo mínimo na conta do dia a dia.
Como responder:
- Português do Brasil, linguagem simples e direta, tom de amigo que entende de finanças. Sem julgamento.
- Baseie-se SOMENTE nos números enviados. Não invente valores. Se faltar dado, diga qual dado ajudaria.
- Estruture em: 1) Resumo em 2 frases; 2) O que está indo bem; 3) Onde dá para melhorar (com valores e % concretos); 4) Ideias para ganhar mais com o que ele já faz (NFC, eventos, turnos); 5) Próximos 3 passos práticos para esta semana.
- Use markdown simples (títulos curtos com ##, listas com -). Seja específico e curto: no máximo ~350 palavras.
- Não recomende produtos financeiros específicos nem investimentos de risco; pode citar conceitos gerais (reserva em aplicação de liquidez diária, por exemplo)."""

    /** Aggregated, anonymous snapshot that is sent to Claude (also shown to Daniel before sending). */
    fun buildSummary(c: FinanceCalc): String {
        val l = c.ledger
        val sb = StringBuilder()
        sb.append("Data de hoje: ${Dates.format(c.today)}\n")
        sb.append("Saldo total nas contas: ${Money.format(c.totalAvailable())}\n")
        sb.append("Fatura do cartão em aberto (total devido, incluindo parcelas futuras): ${Money.format(c.cardDebt())}\n")
        val p = c.position()
        sb.append("Saldo livre (contas − caixinhas − cartão): ${Money.format(p.freeCents)}; depois das contas do mês: ${Money.format(p.freeAfterBillsCents)}\n")
        sb.append("Dinheiro reservado nas caixinhas (NÃO disponível para gastar): ${Money.format(p.reservedCents)}; patrimônio total: ${Money.format(p.patrimonyCents)}\n")
        sb.append("Dinheiro a receber (pendente): ${Money.format(c.pendingTotal())}\n")
        sb.append("Saldo mínimo desejado na conta do dia a dia: ${Money.format(c.minBalance())}\n\nCaixinhas:\n")
        c.activeBoxes.forEach { b ->
            val g = c.goal(b)
            sb.append("- ${b.name}: ${Money.format(g.balanceCents)}" + (g.goalCents?.let { " de meta ${Money.format(it)}" } ?: " (sem meta)") +
                (if (g.monthlyPaceCents != 0L) ", ritmo ${Money.format(g.monthlyPaceCents)}/mês" else "") + "\n")
        }
        sb.append("\nÚltimos 4 meses (entradas por fonte / gastos por categoria):\n")
        for (back in 3 downTo 0) {
            val ym = Dates.ymPlus(Dates.ym(c.today), -back.toLong())
            val r = c.report(Period.month(ym))
            sb.append("## ${Dates.monthLabel(ym)}" + (if (back == 0) " (mês atual, até hoje)" else "") + "\n")
            sb.append("Receitas de trabalho/vendas: ${Money.format(r.incomeCents)} | Lucro líquido: ${Money.format(r.netProfitCents)} | Custos de trabalho: ${Money.format(r.workCostsCents)} | Gastos pessoais: ${Money.format(r.ownExpenseCents)} | Estoque NFC: ${Money.format(r.nfcStockCents)} | Entradas que não são renda: ${Money.format(r.otherInCents)}\n")
            r.incomeBySource.forEach { sb.append("  + ${l.source(it.first)?.name ?: "Outros"}: ${Money.format(it.second)}\n") }
            r.expenseByCategory.forEach { sb.append("  - ${l.category(it.first)?.name ?: "Sem categoria"}: ${Money.format(it.second)}\n") }
            r.hoursBySource.forEach { (sid, h) -> sb.append("  horas em ${l.source(sid)?.name}: ${"%.1f".format(h)}h\n") }
        }
        val nfc = c.nfcSummary(null)
        sb.append("\nNegócio NFC (desde o início): ${nfc.unitsSold} unidades vendidas, faturamento ${Money.format(nfc.revenueCents)}, custo ${Money.format(nfc.costCents)}, lucro líquido ${Money.format(nfc.netProfitCents)}, estoque atual ${c.nfcStock()} un., custo médio ${Money.format(c.nfcAvgUnitCost())}/un., preço padrão ${Money.format(l.settingLong("nfc_price_cents", 8990))}.\n")
        val bills = c.billStatuses()
        if (bills.isNotEmpty()) sb.append("\nContas fixas ativas: " + bills.joinToString { "${it.bill.name} ${Money.format(it.expectedCents)} (${it.state.name.lowercase()})" } + "\n")
        l.mainCard?.let { card ->
            val inv = c.invoice(card, c.currentInvoiceKey(card))
            sb.append("Fatura atual: ${Money.format(inv.totalCents)}, já pago ${Money.format(inv.paymentsCents)}. Parcelas futuras: ${Money.format(c.futureCommitments(card))}.\n")
        }
        val avg = c.avgMonthlyPersonalExpense()
        sb.append("Gasto pessoal médio mensal: ${avg?.let { Money.format(it) } ?: "ainda sem dados suficientes"}\n")
        sb.append("\nAlertas do analista local: " + Analyst.analyze(c).take(6).joinToString(" | ") { it.title } + "\n")
        return sb.toString()
    }

    sealed interface Result {
        data class Ok(val text: String) : Result
        data class Error(val message: String) : Result
    }

    suspend fun analyze(apiKey: String, summary: String, question: String?, name: String = "Antônio Felipe"): Result = withContext(Dispatchers.IO) {
        val client = AnthropicOkHttpClient.builder().apiKey(apiKey.trim()).timeout(Duration.ofMinutes(3)).build()
        val user = "Meus números:\n\n$summary\n\n" + (question?.takeIf { it.isNotBlank() }?.let { "Minha pergunta: $it" } ?: "Faça a análise completa.")
        fun params(withFallback: Boolean): MessageCreateParams {
            val b = MessageCreateParams.builder()
                .model(MODEL)
                .maxTokens(16000L)
                .system(system(name))
                .addUserMessage(user)
                .outputConfig(OutputConfig.builder().effort(OutputConfig.Effort.MEDIUM).build())
            if (withFallback) {
                // Server-side refusal fallback (lets another model answer if this one declines).
                b.putAdditionalHeader("anthropic-beta", "server-side-fallback-2026-07-01")
                b.putAdditionalBodyProperty("fallbacks", JsonValue.from("default"))
            }
            return b.build()
        }
        try {
            val msg = try {
                client.messages().create(params(true))
            } catch (e: BadRequestException) {
                client.messages().create(params(false))
            }
            val stop = msg.stopReason().map { it.toString() }.orElse("")
            val text = msg.content().mapNotNull { block -> block.text().orElse(null)?.text() }.joinToString("\n").trim()
            when {
                stop.contains("refusal", ignoreCase = true) && text.isBlank() -> Result.Error("A IA não respondeu a este pedido. Tente reformular a pergunta.")
                text.isBlank() -> Result.Error("A resposta veio vazia. Tente de novo.")
                else -> Result.Ok(text)
            }
        } catch (e: UnauthorizedException) {
            Result.Error("Chave de API inválida. Confira a chave em Configurações → Inteligência artificial.")
        } catch (e: RateLimitException) {
            Result.Error("Muitas solicitações agora. Espere um minuto e tente de novo.")
        } catch (e: AnthropicServiceException) {
            Result.Error("O serviço respondeu com erro (${e.statusCode()}). Tente mais tarde.")
        } catch (e: Exception) {
            Result.Error("Sem conexão com a IA. Verifique a internet. (${e.javaClass.simpleName})")
        } finally {
            runCatching { client.close() }
        }
    }
}
