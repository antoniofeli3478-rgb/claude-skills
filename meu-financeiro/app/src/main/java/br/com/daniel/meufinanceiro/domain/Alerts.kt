package br.com.daniel.meufinanceiro.domain

import br.com.daniel.meufinanceiro.data.SourceType

/**
 * Smart alerts. Each one needs real data (and enough history when it compares), so it never
 * "guesses". The user can turn each alert off (Keys.ALERTS_OFF) and choose notifications.
 */
object Alerts {

    data class Alert(val id: String, val level: Level, val title: String, val body: String, val route: String? = null)

    /** id -> label for the settings screen. */
    val catalog = linkedMapOf(
        "free_low" to "Saldo livre abaixo do mínimo",
        "reserved_mix" to "Dinheiro reservado maior que o saldo do banco",
        "invoice_due" to "Fatura perto do vencimento",
        "bill_due" to "Contas atrasadas ou vencendo",
        "pending" to "Pagamentos pendentes para receber",
        "transport_week" to "Gasto alto com transporte na semana",
        "food_up" to "Aumento do gasto com alimentação",
        "work_no_costs" to "Trabalho registrado sem custos de transporte",
        "loan_due" to "Empréstimo para devolver",
    )

    fun compute(c: FinanceCalc, off: Set<String> = c.ledger.setting(Keys.ALERTS_OFF)?.split(',')?.toSet() ?: emptySet()): List<Alert> {
        val l = c.ledger
        val t = c.today
        val out = mutableListOf<Alert>()
        val pos = c.position()

        if ("free_low" !in off && pos.freeCents < c.minBalance())
            out += Alert("free_low", Level.ALERT, "Saldo livre baixo",
                "Seu saldo livre está em ${Money.format(pos.freeCents)}, abaixo do limite de ${Money.format(c.minBalance())}.", "home")

        if ("reserved_mix" !in off) {
            val bad = c.activeAccounts.filter { c.boxesInAccount(it.id) > 0 && c.accountOutsideBoxes(it.id) < 0 }
            if (bad.isNotEmpty()) out += Alert("reserved_mix", Level.WARN, "Caixinhas maiores que o saldo do banco",
                bad.joinToString { "${it.name}: as caixinhas somam ${Money.format(c.boxesInAccount(it.id))}, mas a conta tem ${Money.format(c.accountBalance(it.id))} no total" } +
                    ". Pode ser dinheiro reservado sendo usado como livre, ou um saldo desatualizado.", "settings/accounts")
        }

        if ("invoice_due" !in off) l.mainCard?.let { card ->
            // the invoice that is due next: current key or the previous one (already closed)
            listOf(Dates.ymPlus(c.currentInvoiceKey(card), -1), c.currentInvoiceKey(card)).map { c.invoice(card, it) }
                .firstOrNull { it.dueDate != null && it.dueDate >= t && it.dueDate - t <= 3 && it.remainingCents > 0 }
                ?.let { inv -> out += Alert("invoice_due", Level.WARN, "Fatura vence ${Dates.relative(inv.dueDate!!, t).lowercase()}",
                    "Faltam ${Money.format(inv.remainingCents)} da fatura de ${Dates.monthLabel(inv.key)}.", "card") }
        }

        if ("bill_due" !in off) {
            val st = c.billStatuses()
            val overdue = st.filter { it.state == BillState.OVERDUE }
            val soon = st.filter { it.state == BillState.DUE_SOON }
            if (overdue.isNotEmpty()) out += Alert("bill_due", Level.ALERT, "Conta atrasada", overdue.joinToString { "${it.bill.name} (${Money.format(it.expectedCents)})" }, "bills")
            else if (soon.isNotEmpty()) out += Alert("bill_due", Level.INFO, "Contas vencendo", soon.joinToString { "${it.bill.name} ${it.dueDate?.let { d -> Dates.relative(d, t).lowercase() }}" }, "bills")
        }

        if ("pending" !in off) {
            val old = c.receivables().filter { t - it.date >= 3 }
            if (old.isNotEmpty()) out += Alert("pending", Level.INFO, "Pagamento pendente",
                "Você tem ${Money.format(old.sumOf { it.pendingCents })} para receber (${old.take(2).joinToString { it.title }}${if (old.size > 2) "…" else ""}).", "receive")
        }

        if ("transport_week" !in off) {
            val week = Period.of(PeriodKind.WEEK, t)
            val now = c.report(week).transportCents
            // needs at least 4 previous weeks with any expense registered
            val prev = (1..8).map { k -> Period(PeriodKind.CUSTOM, week.start - 7L * k, week.end - 7L * k) }
            val withData = prev.filter { p -> c.expenseEvents().any { it.date in p } }
            if (withData.size >= 4 && now > 0) {
                val avg = withData.sumOf { c.report(it).transportCents } / withData.size
                if (avg > 0 && now > avg * 150 / 100 && now - avg >= 2000)
                    out += Alert("transport_week", Level.INFO, "Transporte alto nesta semana",
                        "Você gastou ${Money.format(now)} com transporte nesta semana. Sua média semanal é ${Money.format(avg)}.", "reports")
            }
        }

        if ("food_up" !in off) {
            val month = Period.of(PeriodKind.MONTH, t)
            val day = Dates.day(t).dayOfMonth
            if (day >= 7) {
                fun food(r: Report) = r.expenseByCategory.filter { QuickEntryParser.normalize(l.category(it.first)?.name ?: "").startsWith("alimenta") }.sumOf { it.second }
                val prevs = (1..3).map { c.report(Period.month(Dates.ymPlus(Dates.ym(t), -it.toLong()))) }.filter { it.expenseCents > 0 }
                if (prevs.size >= 2) {
                    val avg = prevs.sumOf { food(it) } / prevs.size
                    val projected = food(c.report(month)) * Dates.day(t).lengthOfMonth() / day
                    if (avg >= 3000 && projected > avg * 130 / 100)
                        out += Alert("food_up", Level.INFO, "Alimentação subindo",
                            "Neste ritmo, alimentação vai fechar o mês em ${Money.format(projected)}. A média é ${Money.format(avg)}.", "reports")
                }
            }
        }

        if ("work_no_costs" !in off) {
            val recent = l.works.filter { t - it.date in 0..2 && l.source(it.sourceId)?.type in setOf(SourceType.TIO_REX, SourceType.RECREACAO, SourceType.PIPOCA) }
                .filter { w -> w.costsCents == 0L && c.expenseEvents().none { it.workId == w.id } }
            if (recent.isNotEmpty()) out += Alert("work_no_costs", Level.INFO, "Trabalho sem custos registrados",
                "Você registrou ${recent.size} trabalho(s) recente(s) sem custos (ex.: ${l.source(recent.first().sourceId)?.name} em ${Dates.relative(recent.first().date, t).lowercase()}). Se pagou transporte, registre para o lucro ficar certo.", "income")
        }

        if ("loan_due" !in off) {
            val due = l.loans.filter { c.loanOutstanding(it.id) > 0 && it.dueDate != null && it.dueDate - t <= 3 }
            if (due.isNotEmpty()) out += Alert("loan_due", Level.WARN, "Empréstimo para devolver",
                due.joinToString { "${it.person}: ${Money.format(c.loanOutstanding(it.id))}" }, "loans")
        }

        val order = listOf(Level.ALERT, Level.WARN, Level.INFO, Level.GOOD)
        return out.sortedBy { order.indexOf(it.level) }
    }
}
