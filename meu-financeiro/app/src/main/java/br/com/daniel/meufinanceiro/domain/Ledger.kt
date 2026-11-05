package br.com.daniel.meufinanceiro.domain

import br.com.daniel.meufinanceiro.data.Account
import br.com.daniel.meufinanceiro.data.Bill
import br.com.daniel.meufinanceiro.data.Box
import br.com.daniel.meufinanceiro.data.BoxKind
import br.com.daniel.meufinanceiro.data.BoxMovement
import br.com.daniel.meufinanceiro.data.CardInstallment
import br.com.daniel.meufinanceiro.data.CardPurchase
import br.com.daniel.meufinanceiro.data.Category
import br.com.daniel.meufinanceiro.data.CreditCard
import br.com.daniel.meufinanceiro.data.DistributionRule
import br.com.daniel.meufinanceiro.data.IncomeSource
import br.com.daniel.meufinanceiro.data.NfcBatch
import br.com.daniel.meufinanceiro.data.NfcCustomer
import br.com.daniel.meufinanceiro.data.NfcSale
import br.com.daniel.meufinanceiro.data.PayMethod
import br.com.daniel.meufinanceiro.data.SourceType
import br.com.daniel.meufinanceiro.data.Txn
import br.com.daniel.meufinanceiro.data.TxnType
import br.com.daniel.meufinanceiro.data.WorkEntry

/** Setting keys (table "settings"). */
object Keys {
    const val THEME = "theme"                 // system | light | dark
    const val MIN_BALANCE = "min_balance_cents"
    const val NFC_PRICE = "nfc_price_cents"
    const val NFC_PROMO_PRICE = "nfc_promo_price_cents"
    const val NFC_PROMO_MIN_QTY = "nfc_promo_min_qty"
    const val NFC_REF_UNIT_COST = "nfc_ref_unit_cost_cents"
    const val NFC_REF_BATCH_COST = "nfc_ref_batch_cost_cents"
    const val PIN_HASH = "pin_hash"
    const val PIN_SALT = "pin_salt"
    const val BIOMETRIC = "biometric"
    const val HIDE_IN_RECENTS = "hide_in_recents"
    const val AI_ENABLED = "ai_enabled"
    const val ONBOARDED = "onboarded"
    const val LAST_AUTO_BACKUP = "last_auto_backup_day"
}

/** Immutable snapshot of the whole database. All numbers in the app are derived from it. */
data class Ledger(
    val accounts: List<Account> = emptyList(),
    val cards: List<CreditCard> = emptyList(),
    val categories: List<Category> = emptyList(),
    val sources: List<IncomeSource> = emptyList(),
    val boxes: List<Box> = emptyList(),
    val rules: List<DistributionRule> = emptyList(),
    val txns: List<Txn> = emptyList(),
    val moves: List<BoxMovement> = emptyList(),
    val purchases: List<CardPurchase> = emptyList(),
    val installments: List<CardInstallment> = emptyList(),
    val works: List<WorkEntry> = emptyList(),
    val batches: List<NfcBatch> = emptyList(),
    val customers: List<NfcCustomer> = emptyList(),
    val sales: List<NfcSale> = emptyList(),
    val bills: List<Bill> = emptyList(),
    val settings: Map<String, String> = emptyMap(),
) {
    fun setting(key: String): String? = settings[key]
    fun settingLong(key: String, default: Long): Long = settings[key]?.toLongOrNull() ?: default
    fun settingInt(key: String, default: Int): Int = settings[key]?.toIntOrNull() ?: default
    fun settingBool(key: String): Boolean = settings[key] == "true"

    fun account(id: Long?) = accounts.firstOrNull { it.id == id }
    fun card(id: Long?) = cards.firstOrNull { it.id == id }
    fun category(id: Long?) = categories.firstOrNull { it.id == id }
    fun source(id: Long?) = sources.firstOrNull { it.id == id }
    fun box(id: Long?) = boxes.firstOrNull { it.id == id }
    fun boxOfKind(kind: String) = boxes.firstOrNull { it.kind == kind && !it.archived }
    fun sourceOfType(type: String) = sources.firstOrNull { it.type == type }
    fun customer(id: Long?) = customers.firstOrNull { it.id == id }
    fun work(id: Long?) = works.firstOrNull { it.id == id }
    fun sale(id: Long?) = sales.firstOrNull { it.id == id }
    fun purchase(id: Long?) = purchases.firstOrNull { it.id == id }
    fun bill(id: Long?) = bills.firstOrNull { it.id == id }
    val dailyAccount: Account? get() = accounts.firstOrNull { it.isDaily && !it.archived }
    val cashAccount: Account? get() = accounts.firstOrNull { it.kind == "CASH" && !it.archived }
    val mainCard: CreditCard? get() = cards.firstOrNull { !it.archived }

    /** Box that pays an expense of [categoryId] when none is chosen explicitly. */
    fun defaultBoxForCategory(categoryId: Long?): Box? {
        val cat = category(categoryId)
        return if (cat?.isNfcStock == true) boxOfKind(BoxKind.NFC_STOCK) else boxOfKind(BoxKind.SPENDING)
    }
}

/* ---------- Result types ---------- */

data class ExpenseEvent(
    val date: Long, val amountCents: Long, val categoryId: Long?, val description: String,
    val viaCard: Boolean, val isNfcStock: Boolean, val txnId: Long? = null, val purchaseId: Long? = null,
)

data class IncomeEvent(val date: Long, val amountCents: Long, val sourceId: Long?, val description: String, val txnId: Long)

data class InvoiceItem(val purchase: CardPurchase, val installment: CardInstallment)

data class Invoice(
    val cardId: Long, val key: Int, val items: List<InvoiceItem>, val paymentsCents: Long,
    val closingDate: Long, val dueDate: Long?,
) {
    val totalCents: Long get() = items.sumOf { it.installment.amountCents }
    val remainingCents: Long get() = (totalCents - paymentsCents).coerceAtLeast(0)
    val isPaid: Boolean get() = totalCents > 0 && paymentsCents >= totalCents
}

enum class BillState { PAID, OVERDUE, DUE_SOON, UPCOMING, NO_DATE }

data class BillStatus(
    val bill: Bill, val month: Int, val state: BillState, val dueDate: Long?,
    val paidCents: Long, val expectedCents: Long,
)

data class Receivable(val title: String, val sourceName: String, val date: Long, val expectedCents: Long, val receivedCents: Long, val workId: Long?, val saleId: Long?) {
    val pendingCents: Long get() = (expectedCents - receivedCents).coerceAtLeast(0)
}

data class NfcSummary(
    val unitsSold: Int, val revenueCents: Long, val costCents: Long, val extraCostsCents: Long,
    val receivedCents: Long, val pendingCents: Long, val stockSpentCents: Long,
) {
    /** Receita - custo das plaquinhas vendidas. */
    val grossProfitCents: Long get() = revenueCents - costCents
    /** Lucro bruto - outros custos das vendas. */
    val netProfitCents: Long get() = grossProfitCents - extraCostsCents
}

data class Report(
    val period: Period,
    val incomeCents: Long,
    val incomeBySource: List<Pair<Long?, Long>>,
    /** Everything that left: personal spending + NFC stock purchases. */
    val expenseCents: Long,
    /** Without NFC stock reinvestment. */
    val personalExpenseCents: Long,
    val nfcStockCents: Long,
    val expenseByCategory: List<Pair<Long?, Long>>,
    val nfc: NfcSummary,
    val hoursBySource: Map<Long, Double>,
    val workIncomeBySource: Map<Long, Long>,
    val expenseEvents: List<ExpenseEvent>,
    val incomeEvents: List<IncomeEvent>,
) {
    val balanceCents: Long get() = incomeCents - expenseCents
}

data class GoalInfo(
    val box: Box, val balanceCents: Long, val depositedCents: Long, val withdrawnCents: Long,
    val goalCents: Long?, val goalMaxCents: Long?, val monthlyPaceCents: Long, val monthsToGoal: Double?,
    val goalIsComputed: Boolean, val goalNote: String?,
) {
    val progress: Float get() = if (goalCents == null || goalCents <= 0) 0f else (balanceCents.toFloat() / goalCents).coerceIn(0f, 1f)
    val missingCents: Long? get() = goalCents?.let { (it - balanceCents).coerceAtLeast(0) }
}

/* ---------- Calculator ---------- */

/**
 * Pure financial calculator. Every rule from the spec lives here and is unit-tested:
 *  - transfers / card payments / adjustments are never income or expense;
 *  - credit purchases hit the invoice (and the box), not the bank balance;
 *  - paying the invoice moves money out of the bank but is not a new expense;
 *  - installments are spread over invoices and over months in reports;
 *  - pending money is never counted as received.
 */
class FinanceCalc(val ledger: Ledger, val today: Long = Dates.today()) {

    private val purchaseById = ledger.purchases.associateBy { it.id }
    private val categoryById = ledger.categories.associateBy { it.id }
    private val saleById = ledger.sales.associateBy { it.id }
    private val workById = ledger.works.associateBy { it.id }
    private val nfcSourceId = ledger.sourceOfType(SourceType.NFC)?.id

    /* ----- Accounts ----- */

    /** Effect of a transaction on account [accountId] (signed cents). */
    fun effectOn(t: Txn, accountId: Long): Long {
        var v = 0L
        when (t.type) {
            TxnType.INCOME -> if (t.accountId == accountId) v += t.amountCents
            TxnType.EXPENSE -> if (t.accountId == accountId && t.method != PayMethod.CREDIT) v -= t.amountCents
            TxnType.CARD_PAYMENT -> if (t.accountId == accountId) v -= t.amountCents
            TxnType.ADJUSTMENT -> if (t.accountId == accountId) v += t.amountCents
            TxnType.TRANSFER -> {
                if (t.accountId == accountId) v -= t.amountCents
                if (t.toAccountId == accountId) v += t.amountCents
            }
        }
        return v
    }

    fun accountBalance(accountId: Long, upTo: Long? = null): Long {
        val acc = ledger.account(accountId) ?: return 0
        return acc.initialBalanceCents + ledger.txns.asSequence()
            .filter { upTo == null || it.date <= upTo }
            .sumOf { effectOn(it, accountId) }
    }

    val activeAccounts: List<Account> get() = ledger.accounts.filter { !it.archived }.sortedBy { it.sortOrder }

    /** Saldo total disponível nas contas (não desconta a fatura). */
    fun totalAvailable(upTo: Long? = null): Long = activeAccounts.sumOf { accountBalance(it.id, upTo) }

    /* ----- Card ----- */

    fun cardDebt(cardId: Long? = null, upTo: Long? = null): Long {
        val charged = ledger.installments.asSequence()
            .filter { inst ->
                val p = purchaseById[inst.purchaseId] ?: return@filter false
                (cardId == null || p.cardId == cardId) && (upTo == null || p.date <= upTo)
            }.sumOf { it.amountCents }
        val paid = ledger.txns.asSequence()
            .filter { it.type == TxnType.CARD_PAYMENT && (cardId == null || it.cardId == cardId) && (upTo == null || it.date <= upTo) }
            .sumOf { it.amountCents }
        return (charged - paid).coerceAtLeast(0)
    }

    fun invoice(card: CreditCard, key: Int): Invoice {
        val items = ledger.installments.filter { it.invoiceKey == key }
            .mapNotNull { inst -> purchaseById[inst.purchaseId]?.takeIf { it.cardId == card.id }?.let { InvoiceItem(it, inst) } }
            .sortedWith(compareBy({ it.purchase.date }, { it.purchase.id }))
        val paid = ledger.txns.filter { it.type == TxnType.CARD_PAYMENT && it.cardId == card.id && it.invoiceKey == key }.sumOf { it.amountCents }
        return Invoice(
            card.id, key, items, paid,
            CardBilling.closingDate(key, card).toEpochDay(),
            CardBilling.dueDate(key, card)?.toEpochDay(),
        )
    }

    fun currentInvoiceKey(card: CreditCard): Int = CardBilling.invoiceKeyFor(Dates.day(today), card)

    /** Future installments after the next invoice ("compras futuras"). */
    fun futureCommitments(card: CreditCard): Long {
        val next = Dates.ymPlus(currentInvoiceKey(card), 1)
        return ledger.installments.filter { it.invoiceKey > next && purchaseById[it.purchaseId]?.cardId == card.id }.sumOf { it.amountCents }
    }

    /* ----- Boxes ----- */

    fun boxBalance(boxId: Long, upTo: Long? = null): Long =
        ledger.moves.asSequence().filter { it.boxId == boxId && (upTo == null || it.date <= upTo) }.sumOf { it.amountCents }

    fun boxDeposited(boxId: Long): Long = ledger.moves.filter { it.boxId == boxId && it.amountCents > 0 }.sumOf { it.amountCents }
    fun boxWithdrawn(boxId: Long): Long = -ledger.moves.filter { it.boxId == boxId && it.amountCents < 0 }.sumOf { it.amountCents }

    val activeBoxes: List<Box> get() = ledger.boxes.filter { !it.archived }.sortedBy { it.sortOrder }

    fun totalInBoxes(): Long = activeBoxes.sumOf { boxBalance(it.id) }

    /** Saved boxes = all except the spending box and the NFC stock box. */
    fun totalSaved(): Long = activeBoxes.filter { it.kind != BoxKind.SPENDING && it.kind != BoxKind.NFC_STOCK }.sumOf { boxBalance(it.id) }

    /**
     * Money that exists in the accounts but isn't in any box yet (initial balances, adjustments,
     * entries excluded from the split). Accounts − card debt − boxes.
     */
    fun unallocated(): Long = totalAvailable() - cardDebt() - ledger.boxes.sumOf { boxBalance(it.id) }

    /* ----- Income / receivables ----- */

    fun sourceOf(t: Txn): Long? = t.sourceId
        ?: t.workId?.let { workById[it]?.sourceId }
        ?: t.nfcSaleId?.let { nfcSourceId }

    fun workReceived(workId: Long): Long = ledger.txns.filter { it.type == TxnType.INCOME && it.workId == workId }.sumOf { it.amountCents }
    fun saleRevenue(s: NfcSale): Long = s.unitPriceCents * s.quantity
    fun saleCost(s: NfcSale): Long = s.unitCostCents * s.quantity
    fun saleReceived(saleId: Long): Long = ledger.txns.filter { it.type == TxnType.INCOME && it.nfcSaleId == saleId }.sumOf { it.amountCents }

    fun receivables(): List<Receivable> {
        val w = ledger.works.map { work ->
            Receivable(
                work.title.ifBlank { ledger.source(work.sourceId)?.name ?: "Trabalho" },
                ledger.source(work.sourceId)?.name ?: "", work.date, work.myShareCents, workReceived(work.id), work.id, null,
            )
        }
        val s = ledger.sales.map { sale ->
            val c = ledger.customer(sale.customerId)
            Receivable(
                "Venda NFC" + (c?.let { " · " + (it.business.ifBlank { it.name }) } ?: "") + " (${sale.quantity} un.)",
                "Plaquinhas NFC", sale.date, saleRevenue(sale), saleReceived(sale.id), null, sale.id,
            )
        }
        return (w + s).filter { it.pendingCents > 0 }.sortedBy { it.date }
    }

    fun pendingTotal(): Long = receivables().sumOf { it.pendingCents }

    /* ----- NFC ----- */

    fun nfcStock(): Int = ledger.batches.sumOf { it.quantity } - ledger.sales.sumOf { it.quantity }

    /** Weighted average cost per unit of all batches bought up to [date]; falls back to the reference cost setting. */
    fun nfcAvgUnitCost(date: Long = today): Long {
        val bs = ledger.batches.filter { it.date <= date }
        val qty = bs.sumOf { it.quantity }
        return if (qty > 0) bs.sumOf { it.totalCostCents } / qty else ledger.settingLong(Keys.NFC_REF_UNIT_COST, 1167)
    }

    fun nfcSummary(period: Period?): NfcSummary {
        val sales = ledger.sales.filter { period == null || it.date in period }
        val stockSpent = expenseEvents().filter { it.isNfcStock && (period == null || it.date in period) }.sumOf { it.amountCents }
        val received = ledger.txns.filter { it.type == TxnType.INCOME && it.nfcSaleId != null && (period == null || it.date in period) }.sumOf { it.amountCents }
        val pending = ledger.sales.filter { period == null || it.date in period }.sumOf { (saleRevenue(it) - saleReceived(it.id)).coerceAtLeast(0) }
        return NfcSummary(
            unitsSold = sales.sumOf { it.quantity },
            revenueCents = sales.sumOf { saleRevenue(it) },
            costCents = sales.sumOf { saleCost(it) },
            extraCostsCents = sales.sumOf { it.extraCostsCents },
            receivedCents = received,
            pendingCents = pending,
            stockSpentCents = stockSpent,
        )
    }

    /* ----- Expenses / income events ----- */

    /** Non-credit expenses on their date + each card installment on its month (purchase date + n-1 months). */
    fun expenseEvents(): List<ExpenseEvent> {
        val direct = ledger.txns.filter { it.type == TxnType.EXPENSE && it.method != PayMethod.CREDIT }.map {
            ExpenseEvent(it.date, it.amountCents, it.categoryId, it.description, false, categoryById[it.categoryId]?.isNfcStock == true, txnId = it.id)
        }
        val card = ledger.installments.mapNotNull { inst ->
            val p = purchaseById[inst.purchaseId] ?: return@mapNotNull null
            val date = Dates.day(p.date).plusMonths((inst.number - 1).toLong()).toEpochDay()
            val desc = if (p.installments > 1) "${p.description} (${inst.number}/${p.installments})" else p.description
            ExpenseEvent(date, inst.amountCents, p.categoryId, desc, true, categoryById[p.categoryId]?.isNfcStock == true, purchaseId = p.id)
        }
        return direct + card
    }

    fun incomeEvents(): List<IncomeEvent> = ledger.txns.filter { it.type == TxnType.INCOME }
        .map { IncomeEvent(it.date, it.amountCents, sourceOf(it), it.description, it.id) }

    fun report(period: Period): Report {
        val inc = incomeEvents().filter { it.date in period }
        val exp = expenseEvents().filter { it.date in period }
        val works = ledger.works.filter { it.date in period }
        return Report(
            period = period,
            incomeCents = inc.sumOf { it.amountCents },
            incomeBySource = inc.groupBy { it.sourceId }.map { (k, v) -> k to v.sumOf { it.amountCents } }.sortedByDescending { it.second },
            expenseCents = exp.sumOf { it.amountCents },
            personalExpenseCents = exp.filter { !it.isNfcStock }.sumOf { it.amountCents },
            nfcStockCents = exp.filter { it.isNfcStock }.sumOf { it.amountCents },
            expenseByCategory = exp.groupBy { it.categoryId }.map { (k, v) -> k to v.sumOf { it.amountCents } }.sortedByDescending { it.second },
            nfc = nfcSummary(period),
            hoursBySource = works.groupBy { it.sourceId }.mapValues { (_, v) -> v.sumOf { it.hours } },
            workIncomeBySource = works.groupBy { it.sourceId }.mapValues { (_, v) -> v.sumOf { it.myShareCents } },
            expenseEvents = exp.sortedByDescending { it.date },
            incomeEvents = inc.sortedByDescending { it.date },
        )
    }

    /* ----- Bills ----- */

    fun billStatuses(month: Int = Dates.ym(today)): List<BillStatus> = ledger.bills.filter { it.active }.sortedBy { it.dueDay ?: 99 }.map { bill ->
        val paidTx = ledger.txns.filter { it.billId == bill.id && it.billMonth == month }.sumOf { it.amountCents }
        val paidCard = ledger.purchases.filter { it.billId == bill.id && it.billMonth == month }.sumOf { it.totalCents }
        val paid = paidTx + paidCard
        val due = bill.dueDay?.let { Dates.dayInMonth(month, it).toEpochDay() }
        val state = when {
            paid > 0 -> BillState.PAID
            due == null -> BillState.NO_DATE
            due < today -> BillState.OVERDUE
            due - today <= 5 -> BillState.DUE_SOON
            else -> BillState.UPCOMING
        }
        BillStatus(bill, month, state, due, paid, bill.amountCents)
    }

    fun billsMonthTotal(month: Int = Dates.ym(today)): Long = billStatuses(month).sumOf { maxOf(it.expectedCents, it.paidCents) }
    fun billsUnpaid(month: Int = Dates.ym(today)): Long = billStatuses(month).filter { it.state != BillState.PAID }.sumOf { it.expectedCents }

    /* ----- "Quanto posso gastar" ----- */

    /**
     * Livre para gastar = saldo da caixinha Gastos pessoais − contas pessoais do mês ainda não pagas.
     * (Compras no cartão já saíram da caixinha quando foram feitas, então a fatura não é descontada de novo.)
     */
    fun freeToSpend(): Long {
        val spending = ledger.boxOfKind(BoxKind.SPENDING) ?: return 0
        val unpaidPersonal = billStatuses().filter { it.state != BillState.PAID && categoryById[it.bill.categoryId]?.isNfcStock != true }.sumOf { it.expectedCents }
        return boxBalance(spending.id) - unpaidPersonal
    }

    /* ----- Net worth / series ----- */

    /** Patrimônio financeiro = contas − dívida do cartão. */
    fun netWorth(upTo: Long? = null): Long = totalAvailable(upTo) - cardDebt(upTo = upTo)

    /** Net worth at the end of each of the last [n] months (including the current one). */
    fun netWorthByMonth(n: Int = 6): List<Pair<Int, Long>> {
        val cur = Dates.ym(today)
        return (n - 1 downTo 0).map { back ->
            val ym = Dates.ymPlus(cur, -back.toLong())
            val end = minOf(Dates.ymToYearMonth(ym).atEndOfMonth().toEpochDay(), today)
            ym to netWorth(end)
        }
    }

    /** Net worth sampled across a period (max ~12 points). */
    fun netWorthSeries(period: Period): List<Pair<Long, Long>> {
        val end = minOf(period.end, today)
        if (end < period.start) return emptyList()
        val span = (end - period.start).coerceAtLeast(0)
        val steps = minOf(span, 11L).toInt()
        if (steps == 0) return listOf(end to netWorth(end))
        return (0..steps).map { i -> val d = period.start + span * i / steps; d to netWorth(d) }
    }

    /** Income vs expense buckets: days for short periods, weeks for a month, months for long ones. */
    fun incomeVsExpense(period: Period): List<Triple<String, Long, Long>> {
        val inc = incomeEvents().filter { it.date in period }
        val exp = expenseEvents().filter { it.date in period }
        return when {
            period.days <= 10 -> (period.start..period.end).map { d ->
                Triple(if (period.days == 1) "Hoje" else Dates.weekdayShort(d), inc.filter { it.date == d }.sumOf { it.amountCents }, exp.filter { it.date == d }.sumOf { it.amountCents })
            }
            period.days <= 45 -> {
                val buckets = mutableListOf<Triple<String, Long, Long>>()
                var s = period.start
                while (s <= period.end) {
                    val e = minOf(s + 6, period.end)
                    buckets += Triple(Dates.formatShort(s), inc.filter { it.date in s..e }.sumOf { it.amountCents }, exp.filter { it.date in s..e }.sumOf { it.amountCents })
                    s = e + 1
                }
                buckets
            }
            else -> {
                val first = Dates.ym(period.start)
                val last = Dates.ym(period.end)
                (0..Dates.ymMinus(last, first)).map { i ->
                    val ym = Dates.ymPlus(first, i.toLong())
                    Triple(Dates.monthShortLabel(ym), inc.filter { Dates.ym(it.date) == ym }.sumOf { it.amountCents }, exp.filter { Dates.ym(it.date) == ym }.sumOf { it.amountCents })
                }
            }
        }
    }

    fun boxBalanceByMonth(boxId: Long, n: Int = 6): List<Pair<Int, Long>> {
        val cur = Dates.ym(today)
        return (n - 1 downTo 0).map { back ->
            val ym = Dates.ymPlus(cur, -back.toLong())
            ym to boxBalance(boxId, minOf(Dates.ymToYearMonth(ym).atEndOfMonth().toEpochDay(), today))
        }
    }

    /* ----- Goals ----- */

    /** Average monthly personal spending over complete months with data (needs at least [minMonths]). */
    fun avgMonthlyPersonalExpense(minMonths: Int = 2, maxMonths: Int = 6): Long? {
        val events = expenseEvents().filter { !it.isNfcStock && it.date <= today }
        val firstDate = (events.map { it.date } + ledger.txns.map { it.date }).minOrNull() ?: return null
        val firstFullMonth = Dates.ymPlus(Dates.ym(firstDate), if (Dates.day(firstDate).dayOfMonth == 1) 0 else 1)
        val lastFullMonth = Dates.ymPlus(Dates.ym(today), -1)
        val available = Dates.ymMinus(lastFullMonth, firstFullMonth) + 1
        if (available < minMonths) return null
        val months = minOf(available, maxMonths)
        val from = Dates.ymPlus(lastFullMonth, -(months - 1).toLong())
        val total = events.filter { Dates.ym(it.date) in from..lastFullMonth }.sumOf { it.amountCents }
        return total / months
    }

    /** Average monthly net inflow into a box over the last 90 days (for the forecast). */
    fun boxMonthlyPace(boxId: Long): Long {
        val from = today - 90
        val firstMove = ledger.moves.filter { it.boxId == boxId }.minOfOrNull { it.date } ?: return 0
        val start = maxOf(from, firstMove)
        val days = (today - start + 1).coerceAtLeast(30)
        val net = ledger.moves.filter { it.boxId == boxId && it.date >= start && it.date <= today }.sumOf { it.amountCents }
        return net * 30 / days
    }

    fun goal(box: Box): GoalInfo {
        val balance = boxBalance(box.id)
        var goal = box.goalCents
        var computed = false
        var note: String? = null
        if (box.kind == BoxKind.EMERGENCY && box.goalCents == null && box.emergencyMonths != null) {
            val avg = avgMonthlyPersonalExpense()
            if (avg != null && avg > 0) {
                goal = avg * box.emergencyMonths
                computed = true
                note = "${box.emergencyMonths} meses × gasto médio de ${Money.format(avg)}/mês"
            } else {
                note = "Ainda não há dados suficientes. Registre seus gastos por pelo menos 2 meses completos."
            }
        }
        val pace = boxMonthlyPace(box.id)
        val months = if (goal != null && goal > balance && pace > 0) (goal - balance).toDouble() / pace else if (goal != null && balance >= goal) 0.0 else null
        return GoalInfo(box, balance, boxDeposited(box.id), boxWithdrawn(box.id), goal, box.goalMaxCents, pace, months, computed, note)
    }

    /* ----- Alerts ----- */

    fun minBalance(): Long = ledger.settingLong(Keys.MIN_BALANCE, 3000)

    /** Returns a warning if moving [deltaCents] in [accountId] puts the daily account below the minimum. */
    fun lowBalanceWarning(accountId: Long?, deltaCents: Long): String? {
        val daily = ledger.dailyAccount ?: return null
        if (accountId != daily.id || deltaCents >= 0) return null
        val after = accountBalance(daily.id) + deltaCents
        val min = minBalance()
        return if (after < min) "Com isso, o saldo de ${daily.name} fica em ${Money.format(after)}, abaixo do mínimo de ${Money.format(min)}." else null
    }
}
