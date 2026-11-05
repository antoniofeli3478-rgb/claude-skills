package br.com.daniel.meufinanceiro.domain

import br.com.daniel.meufinanceiro.data.BoxKind

/** "Como estou financeiramente?" — objective summary built only from real numbers (works offline). */
object Health {

    data class Section(val title: String, val lines: List<String>)

    fun summary(c: FinanceCalc): List<Section> {
        val l = c.ledger
        val t = c.today
        val p = c.position()
        val month = c.report(Period.of(PeriodKind.MONTH, t))
        val out = mutableListOf<Section>()

        out += Section("Agora", buildList {
            add("Você pode gastar ${Money.format(p.freeAfterBillsCents)}" + if (p.unpaidBillsCents > 0) " depois das contas do mês (livre agora: ${Money.format(p.freeCents)})." else ".")
            add("Guardado nas caixinhas: ${Money.format(p.reservedCents)} (não entra no que você pode gastar).")
            add("Patrimônio total: ${Money.format(p.patrimonyCents)}" + if (p.cardDebtCents > 0 || p.loansCents > 0) " (já descontados ${Money.format(p.cardDebtCents + p.loansCents)} de cartão e empréstimos)." else ".")
            if (p.freeCents < c.minBalance()) add("Atenção: o saldo livre está abaixo do seu mínimo de ${Money.format(c.minBalance())}.")
        })

        val hasMonthData = month.incomeCents > 0 || month.expenseCents > 0 || month.grossRevenueCents > 0
        if (hasMonthData) out += Section("Este mês", buildList {
            add("Faturou ${Money.format(month.grossRevenueCents)} com trabalhos e vendas, gastou ${Money.format(month.profitCostsCents)} para trabalhar e teve lucro líquido de ${Money.format(month.netProfitCents)}.")
            add("Gastos pessoais: ${Money.format(month.ownExpenseCents)}" + if (month.transportCents > 0) " (transporte no total: ${Money.format(month.transportCents)})." else ".")
            if (month.otherInCents > 0) add("Recebeu ${Money.format(month.otherInCents)} que não é renda (família, empréstimo, ajuda ou reembolso).")
            val top = month.expenseByCategory.firstOrNull { l.category(it.first)?.isNfcStock != true }
            if (top != null && month.personalExpenseCents > 0) add("Maior gasto: ${l.category(top.first)?.name ?: "sem categoria"} (${top.second * 100 / month.personalExpenseCents}% dos gastos).")
            val best = month.profit.filter { it.netPerHourCents != null && it.hours >= 2 }.maxByOrNull { it.netPerHourCents!! }
            if (best != null) add("Trabalho que mais rendeu por hora: ${best.name} (${Money.format(best.netPerHourCents!!)}/h líquido).")
        }) else out += Section("Este mês", listOf("Ainda não há lançamentos neste mês para analisar."))

        out += Section("Compromissos", buildList {
            l.mainCard?.let { card ->
                val inv = c.invoice(card, c.currentInvoiceKey(card))
                add("Fatura atual: ${Money.format(inv.totalCents)}" + if (inv.paymentsCents > 0) ", já pago ${Money.format(inv.paymentsCents)}." else ".")
                val fut = c.futureCommitments(card)
                if (fut > 0) add("Parcelas para depois da próxima fatura: ${Money.format(fut)}.")
            }
            if (p.unpaidBillsCents > 0) add("Contas fixas do mês ainda não pagas: ${Money.format(p.unpaidBillsCents)}.")
            val pend = c.pendingTotal()
            if (pend > 0) add("Você tem ${Money.format(pend)} para receber.")
            if (p.loansCents > 0) add("Empréstimos a devolver: ${Money.format(p.loansCents)}.")
            if (isEmpty()) add("Nenhum compromisso pendente registrado.")
        })

        val goals = c.reserveBoxes.filter { it.kind != BoxKind.NFC_STOCK }.map { c.goal(it) }.filter { it.goalCents != null }
        if (goals.isNotEmpty()) out += Section("Metas", goals.map { g ->
            "${g.box.name}: ${Money.format(g.balanceCents)} de ${Money.format(g.goalCents!!)} (${(g.progress * 100).toInt()}%)" +
                (g.monthsToGoal?.let { m -> if (m > 0) ", ~${Math.ceil(m).toInt()} mês(es) no ritmo atual" else "" } ?: ", sem ritmo recente para prever")
        })
        return out
    }

    fun asText(c: FinanceCalc): String = summary(c).joinToString("\n\n") { s -> s.title + "\n" + s.lines.joinToString("\n") { "• $it" } }
}
