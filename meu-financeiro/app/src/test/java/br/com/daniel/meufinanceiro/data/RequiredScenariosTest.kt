package br.com.daniel.meufinanceiro.data

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.FinanceCalc
import br.com.daniel.meufinanceiro.domain.Period
import br.com.daniel.meufinanceiro.domain.PeriodKind
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/** The 5 data scenarios Antônio asked for in the 1.1 update (the 6th, assistant, is in AssistantTest). */
@RunWith(AndroidJUnit4::class)
class RequiredScenariosTest {
    private lateinit var db: AppDatabase
    private lateinit var repo: FinanceRepository
    private val today = LocalDate.of(2026, 11, 5).toEpochDay()

    @Before fun setUp() = runBlocking {
        db = AppDatabase.inMemory(ApplicationProvider.getApplicationContext())
        repo = FinanceRepository(db)
        repo.seedIfEmpty()
    }
    @After fun tearDown() { db.close() }

    private fun l() = runBlocking { repo.snapshot() }
    private fun c() = FinanceCalc(l(), today)
    private val month get() = Period.of(PeriodKind.MONTH, today)

    /** Teste 1: conta 42, caixinhas 170 -> livre 42, reservado 170, patrimônio 212. */
    @Test fun teste1_caixinhas() = runBlocking {
        val l = l()
        val nu = l.accounts.first { it.name == "Nubank" }.id
        // Real Nubank situation: 212 in total, of which 170 is in caixinhas.
        repo.adjustBalance(nu, 0, 21200, today)
        repo.boxDeposit(l.boxOfKind(BoxKind.EMERGENCY)!!.id, 10000, today)
        repo.boxDeposit(l.boxOfKind(BoxKind.COMPUTER)!!.id, 7000, today)
        val p = c().position()
        assertEquals(4200L, p.freeCents)
        assertEquals(17000L, p.reservedCents)
        assertEquals(21200L, p.patrimonyCents)
        assertEquals(4200L, c().accountOutsideBoxes(nu))
        // Guardar reduz o livre; retirar aumenta; nenhum dos dois é receita/despesa
        repo.boxDeposit(l.boxOfKind(BoxKind.ABROAD)!!.id, 1000, today)
        assertEquals(3200L, c().position().freeCents)
        repo.boxDeposit(l.boxOfKind(BoxKind.ABROAD)!!.id, -1000, today)
        assertEquals(4200L, c().position().freeCents)
        assertEquals(21200L, c().position().patrimonyCents)
        val r = c().report(month)
        assertEquals(0L, r.incomeCents); assertEquals(0L, r.expenseCents)
    }

    /** Teste 2: Tio Rex 80, Uber 15, ônibus 5 -> lucro 60. */
    @Test fun teste2_lucroTioRex() = runBlocking {
        val l = l()
        val rex = l.sources.first { it.type == SourceType.TIO_REX }.id
        val inter = l.dailyAccount!!.id
        val transporte = l.categories.first { it.name == "Transporte" }.id
        val w = repo.saveWork(WorkEntry(sourceId = rex, date = today, hours = 4.0, myShareCents = 8000), 8000, inter,
            costs = listOf(
                FinanceRepository.ExpenseInput(1500, today, "Uber para o Tio Rex", transporte, PayMethod.PIX, inter),
                FinanceRepository.ExpenseInput(500, today, "Ônibus de volta", transporte, PayMethod.CASH, l.cashAccount!!.id),
            ))
        val c = c()
        assertEquals(6000L, c.workProfit(w))
        val p = c.profitability(month).first { it.sourceId == rex }
        assertEquals(8000L, p.revenueCents); assertEquals(2000L, p.costsCents); assertEquals(6000L, p.netCents)
        assertEquals(1500L, p.netPerHourCents) // 60 / 4h
        assertEquals(75L, p.marginPct)
        val r = c.report(month)
        assertEquals(8000L, r.incomeCents)
        assertEquals(2000L, r.workCostsCents)
        assertEquals(0L, r.ownExpenseCents)
        assertEquals(6000L, r.netProfitCents)
        // A personal Uber is NOT a work cost
        repo.addExpense(FinanceRepository.ExpenseInput(1800, today, "Uber pessoal", transporte, PayMethod.PIX, inter))
        assertEquals(6000L, c().profitability(month).first { it.sourceId == rex }.netCents)
        assertEquals(3800L, c().report(month).transportCents)
    }

    /** Teste 3: custo 14, venda 89,90 -> lucro bruto unitário 75,90. Também o lote de 10 por 140. */
    @Test fun teste3_plaquinhaNfc() = runBlocking {
        val l = l()
        val nu = l.accounts.first { it.name == "Nubank" }.id
        repo.saveBatch(NfcBatch(date = today - 5, quantity = 10, totalCostCents = 14000)) // only stock, no payment
        assertEquals(1400L, c().nfcAvgUnitCost())
        repo.saveSale(NfcSale(date = today, quantity = 1, unitPriceCents = 8990, unitCostCents = c().nfcAvgUnitCost()), 8990, nu)
        val s = c().nfcSummary(null)
        assertEquals(8990L, s.revenueCents)
        assertEquals(7590L, s.grossProfitCents) // never the full 89,90
        assertEquals(9, c().nfcStock())
        val nfc = c().profitability(month).first { it.sourceId == l.sources.first { it.type == SourceType.NFC }.id }
        assertEquals(7590L, nfc.netCents)
        assertEquals(1400L, c().boxBalance(l.boxOfKind(BoxKind.NFC_STOCK)!!.id)) // cost goes back to stock money
    }

    /** Teste 4: empréstimo de 30 da mãe: entra dinheiro, não é renda, não aumenta lucro, fica a devolver. */
    @Test fun teste4_emprestimo() = runBlocking {
        val inter = l().dailyAccount!!.id
        val loan = repo.addLoan("Mãe", 3000, today, inter)
        var c = c()
        assertEquals(3000L, c.accountBalance(inter))
        val r = c.report(month)
        assertEquals(0L, r.incomeCents)       // not earned income
        assertEquals(3000L, r.otherInCents)   // but registered as money in
        assertEquals(0L, r.netProfitCents)
        assertEquals(3000L, c.loanOutstanding(loan))
        assertEquals(0L, c.position().patrimonyCents) // 30 in the account - 30 owed
        assertEquals(0L, c.position().reservedCents)  // not split into boxes
        repo.repayLoan(loan, 1000, inter, today + 1)
        c = c()
        assertEquals(2000L, c.loanOutstanding(loan))
        assertEquals(0L, c.report(Period(PeriodKind.CUSTOM, today, today + 1)).expenseCents) // repayment is not an expense
    }

    @Test fun dinheiroDaFamiliaNaoInflaOLucro() = runBlocking {
        val l = l()
        val inter = l.dailyAccount!!.id
        val fam = l.sources.first { it.type == SourceType.FAMILY }.id
        repo.addIncome(2000, today, "Mãe para o Uber", inter, fam)
        repo.addExpense(FinanceRepository.ExpenseInput(2000, today, "Uber", l.categories.first { it.name == "Transporte" }.id, PayMethod.PIX, inter))
        val r = c().report(month)
        assertEquals(0L, r.incomeCents); assertEquals(2000L, r.otherInCents); assertEquals(0L, r.netProfitCents)
        assertEquals(0L, c().position().reservedCents) // family money is not split 50/25/25
        assertEquals(0L, c().position().freeCents)
    }

    /** Teste 5: compra de 100 no crédito -> fatura certa, conta intacta, sem duplicar ao pagar. */
    @Test fun teste5_cartao() = runBlocking {
        val l = l()
        val inter = l.dailyAccount!!.id
        val card = l.mainCard!!
        repo.adjustBalance(inter, 0, 50000, today)
        repo.addExpense(FinanceRepository.ExpenseInput(10000, today, "Compra", l.categories.first().id, PayMethod.CREDIT, null, card.id))
        var c = c()
        val key = c.currentInvoiceKey(card)
        assertEquals(202611, key) // 05/11 is before the best day (14)
        assertEquals(10000L, c.invoice(card, key).totalCents)
        assertEquals(50000L, c.accountBalance(inter))
        assertEquals(40000L, c.position().freeCents) // committed on the card
        repo.payInvoice(card.id, key, 10000, inter, today + 15)
        c = c()
        assertEquals(40000L, c.accountBalance(inter))
        assertEquals(40000L, c.position().freeCents)
        assertEquals(10000L, c.report(Period(PeriodKind.CUSTOM, today, today + 40)).expenseCents) // once
        assertTrue(c.invoice(card, key).isPaid)
        assertEquals(Dates.ym(today), key)
    }
}
