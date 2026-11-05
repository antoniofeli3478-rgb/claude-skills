package br.com.daniel.meufinanceiro.domain

import br.com.daniel.meufinanceiro.data.CreditCard
import br.com.daniel.meufinanceiro.data.CycleRule
import java.time.LocalDate

/** Splitting an income between boxes ("caixinhas"). */
object Distribution {

    /**
     * Splits [amountCents] using [rules] (boxId to basis points, in display order).
     * Each share is floored; leftover cents go to the first box, so the parts always add up exactly.
     * Rules that don't sum to 100% are normalised against their own total.
     */
    fun split(amountCents: Long, rules: List<Pair<Long, Int>>): List<Pair<Long, Long>> {
        val active = rules.filter { it.second > 0 }
        if (amountCents <= 0 || active.isEmpty()) return emptyList()
        val totalBp = active.sumOf { it.second.toLong() }
        val parts = active.map { (box, bp) -> box to (amountCents * bp / totalBp) }.toMutableList()
        val rest = amountCents - parts.sumOf { it.second }
        if (rest != 0L) parts[0] = parts[0].first to (parts[0].second + rest)
        return parts.filter { it.second != 0L }
    }

    /**
     * NFC sale payment: the cost of goods part goes back to the stock box (so it can buy the next batch),
     * only the profit part is split as personal money.
     * Returns (stockShare, profitShare).
     */
    fun nfcPaymentShares(paymentCents: Long, saleRevenueCents: Long, saleCostCents: Long): Pair<Long, Long> {
        if (paymentCents <= 0) return 0L to 0L
        if (saleRevenueCents <= 0) return 0L to paymentCents
        val cost = saleCostCents.coerceIn(0, saleRevenueCents)
        val stock = (paymentCents * cost / saleRevenueCents).coerceIn(0, paymentCents)
        return stock to (paymentCents - stock)
    }
}

/** Credit card cycle and installment math. */
object CardBilling {

    /** First day of the month on which purchases already go to the NEXT invoice. */
    fun cutoffDay(card: CreditCard): Int = when (card.cycleRule) {
        CycleRule.CLOSING -> card.closingDay + 1
        else -> card.bestPurchaseDay
    }

    /**
     * Invoice key (yyyymm of the month the invoice closes) for a purchase made on [date].
     * Example with best day 14: purchase on 10/nov -> invoice "nov"; on 14/nov -> invoice "dez".
     */
    fun invoiceKeyFor(date: LocalDate, card: CreditCard): Int {
        val cutoff = cutoffDay(card)
        val base = Dates.ym(date)
        return if (cutoff in 1..date.lengthOfMonth() && date.dayOfMonth >= cutoff) Dates.ymPlus(base, 1) else base
    }

    fun closingDate(invoiceKey: Int, card: CreditCard): LocalDate = Dates.dayInMonth(invoiceKey, card.closingDay)

    /** Due date, only if Daniel configured the due day. Due day after the closing day = same month, otherwise next month. */
    fun dueDate(invoiceKey: Int, card: CreditCard): LocalDate? {
        val due = card.dueDay ?: return null
        val ym = if (due > card.closingDay) invoiceKey else Dates.ymPlus(invoiceKey, 1)
        return Dates.dayInMonth(ym, due)
    }

    /** R$ 300 in 3x -> 100, 100, 100. R$ 100 in 3x -> 33,34 + 33,33 + 33,33 (extra cent on the first). */
    fun splitInstallments(totalCents: Long, n: Int): List<Long> {
        val count = n.coerceAtLeast(1)
        val base = totalCents / count
        val rest = totalCents - base * count
        return List(count) { i -> if (i == 0) base + rest else base }
    }

    /** (number, amount, invoiceKey) for each installment of a purchase. */
    fun installments(totalCents: Long, n: Int, date: LocalDate, card: CreditCard): List<Triple<Int, Long, Int>> {
        val first = invoiceKeyFor(date, card)
        return splitInstallments(totalCents, n).mapIndexed { i, v -> Triple(i + 1, v, Dates.ymPlus(first, i.toLong())) }
    }

    /** Settings sanity warning shown in the card screen (e.g. best day 14 but closing day 20). */
    fun configWarning(card: CreditCard): String? {
        val expectedBest = if (card.closingDay >= 28) 1 else card.closingDay + 1
        return if (card.bestPurchaseDay != expectedBest)
            "Você informou melhor dia de compra ${card.bestPurchaseDay} e fechamento no dia ${card.closingDay}. " +
                "Normalmente o melhor dia é o dia seguinte ao fechamento. Confira no app do Inter. " +
                "Hoje o app usa a regra: " + (if (card.cycleRule == CycleRule.CLOSING) "compras depois do dia ${card.closingDay} vão para a próxima fatura."
                else "compras a partir do dia ${card.bestPurchaseDay} vão para a próxima fatura.")
        else null
    }
}
