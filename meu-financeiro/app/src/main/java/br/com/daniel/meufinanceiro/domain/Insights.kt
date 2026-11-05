package br.com.daniel.meufinanceiro.domain

import br.com.daniel.meufinanceiro.data.BoxKind
import br.com.daniel.meufinanceiro.data.SourceType
import java.time.LocalDate
import kotlin.math.ceil
import kotlin.math.roundToLong

enum class Level { GOOD, INFO, WARN, ALERT }

data class Insight(val id: String, val level: Level, val title: String, val body: String, val tip: String? = null)

/**
 * The built-in financial analyst. Runs 100% on the phone, no internet, no data leaves the device.
 * Each rule looks at Daniel's real numbers and only speaks when it has something useful to say.
 */
object Analyst {

    fun analyze(calc: FinanceCalc): List<Insight> {
        val l = calc.ledger
        val out = mutableListOf<Insight>()
        val today = calc.today
        val t = Dates.day(today)
        val month = Period.of(PeriodKind.MONTH, today)
        val r = calc.report(month)
        val hasData = l.txns.isNotEmpty() || l.purchases.isNotEmpty()

        if (!hasData) {
            out += Insight("start", Level.INFO, "Vamos começar",
                "Ainda não há movimentações. Informe o saldo atual das suas contas em Configurações → Contas e registre seus gastos por algumas semanas: quanto mais dados, melhores as análises.")
            return out
        }

        // 1. Overdue bills
        val overdue = calc.billStatuses().filter { it.state == BillState.OVERDUE }
        if (overdue.isNotEmpty()) {
            out += Insight("overdue", Level.ALERT, "Conta atrasada",
                overdue.joinToString { "${it.bill.name} (${Money.format(it.expectedCents)})" } + " passou do vencimento.",
                "Pague o quanto antes para evitar juros e multa.")
        }

        // 2. Daily account below minimum
        l.dailyAccount?.let { acc ->
            val bal = calc.accountBalance(acc.id)
            if (bal < calc.minBalance()) out += Insight("lowbal", Level.ALERT, "Saldo abaixo do mínimo",
                "${acc.name} está com ${Money.format(bal)}, abaixo do seu mínimo de ${Money.format(calc.minBalance())}.",
                "Transfira um valor da caixinha Gastos pessoais ou adie gastos não essenciais.")
        }

        // 3. Card invoice vs spending money
        l.mainCard?.let { card ->
            val inv = calc.invoice(card, calc.currentInvoiceKey(card))
            val spendingBox = l.boxOfKind(BoxKind.SPENDING)
            val accounts = calc.totalAvailable()
            if (inv.remainingCents > 0 && inv.remainingCents > accounts) {
                out += Insight("card_cover", Level.ALERT, "Fatura maior que o saldo",
                    "A fatura atual do ${card.name} (${Money.format(inv.remainingCents)}) é maior do que o dinheiro nas suas contas (${Money.format(accounts)}).",
                    "Evite novas compras no crédito até a fatura fechar.")
            }
            val income = r.incomeCents
            if (income > 0 && inv.totalCents > income / 2 && spendingBox != null) {
                out += Insight("card_share", Level.WARN, "Cartão pesando no mês",
                    "A fatura atual (${Money.format(inv.totalCents)}) já passa da metade do que você recebeu este mês (${Money.format(income)}).")
            }
            val future = calc.futureCommitments(card)
            if (future > 0) out += Insight("card_future", Level.INFO, "Parcelas futuras",
                "Você já tem ${Money.format(future)} em parcelas para depois da próxima fatura. Conte com isso antes de parcelar algo novo.")
        }

        // 4. Spending pace: projected month spending vs month income
        val dayOfMonth = t.dayOfMonth
        val daysInMonth = t.lengthOfMonth()
        if (dayOfMonth >= 5 && r.personalExpenseCents > 0) {
            val projected = r.personalExpenseCents * daysInMonth / dayOfMonth
            val avg = calc.avgMonthlyPersonalExpense()
            if (avg != null && projected > avg * 120 / 100) {
                out += Insight("pace", Level.WARN, "Ritmo de gastos acima do normal",
                    "Neste ritmo, você vai gastar cerca de ${Money.format(projected)} este mês. Sua média é ${Money.format(avg)}.",
                    "Para voltar à média, o ideal é gastar até ${Money.format(((avg - r.personalExpenseCents).coerceAtLeast(0)) / (daysInMonth - dayOfMonth + 1).coerceAtLeast(1))} por dia até o fim do mês.")
            } else if (r.incomeCents > 0 && projected > r.incomeCents) {
                out += Insight("pace_income", Level.WARN, "Gastando mais do que entra",
                    "Projeção de gastos do mês: ${Money.format(projected)}. Recebido até agora: ${Money.format(r.incomeCents)}.")
            }
        }

        // 5. Category spike vs average of the previous 3 months
        val prevMonths = (1..3).map { Period.month(Dates.ymPlus(Dates.ym(today), -it.toLong())) }
        val prevReports = prevMonths.map { calc.report(it) }
        if (prevReports.count { it.expenseCents > 0 } >= 2) {
            for ((catId, value) in r.expenseByCategory) {
                val cat = l.category(catId) ?: continue
                if (cat.isNfcStock) continue
                val avg = prevReports.map { pr -> pr.expenseByCategory.firstOrNull { it.first == catId }?.second ?: 0 }.average()
                if (avg >= 3000 && value > avg * 1.3 && value - avg >= 5000) {
                    val pct = ((value - avg) / avg * 100).roundToLong()
                    out += Insight("spike_${cat.id}", Level.WARN, "${cat.name}: $pct% acima da média",
                        "Você gastou ${Money.format(value)} com ${cat.name} este mês. A média dos últimos meses é ${Money.format(avg.roundToLong())}.",
                        "Se cortar ${Money.format((value - avg.roundToLong()) / 2)} aqui, isso pode ir direto para a caixinha Sair do Brasil.")
                }
            }
        }

        // 6. Top category concentration
        val top = r.expenseByCategory.firstOrNull { l.category(it.first)?.isNfcStock != true }
        if (top != null && r.personalExpenseCents >= 10000) {
            val share = top.second * 100 / r.personalExpenseCents
            if (share >= 40) {
                val name = l.category(top.first)?.name ?: "Sem categoria"
                out += Insight("top_cat", Level.INFO, "Onde está indo seu dinheiro",
                    "$name representa $share% dos seus gastos pessoais deste mês (${Money.format(top.second)}).")
            }
        }

        // 7. Small expenses ("formiguinha")
        val small = r.expenseEvents.filter { !it.isNfcStock && it.amountCents < 3000 }
        if (small.size >= 10) {
            val sum = small.sumOf { it.amountCents }
            out += Insight("small", Level.INFO, "Gastos pequenos somados",
                "${small.size} gastos abaixo de R$ 30 este mês somaram ${Money.format(sum)}.",
                "Gastos pequenos passam despercebidos. Que tal um limite semanal para lanches e extras?")
        }

        // 8. Subscriptions
        val subsCat = l.categories.firstOrNull { QuickEntryParser.normalize(it.name) == "assinaturas" }
        val subsBills = l.bills.filter { it.active && it.categoryId == subsCat?.id }
        val subsTotal = subsBills.sumOf { it.amountCents }
        if (subsTotal > 0) {
            out += Insight("subs", Level.INFO, "Assinaturas ativas",
                "Você tem ${subsBills.size} assinatura(s) somando ${Money.format(subsTotal)}/mês, ou ${Money.format(subsTotal * 12)} por ano.",
                "Revise se está usando todas. Cancelar uma pouco usada é dinheiro garantido todo mês.")
        }

        // 9. Income per hour by source (last 90 days)
        val since = today - 90
        val recentWorks = l.works.filter { it.date >= since && it.hours > 0 }
        val perHour = recentWorks.groupBy { it.sourceId }.mapNotNull { (sid, ws) ->
            val h = ws.sumOf { it.hours }
            if (h < 4) null else Triple(l.source(sid)?.name ?: "?", ws.sumOf { it.myShareCents - it.costsCents }, h)
        }.sortedByDescending { it.second / it.third }
        if (perHour.size >= 2) {
            val best = perHour.first()
            val worst = perHour.last()
            val bestH = (best.second / best.third).roundToLong()
            val worstH = (worst.second / worst.third).roundToLong()
            if (bestH > worstH * 1.15) out += Insight("per_hour", Level.GOOD, "O que paga melhor por hora",
                "Nos últimos 90 dias, ${best.first} rendeu ${Money.format(bestH)}/h e ${worst.first} rendeu ${Money.format(worstH)}/h (já descontando custos).",
                "Quando puder escolher entre trabalhos, priorize ${best.first}.")
        }

        // 10. Income concentration
        if (r.incomeCents >= 20000 && r.incomeBySource.size >= 1) {
            val (sid, v) = r.incomeBySource.first()
            val share = v * 100 / r.incomeCents
            if (share >= 75 && r.incomeBySource.size <= 2) out += Insight("concentration", Level.INFO, "Renda concentrada",
                "$share% do que entrou este mês veio de ${l.source(sid)?.name ?: "uma fonte só"}. Com renda variável, ter mais de uma fonte protege você em meses fracos.")
        }

        // 11. Receivables
        val pend = calc.receivables()
        if (pend.isNotEmpty()) {
            val old = pend.filter { today - it.date > 15 }
            out += Insight("receivables", if (old.isNotEmpty()) Level.WARN else Level.INFO, "Dinheiro a receber",
                "Você tem ${Money.format(pend.sumOf { it.pendingCents })} a receber em ${pend.size} item(ns)" +
                    (if (old.isNotEmpty()) ", sendo ${old.size} há mais de 15 dias." else "."),
                if (old.isNotEmpty()) "Vale cobrar: " + old.take(3).joinToString { it.title } else null)
        }

        // 12. NFC business
        val nfcAll = calc.nfcSummary(null)
        val stock = calc.nfcStock()
        val nfcBox = l.boxOfKind(BoxKind.NFC_STOCK)
        if (l.batches.isNotEmpty() || l.sales.isNotEmpty()) {
            if (nfcAll.revenueCents > 0) {
                val margin = nfcAll.netProfitCents * 100 / nfcAll.revenueCents
                out += Insight("nfc_margin", if (margin >= 50) Level.GOOD else Level.INFO, "Plaquinhas NFC: margem de $margin%",
                    "Você já vendeu ${nfcAll.unitsSold} unidades, faturou ${Money.format(nfcAll.revenueCents)} e teve lucro líquido de ${Money.format(nfcAll.netProfitCents)}. Custo médio: ${Money.format(calc.nfcAvgUnitCost())}/un.",
                    "Cada plaquinha a ${Money.format(l.settingLong(Keys.NFC_PRICE, 8990))} deixa uns ${Money.format(l.settingLong(Keys.NFC_PRICE, 8990) - calc.nfcAvgUnitCost())} de lucro. Vender 3 por semana dá cerca de ${Money.format((l.settingLong(Keys.NFC_PRICE, 8990) - calc.nfcAvgUnitCost()) * 13)} por mês.")
            }
            if (stock <= 2) {
                val boxBal = nfcBox?.let { calc.boxBalance(it.id) } ?: 0
                val ref = l.settingLong(Keys.NFC_REF_BATCH_COST, 35000)
                out += Insight("nfc_stock", Level.WARN, "Estoque NFC baixo: $stock un.",
                    "A caixinha Estoque NFC tem ${Money.format(boxBal)}." +
                        if (boxBal >= ref) " Já dá para comprar um novo lote (referência ${Money.format(ref)}) sem usar dinheiro pessoal." else " Faltam ${Money.format(ref - boxBal)} para um lote de referência de ${Money.format(ref)}.")
            }
        }

        // 13. Goals forecast
        for (box in calc.activeBoxes.filter { it.kind == BoxKind.COMPUTER || it.kind == BoxKind.ABROAD || it.kind == BoxKind.OTHER }) {
            val g = calc.goal(box)
            val goal = g.goalCents ?: continue
            if (g.balanceCents >= goal) {
                out += Insight("goal_done_${box.id}", Level.GOOD, "Meta atingida: ${box.name}", "Você juntou ${Money.format(g.balanceCents)}. Parabéns!")
            } else if (g.monthsToGoal != null) {
                val months = ceil(g.monthsToGoal).toLong()
                val eta = LocalDate.ofEpochDay(today).plusMonths(months)
                val extra = 5000L // +R$ 50 por semana ≈ +R$ 217/mês
                val faster = if (g.monthlyPaceCents > 0) ceil((goal - g.balanceCents).toDouble() / (g.monthlyPaceCents + extra * 52 / 12)).toLong() else null
                out += Insight("goal_${box.id}", Level.INFO, "${box.name}: faltam ${Money.format(g.missingCents ?: 0)}",
                    "No ritmo atual (${Money.format(g.monthlyPaceCents)}/mês) você chega lá em cerca de $months mês(es), por volta de ${Dates.monthLabel(Dates.ym(eta))}.",
                    faster?.let { "Guardando R$ 50 a mais por semana, cai para uns $it mês(es)." })
            } else if (g.balanceCents < goal) {
                out += Insight("goal_${box.id}", Level.INFO, "${box.name}: faltam ${Money.format(goal - g.balanceCents)}",
                    "Ainda não há depósitos recentes nesta caixinha para calcular uma previsão.")
            }
        }

        // 14. Emergency reserve coverage
        l.boxOfKind(BoxKind.EMERGENCY)?.let { box ->
            val avg = calc.avgMonthlyPersonalExpense()
            val bal = calc.boxBalance(box.id)
            if (avg != null && avg > 0) {
                val months = bal.toDouble() / avg
                val ms = String.format(java.util.Locale.ROOT, "%.1f", months).replace('.', ',')
                out += Insight("emergency", if (months >= 3) Level.GOOD else Level.INFO, "Reserva cobre $ms mês(es)",
                    "Sua reserva de emergência (${Money.format(bal)}) cobre cerca de $ms mês(es) dos seus gastos médios (${Money.format(avg)}/mês).",
                    if (months < 3) "Uma meta comum para renda variável é de 6 meses de gastos." else null)
            }
        }

        // 15. Unallocated money
        val un = calc.unallocated()
        if (un >= 2000) out += Insight("unallocated", Level.INFO, "Dinheiro sem caixinha",
            "${Money.format(un)} nas suas contas ainda não está em nenhuma caixinha.",
            "Vá em Caixinhas → Distribuir para dar um destino a esse dinheiro.")
        else if (un <= -2000) out += Insight("unallocated_neg", Level.WARN, "Caixinhas acima do saldo real",
            "As caixinhas somam ${Money.format(-un)} a mais do que o dinheiro nas contas (descontada a fatura). Talvez algum saldo de conta esteja desatualizado.",
            "Use \"Ajustar saldo\" na conta para corrigir.")

        // 16. Savings rate this month
        if (r.incomeCents > 0 && dayOfMonth >= 10) {
            val rate = (r.incomeCents - r.personalExpenseCents) * 100 / r.incomeCents
            out += Insight("savings_rate", if (rate >= 30) Level.GOOD else if (rate >= 0) Level.INFO else Level.WARN,
                "Taxa de sobra do mês: $rate%",
                "Entrou ${Money.format(r.incomeCents)} e você gastou ${Money.format(r.personalExpenseCents)} com você mesmo (sem contar o estoque NFC).",
                if (rate < 50) "Sua regra 50/25/25 prevê guardar 50% do que entra. Ajustes pequenos no maior gasto fazem diferença." else null)
        }

        val order = listOf(Level.ALERT, Level.WARN, Level.GOOD, Level.INFO)
        return out.sortedBy { order.indexOf(it.level) }
    }
}
