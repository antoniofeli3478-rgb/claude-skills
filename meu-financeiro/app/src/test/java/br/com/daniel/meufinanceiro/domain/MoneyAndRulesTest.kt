package br.com.daniel.meufinanceiro.domain

import br.com.daniel.meufinanceiro.data.CreditCard
import br.com.daniel.meufinanceiro.data.CycleRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class MoneyAndRulesTest {

    @Test fun formatsBrazilianReais() {
        assertEquals("R$ 0,00", Money.format(0))
        assertEquals("R$ 25,00", Money.format(2500))
        assertEquals("R$ 1.234,56", Money.format(123456))
        assertEquals("-R$ 12,30", Money.format(-1230))
        assertEquals("R$ 1.000.000,00", Money.format(100_000_000))
    }

    @Test fun parsesWhatPeopleType() {
        assertEquals(2500L, Money.parse("25"))
        assertEquals(2550L, Money.parse("25,5"))
        assertEquals(2550L, Money.parse("25,50"))
        assertEquals(1890L, Money.parse("18.90"))
        assertEquals(123456L, Money.parse("1.234,56"))
        assertEquals(123400L, Money.parse("1.234"))
        assertEquals(1800L, Money.parse("R$ 18"))
        assertEquals(8990L, Money.parse("89,90"))
        assertNull(Money.parse(""))
        assertNull(Money.parse("abc"))
        assertNull(Money.parse("1,234"))
    }

    @Test fun split200Into50_25_25() {
        val parts = Distribution.split(20000, listOf(1L to 5000, 2L to 2500, 3L to 2500))
        assertEquals(listOf(1L to 10000L, 2L to 5000L, 3L to 5000L), parts)
    }

    @Test fun splitNeverLosesACent() {
        for (amount in listOf(1L, 3L, 9999L, 12345L, 100001L)) {
            val parts = Distribution.split(amount, listOf(1L to 5000, 2L to 2500, 3L to 2500))
            assertEquals(amount, parts.sumOf { it.second })
        }
        // R$ 99,99 -> 49,99 + 25,00 (24,99 + leftover goes to first) ...
        val p = Distribution.split(9999, listOf(1L to 5000, 2L to 2500, 3L to 2500)).toMap()
        assertEquals(2499L, p[2]); assertEquals(2499L, p[3]); assertEquals(5001L, p[1])
    }

    @Test fun customPercentagesWork() {
        val parts = Distribution.split(10000, listOf(1L to 7000, 2L to 3000)).toMap()
        assertEquals(7000L, parts[1]); assertEquals(3000L, parts[2])
    }

    @Test fun nfcPaymentSeparatesStockFromProfit() {
        // Sale: 2 x 89,90 = 179,80 revenue, cost 2 x 11,67 = 23,34
        val (stock, profit) = Distribution.nfcPaymentShares(17980, 17980, 2334)
        assertEquals(2334L, stock)
        assertEquals(15646L, profit)
        // Half payment -> half of the cost goes to stock
        val (s2, p2) = Distribution.nfcPaymentShares(8990, 17980, 2334)
        assertEquals(1167L, s2); assertEquals(7823L, p2)
    }

    private val inter = CreditCard(id = 1, name = "Inter", bank = "Inter", closingDay = 20, bestPurchaseDay = 14, dueDay = null)

    @Test fun purchasesBeforeBestDayGoToCurrentInvoice() {
        assertEquals(202611, CardBilling.invoiceKeyFor(LocalDate.of(2026, 11, 13), inter))
        assertEquals(202612, CardBilling.invoiceKeyFor(LocalDate.of(2026, 11, 14), inter))
        assertEquals(202701, CardBilling.invoiceKeyFor(LocalDate.of(2026, 12, 25), inter))
    }

    @Test fun closingRuleIsAlsoSupported() {
        val c = inter.copy(cycleRule = CycleRule.CLOSING)
        assertEquals(202611, CardBilling.invoiceKeyFor(LocalDate.of(2026, 11, 20), c))
        assertEquals(202612, CardBilling.invoiceKeyFor(LocalDate.of(2026, 11, 21), c))
    }

    @Test fun installmentsSpreadAcrossInvoicesWithoutTriplingTheValue() {
        val inst = CardBilling.installments(30000, 3, LocalDate.of(2026, 11, 5), inter)
        assertEquals(listOf(10000L, 10000L, 10000L), inst.map { it.second })
        assertEquals(listOf(202611, 202612, 202701), inst.map { it.third })
        assertEquals(30000L, inst.sumOf { it.second })
        assertEquals(listOf(3334L, 3333L, 3333L), CardBilling.splitInstallments(10000, 3))
    }

    @Test fun dueDateOnlyWhenConfigured() {
        assertNull(CardBilling.dueDate(202611, inter))
        assertEquals(LocalDate.of(2026, 12, 1), CardBilling.dueDate(202611, inter.copy(dueDay = 1)))
        assertEquals(LocalDate.of(2026, 11, 27), CardBilling.dueDate(202611, inter.copy(dueDay = 27)))
    }

    @Test fun warnsAboutInconsistentCardSettings() {
        assert(CardBilling.configWarning(inter) != null)
        assertNull(CardBilling.configWarning(inter.copy(bestPurchaseDay = 21)))
    }

    @Test fun periods() {
        val today = LocalDate.of(2026, 11, 5).toEpochDay() // Thursday
        val w = Period.of(PeriodKind.WEEK, today)
        assertEquals(LocalDate.of(2026, 11, 2).toEpochDay(), w.start)
        assertEquals(LocalDate.of(2026, 11, 8).toEpochDay(), w.end)
        val lm = Period.of(PeriodKind.LAST_MONTH, today)
        assertEquals(LocalDate.of(2026, 10, 1).toEpochDay(), lm.start)
        assertEquals(LocalDate.of(2026, 10, 31).toEpochDay(), lm.end)
    }
}
