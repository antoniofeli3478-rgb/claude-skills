package br.com.daniel.meufinanceiro.data

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.daniel.meufinanceiro.domain.Analyst
import br.com.daniel.meufinanceiro.domain.BillState
import br.com.daniel.meufinanceiro.domain.FinanceCalc
import br.com.daniel.meufinanceiro.domain.Ledger
import br.com.daniel.meufinanceiro.domain.Period
import br.com.daniel.meufinanceiro.domain.PeriodKind
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/** End-to-end checks of the money rules through the real Room database. */
@RunWith(AndroidJUnit4::class)
class FinanceFlowsTest {
    private lateinit var db: AppDatabase
    private lateinit var repo: FinanceRepository
    private val today = LocalDate.of(2026, 11, 5).toEpochDay()

    @Before fun setUp() = runBlocking {
        db = AppDatabase.inMemory(ApplicationProvider.getApplicationContext())
        repo = FinanceRepository(db)
        repo.seedIfEmpty()
    }

    @After fun tearDown() { db.close() }

    private fun ledger(): Ledger = runBlocking { repo.snapshot() }
    private fun calc() = FinanceCalc(ledger(), today)
    private fun Ledger.acc(name: String) = accounts.first { it.name == name }
    private fun Ledger.boxK(kind: String) = boxes.first { it.kind == kind }
    private fun Ledger.cat(name: String) = categories.first { it.name == name }
    private fun Ledger.src(type: String) = sources.first { it.type == type }

    @Test fun seedCreatesEverythingOnceAndBillsStartInactive() = runBlocking {
        repo.seedIfEmpty() // second call must not duplicate
        val l = ledger()
        assertEquals(3, l.accounts.size)
        assertEquals(13, l.categories.size)
        assertEquals(6, l.boxes.size)
        assertEquals(5, l.sources.size)
        assertEquals(10000, l.rules.sumOf { it.basisPoints })
        assertTrue(l.bills.none { it.active })
        assertNull(l.mainCard!!.dueDay)
        assertEquals(20, l.mainCard!!.closingDay); assertEquals(14, l.mainCard!!.bestPurchaseDay)
    }

    @Test fun incomeOf200IsSplit50_25_25() = runBlocking {
        val l = ledger()
        repo.addIncome(20000, today, "Tio Rex", l.acc("Banco Inter").id, l.src(SourceType.TIO_REX).id)
        val c = calc()
        assertEquals(10000L, c.boxBalance(l.boxK(BoxKind.SPENDING).id))
        assertEquals(5000L, c.boxBalance(l.boxK(BoxKind.EMERGENCY).id))
        assertEquals(5000L, c.boxBalance(l.boxK(BoxKind.ABROAD).id))
        assertEquals(20000L, c.accountBalance(l.acc("Banco Inter").id))
        assertEquals(0L, c.unallocated())
    }

    @Test fun incomeExcludedFromSplitStaysUnallocated() = runBlocking {
        val l = ledger()
        repo.addIncome(5000, today, "Presente", l.acc("Nubank").id, null, Split.None)
        assertEquals(5000L, calc().unallocated())
        assertEquals(0L, calc().totalInBoxes())
    }

    @Test fun customSplitForOneIncome() = runBlocking {
        val l = ledger()
        val pc = l.boxK(BoxKind.COMPUTER).id
        repo.addIncome(10000, today, "Bico", l.acc("Nubank").id, null, Split.Custom(listOf(pc to 10000)))
        assertEquals(10000L, calc().boxBalance(pc))
    }

    @Test fun debitExpenseReducesAccountAndSpendingBox() = runBlocking {
        val l = ledger()
        val inter = l.acc("Banco Inter").id
        repo.addIncome(20000, today, "x", inter, null)
        repo.addExpense(FinanceRepository.ExpenseInput(2500, today, "Almoço", l.cat("Alimentação").id, PayMethod.PIX, inter))
        val c = calc()
        assertEquals(17500L, c.accountBalance(inter))
        assertEquals(7500L, c.boxBalance(l.boxK(BoxKind.SPENDING).id))
        assertEquals(2500L, c.report(Period.of(PeriodKind.MONTH, today)).expenseCents)
        assertEquals(0L, c.unallocated())
    }

    @Test fun creditPurchaseGoesToInvoiceNotToBankBalance() = runBlocking {
        val l = ledger()
        val inter = l.acc("Banco Inter").id
        repo.addIncome(20000, today, "x", inter, null)
        repo.addExpense(FinanceRepository.ExpenseInput(5000, today, "Tênis", l.cat("Compras pessoais").id, PayMethod.CREDIT, null, l.mainCard!!.id))
        val c = calc()
        assertEquals(20000L, c.accountBalance(inter)) // bank untouched
        val inv = c.invoice(l.mainCard!!, c.currentInvoiceKey(l.mainCard!!))
        assertEquals(5000L, inv.totalCents)
        assertEquals(5000L, c.cardDebt())
        assertEquals(5000L, c.report(Period.of(PeriodKind.MONTH, today)).expenseCents)
        assertEquals(5000L, c.boxBalance(l.boxK(BoxKind.SPENDING).id)) // 10000 - 5000
        assertEquals(0L, c.unallocated())
    }

    @Test fun payingTheInvoiceDoesNotDuplicateExpenses() = runBlocking {
        val l = ledger()
        val inter = l.acc("Banco Inter").id
        val card = l.mainCard!!
        repo.addIncome(20000, today, "x", inter, null)
        repo.addExpense(FinanceRepository.ExpenseInput(5000, today, "Compra", l.cat("Outros").id, PayMethod.CREDIT, null, card.id))
        val key = calc().currentInvoiceKey(card)
        repo.payInvoice(card.id, key, 5000, inter, today + 1)
        val c = calc()
        assertEquals(15000L, c.accountBalance(inter))
        assertEquals(0L, c.cardDebt())
        assertTrue(c.invoice(card, key).isPaid)
        val month = Period.of(PeriodKind.MONTH, today)
        assertEquals(5000L, c.report(month).expenseCents) // still one expense of 50, not 100
        assertEquals(20000L, c.report(month).incomeCents)
        assertEquals(5000L, c.boxBalance(l.boxK(BoxKind.SPENDING).id)) // box not charged twice
        assertEquals(0L, c.unallocated())
    }

    @Test fun installments300In3xBecomeThreeInvoicesOf100() = runBlocking {
        val l = ledger()
        val card = l.mainCard!!
        repo.addExpense(FinanceRepository.ExpenseInput(30000, today, "Fone", l.cat("Compras pessoais").id, PayMethod.CREDIT, null, card.id, installments = 3))
        val c = calc()
        val k = c.currentInvoiceKey(card)
        assertEquals(10000L, c.invoice(card, k).totalCents)
        assertEquals(10000L, c.invoice(card, br.com.daniel.meufinanceiro.domain.Dates.ymPlus(k, 1)).totalCents)
        assertEquals(10000L, c.invoice(card, br.com.daniel.meufinanceiro.domain.Dates.ymPlus(k, 2)).totalCents)
        assertEquals(30000L, c.cardDebt())
        // Reports: 100 in Nov, 100 in Dec, 100 in Jan, never 300 three times.
        assertEquals(10000L, c.report(Period.month(202611)).expenseCents)
        assertEquals(10000L, c.report(Period.month(202612)).expenseCents)
        assertEquals(10000L, c.report(Period.month(202701)).expenseCents)
        assertEquals(1, ledger().purchases.size)
        assertEquals(3, ledger().installments.size)
    }

    @Test fun transferBetweenOwnAccountsIsNeitherIncomeNorExpense() = runBlocking {
        val l = ledger()
        val nu = l.acc("Nubank").id; val inter = l.acc("Banco Inter").id
        repo.addIncome(10000, today, "x", nu, null)
        repo.addTransfer(nu, inter, 4000, today)
        val c = calc()
        assertEquals(6000L, c.accountBalance(nu)); assertEquals(4000L, c.accountBalance(inter))
        assertEquals(10000L, c.totalAvailable())
        val r = c.report(Period.of(PeriodKind.MONTH, today))
        assertEquals(10000L, r.incomeCents); assertEquals(0L, r.expenseCents)
        assertEquals(0L, c.unallocated())
    }

    @Test fun boxTransfersDoNotCreateMoney() = runBlocking {
        val l = ledger()
        repo.addIncome(20000, today, "x", l.acc("Nubank").id, null)
        repo.boxTransfer(l.boxK(BoxKind.SPENDING).id, l.boxK(BoxKind.COMPUTER).id, 3000, today)
        val c = calc()
        assertEquals(20000L, c.totalInBoxes())
        assertEquals(3000L, c.boxBalance(l.boxK(BoxKind.COMPUTER).id))
        assertEquals(20000L, c.report(Period.of(PeriodKind.MONTH, today)).incomeCents)
    }

    @Test fun pendingWorkIsNotReceivedMoney() = runBlocking {
        val l = ledger()
        val rex = l.src(SourceType.TIO_REX).id
        val inter = l.acc("Banco Inter").id
        val w = repo.saveWork(WorkEntry(sourceId = rex, date = today, hours = 4.0, myShareCents = 8000))
        var c = calc()
        assertEquals(0L, c.totalAvailable())
        assertEquals(8000L, c.pendingTotal())
        assertEquals(0L, c.report(Period.of(PeriodKind.WEEK, today)).incomeCents)
        repo.addIncome(8000, today, "Tio Rex", inter, rex, workId = w)
        c = calc()
        assertEquals(0L, c.pendingTotal())
        assertEquals(8000L, c.report(Period.of(PeriodKind.WEEK, today)).incomeCents)
        assertEquals(8000L, c.report(Period.of(PeriodKind.WEEK, today)).incomeBySource.first { it.first == rex }.second)
    }

    @Test fun pipocaRecordsOnlyMyShare() = runBlocking {
        val l = ledger()
        val pip = l.src(SourceType.PIPOCA).id
        repo.saveWork(WorkEntry(sourceId = pip, date = today, title = "Festa junina", hours = 5.0, grossCents = 60000, costsCents = 2000, myShareCents = 12000),
            receivedNowCents = 10000, accountId = l.acc("Dinheiro (carteira)").id)
        val c = calc()
        assertEquals(10000L, c.report(Period.of(PeriodKind.MONTH, today)).incomeCents) // not 600
        assertEquals(2000L, c.pendingTotal())
    }

    @Test fun nfcSaleKeepsStockMoneySeparateFromProfit() = runBlocking {
        val l = ledger()
        val nu = l.acc("Nubank").id
        repo.saveBatch(NfcBatch(date = today - 10, quantity = 30, totalCostCents = 35000), PayMethod.PIX, nu)
        var c = calc()
        assertEquals(30, c.nfcStock())
        assertEquals(1166L, c.nfcAvgUnitCost())
        val saleId = repo.saveSale(NfcSale(date = today, quantity = 2, unitPriceCents = 7990, unitCostCents = c.nfcAvgUnitCost()), 15980, nu)
        c = calc()
        val stockBox = l.boxK(BoxKind.NFC_STOCK).id
        // Stock box: -350 (batch) + 23,32 (cost of 2 units back)
        assertEquals(-35000L + 2332L, c.boxBalance(stockBox))
        // Profit 136,48 split 50/25/25 -> 68,24 for personal spending
        assertEquals(6824L, c.boxBalance(l.boxK(BoxKind.SPENDING).id))
        val s = c.nfcSummary(null)
        assertEquals(15980L, s.revenueCents); assertEquals(2332L, s.costCents); assertEquals(15980L - 2332L, s.grossProfitCents)
        assertEquals(28, c.nfcStock())
        assertEquals(0L, c.saleRevenue(ledger().sale(saleId)!!) - c.saleReceived(saleId))
        // The stock purchase is reinvestment, not personal spending.
        val r = c.report(Period(PeriodKind.CUSTOM, today - 30, today))
        assertEquals(35000L, r.nfcStockCents); assertEquals(0L, r.personalExpenseCents)
    }

    @Test fun billsShowStatusAndPayingMarksThemPaid() = runBlocking {
        val l = ledger()
        val gym = l.bills.first { it.name == "Academia" }
        repo.saveBill(gym.copy(active = true, dueDay = 3))
        var c = calc()
        assertEquals(BillState.OVERDUE, c.billStatuses().first().state)
        repo.addExpense(FinanceRepository.ExpenseInput(16000, today, "Academia", gym.categoryId, PayMethod.PIX, l.acc("Banco Inter").id, billId = gym.id, billMonth = 202611, recurring = true))
        c = calc()
        assertEquals(BillState.PAID, c.billStatuses().first().state)
        assertEquals(0L, c.billsUnpaid())
    }

    @Test fun lowBalanceAlert() = runBlocking {
        val l = ledger()
        val inter = l.acc("Banco Inter").id
        repo.addIncome(5000, today, "x", inter, null)
        assertNotNull(calc().lowBalanceWarning(inter, -2500)) // 50 - 25 = 25 < 30
        assertNull(calc().lowBalanceWarning(inter, -1000))    // 40 >= 30
        assertNull(calc().lowBalanceWarning(l.acc("Nubank").id, -100000)) // not the daily account
    }

    @Test fun adjustmentIsNotIncome() = runBlocking {
        val l = ledger()
        val nu = l.acc("Nubank").id
        repo.adjustBalance(nu, 0, 150000, today)
        val c = calc()
        assertEquals(150000L, c.accountBalance(nu))
        assertEquals(0L, c.report(Period.of(PeriodKind.MONTH, today)).incomeCents)
        assertEquals(150000L, c.unallocated())
    }

    @Test fun deletingAPurchaseRemovesInstallmentsAndRefundsTheBox() = runBlocking {
        val l = ledger()
        val r = repo.addExpense(FinanceRepository.ExpenseInput(30000, today, "x", l.cat("Outros").id, PayMethod.CREDIT, null, l.mainCard!!.id, 3))
        repo.deletePurchase(r.purchaseId!!)
        val after = ledger()
        assertTrue(after.installments.isEmpty())
        assertTrue(after.moves.isEmpty())
        assertEquals(0L, calc().cardDebt())
    }

    @Test fun editingAnExpenseReplacesIt() = runBlocking {
        val l = ledger()
        val inter = l.acc("Banco Inter").id
        val r = repo.addExpense(FinanceRepository.ExpenseInput(2500, today, "Almoço", l.cat("Alimentação").id, PayMethod.PIX, inter))
        repo.replaceExpense(r.txnId, null, FinanceRepository.ExpenseInput(3000, today, "Almoço", l.cat("Alimentação").id, PayMethod.CREDIT, null, l.mainCard!!.id))
        val c = calc()
        assertEquals(0L, c.accountBalance(inter))
        assertEquals(3000L, c.cardDebt())
        assertEquals(3000L, c.report(Period.of(PeriodKind.MONTH, today)).expenseCents)
    }

    @Test fun backupRoundTripRestoresEverything() = runBlocking {
        val l = ledger()
        repo.addIncome(20000, today, "x", l.acc("Nubank").id, null)
        repo.addExpense(FinanceRepository.ExpenseInput(30000, today, "Fone", l.cat("Outros").id, PayMethod.CREDIT, null, l.mainCard!!.id, 3))
        val json = Json { ignoreUnknownKeys = true }
        val text = json.encodeToString(BackupData.serializer(), repo.exportAll())
        val before = calc()
        // wipe by restoring into a fresh DB
        val db2 = AppDatabase.inMemory(ApplicationProvider.getApplicationContext())
        val repo2 = FinanceRepository(db2)
        repo2.restoreAll(json.decodeFromString(BackupData.serializer(), text))
        val after = FinanceCalc(repo2.snapshot(), today)
        assertEquals(before.totalAvailable(), after.totalAvailable())
        assertEquals(before.cardDebt(), after.cardDebt())
        assertEquals(before.totalInBoxes(), after.totalInBoxes())
        db2.close()
    }

    @Test fun analystProducesInsightsWithoutCrashing() = runBlocking {
        val l = ledger()
        assertFalse(Analyst.analyze(calc()).isEmpty())
        repo.addIncome(20000, today, "x", l.acc("Banco Inter").id, l.src(SourceType.TIO_REX).id)
        repo.addExpense(FinanceRepository.ExpenseInput(50000, today, "x", l.cat("Lazer").id, PayMethod.CREDIT, null, l.mainCard!!.id))
        val ins = Analyst.analyze(calc())
        assertTrue(ins.any { it.id == "card_cover" || it.id == "card_share" })
    }
}
