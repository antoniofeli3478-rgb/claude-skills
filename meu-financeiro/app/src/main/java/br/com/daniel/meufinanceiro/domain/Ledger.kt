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
import br.com.daniel.meufinanceiro.data.Loan
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
    // v2
    const val DATA_VERSION = "data_version"
    const val USER_NAME = "user_name"
    const val REVIEW_BALANCES = "review_balances"
    const val ASSISTANT_AUTO_SIMPLE = "assistant_auto_simple"
    const val ALERTS_OFF = "alerts_off"            // comma-separated alert ids turned off
    const val ALERT_NOTIFY = "alert_notify"        // "true" = Android notifications (max 1/day)
    const val ALERT_LAST_NOTIFIED = "alert_last_notified"
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
    val loans: List<Loan> = emptyList(),
) {
    val userName: String get() = settings[Keys.USER_NAME]?.takeIf { it.isNotBlank() } ?: "Antônio Felipe"
    fun loan(id: Long?) = loans.firstOrNull { it.id == id }
    fun isEarnedSource(id: Long?): Boolean = source(id)?.let { it.isEarned && it.type !in SourceType.notEarned } ?: true
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

    /**
     * Box that pays an expense of [categoryId] when none is chosen explicitly.
     * v2: personal spending comes from the FREE balance (no box); NFC stock comes from the Estoque NFC box.
     */
    fun defaultBoxForCategory(categoryId: Long?): Box? {
        val cat = category(categoryId)
        return if (cat?.isNfcStock == true) boxOfKind(BoxKind.NFC_STOCK) else null
    }
}

/* ---------- Result types ---------- */

data class ExpenseEvent(
    val date: Long, val amountCents: Long, val categoryId: Long?, val description: String,
    val viaCard: Boolean, val isNfcStock: Boolean, val txnId: Long? = null, val purchaseId: Long? = null,
    /** v2: cost of a job (work entry) or of an income source. */
    val workId: Long? = null, val sourceId: Long? = null,
) {
    val isWorkCost: Boolean get() = workId != null || sourceId != null
}

/** Rentabilidade de uma fonte de renda num período. */
data class SourceProfit(
    val sourceId: Long, val name: String, val revenueCents: Long, val costsCents: Long, val hours: Double,
    val receivedCents: Long, val pendingCents: Long, val jobs: Int,
) {
    val netCents: Long get() = revenueCents - costsCents
    /** Margem em % (0..100), null sem faturamento. */
    val marginPct: Long? get() = if (revenueCents > 0) netCents * 100 / revenueCents else null
    val netPerHourCents: Long? get() = if (hours > 0) (netCents / hours).toLong() else null
}

/** Os três números que nunca podem se misturar. */
data class MoneyPosition(
    /** Todo o dinheiro nas contas (inclui o que está guardado em caixinhas do banco). */
    val accountsCents: Long,
    /** Dinheiro das caixinhas (reserva, sair do Brasil, computador, estoque NFC, metas). */
    val reservedCents: Long,
    /** Fatura do cartão ainda não paga (todas as parcelas). */
    val cardDebtCents: Long,
    /** Empréstimos recebidos que ainda faltam devolver. */
    val loansCents: Long,
    /** Contas fixas do mês ainda não pagas. */
    val unpaidBillsCents: Long,
) {
    /** Dinheiro livre agora = contas − caixinhas − cartão já comprometido. */
    val freeCents: Long get() = accountsCents - reservedCents - cardDebtCents
    /** Livre depois de pagar as contas fixas do mês que ainda faltam. */
    val freeAfterBillsCents: Long get() = freeCents - unpaidBillsCents
    /** Patrimônio total = tudo que você tem − o que deve (cartão e empréstimos), sem contar nada duas vezes. */
    val patrimonyCents: Long get() = accountsCents - cardDebtCents - loansCents
}

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
    /** v2: money in that is NOT earned (família, empréstimo, ajuda, reembolso). Not in incomeCents. */
    val otherInCents: Long = 0,
    /** v2: expenses linked to a job/source (Uber para trabalhar, etc.). Included in personalExpenseCents. */
    val workCostsCents: Long = 0,
    val transportCents: Long = 0,
    val profit: List<SourceProfit> = emptyList(),
) {
    val balanceCents: Long get() = incomeCents - expenseCents
    /** Gastos com você mesmo (sem custos de trabalho e sem estoque NFC). */
    val ownExpenseCents: Long get() = personalExpenseCents - workCostsCents
    val grossRevenueCents: Long get() = profit.sumOf { it.revenueCents }
    val profitCostsCents: Long get() = profit.sumOf { it.costsCents }
    val netProfitCents: Long get() = profit.sumOf { it.netCents }
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
            TxnType.LOAN_PAYMENT -> if (t.accountId == accountId) v -= t.amountCents
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

    /** v2: every box except the legacy "Gastos pessoais" is protected (reserved) money. */
    fun isReserved(box: br.com.daniel.meufinanceiro.data.Box) = box.kind != BoxKind.SPENDING

    /** Boxes the user sees as caixinhas (the old "Gastos pessoais" envelope is now just the free balance). */
    val reserveBoxes: List<br.com.daniel.meufinanceiro.data.Box> get() = activeBoxes.filter { isReserved(it) }

    /**
     * Reserved (protected) money. A box that went negative (spent more than it had) counts as 0:
     * the extra came out of the free money, so it must not inflate the free balance.
     */
    fun reserved(upTo: Long? = null): Long = ledger.boxes.filter { isReserved(it) }.sumOf { boxBalance(it.id, upTo).coerceAtLeast(0) }

    fun loansOutstanding(upTo: Long? = null): Long = ledger.loans.filter { upTo == null || it.date <= upTo }.sumOf { loanOutstanding(it.id, upTo) }

    fun loanOutstanding(loanId: Long, upTo: Long? = null): Long {
        val loan = ledger.loan(loanId) ?: return 0
        val paid = ledger.txns.filter { it.type == TxnType.LOAN_PAYMENT && it.loanId == loanId && (upTo == null || it.date <= upTo) }.sumOf { it.amountCents }
        return (loan.principalCents - paid).coerceAtLeast(0)
    }

    /** THE single source of truth for free / reserved / patrimony. Used by Home, reports, alerts and the assistant. */
    fun position(): MoneyPosition = MoneyPosition(
        accountsCents = totalAvailable(),
        reservedCents = reserved(),
        cardDebtCents = cardDebt(),
        loansCents = loansOutstanding(),
        unpaidBillsCents = billStatuses().filter { it.state != BillState.PAID && categoryById[it.bill.categoryId]?.isNfcStock != true }.sumOf { it.expectedCents },
    )

    /** Money in an account outside its caixinhas (negative = the boxes say more than the bank has). */
    fun accountOutsideBoxes(accountId: Long): Long =
        accountBalance(accountId) - ledger.boxes.filter { isReserved(it) && it.accountId == accountId }.sumOf { boxBalance(it.id).coerceAtLeast(0) }

    fun boxesInAccount(accountId: Long): Long = ledger.boxes.filter { isReserved(it) && it.accountId == accountId }.sumOf { boxBalance(it.id).coerceAtLeast(0) }

    /** Kept for compatibility: in v2 there is no "unallocated" pool; free money is simply what is not reserved. */
    fun unallocated(): Long = position().freeCents

    /* ----- Income / receivables ----- */

    fun sourceOf(t: Txn): Long? = t.sourceId
        ?: t.workId?.let { workById[it]?.sourceId }
        ?: t.nfcSaleId?.let { nfcSourceId }

    /** Earned income = work / sales. Family money, loans, help and refunds are NOT earned. */
    fun isEarned(t: Txn): Boolean = t.type == TxnType.INCOME && t.loanId == null && ledger.isEarnedSource(sourceOf(t))

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
            ExpenseEvent(it.date, it.amountCents, it.categoryId, it.description, false, categoryById[it.categoryId]?.isNfcStock == true, txnId = it.id,
                workId = it.workId, sourceId = it.sourceId ?: it.workId?.let { w -> workById[w]?.sourceId })
        }
        val card = ledger.installments.mapNotNull { inst ->
            val p = purchaseById[inst.purchaseId] ?: return@mapNotNull null
            val date = Dates.day(p.date).plusMonths((inst.number - 1).toLong()).toEpochDay()
            val desc = if (p.installments > 1) "${p.description} (${inst.number}/${p.installments})" else p.description
            ExpenseEvent(date, inst.amountCents, p.categoryId, desc, true, categoryById[p.categoryId]?.isNfcStock == true, purchaseId = p.id,
                workId = p.workId, sourceId = p.sourceId ?: p.workId?.let { w -> workById[w]?.sourceId })
        }
        return direct + card
    }

    /** Earned income only (work and sales). */
    fun incomeEvents(): List<IncomeEvent> = ledger.txns.filter { it.type == TxnType.INCOME && isEarned(it) }
        .map { IncomeEvent(it.date, it.amountCents, sourceOf(it), it.description, it.id) }

    /** Money that came in but is not earned income (família, empréstimo, ajuda, reembolso). */
    fun otherInEvents(): List<IncomeEvent> = ledger.txns.filter { it.type == TxnType.INCOME && !isEarned(it) }
        .map { IncomeEvent(it.date, it.amountCents, sourceOf(it), it.description, it.id) }

    /** Total cost linked to one job: inline costs + expenses linked to it. */
    fun workCosts(workId: Long): Long {
        val w = workById[workId] ?: return 0
        return w.costsCents + expenseEvents().filter { it.workId == workId }.sumOf { it.amountCents }
    }
    fun workProfit(workId: Long): Long = (workById[workId]?.myShareCents ?: 0) - workCosts(workId)

    /** Rentabilidade por fonte de renda (por competência: trabalhos pela data do trabalho, vendas pela data da venda). */
    fun profitability(period: Period): List<SourceProfit> {
        val exp = expenseEvents()
        return ledger.sources.filter { it.isEarned && it.type !in SourceType.notEarned }.mapNotNull { src ->
            val works = ledger.works.filter { it.sourceId == src.id && it.date in period }
            val workIds = works.map { it.id }.toSet()
            val direct = ledger.txns.filter { it.type == TxnType.INCOME && it.workId == null && it.nfcSaleId == null && it.loanId == null && it.sourceId == src.id && it.date in period }
            val linkedToWorks = exp.filter { it.workId != null && it.workId in workIds && !it.isNfcStock }.sumOf { it.amountCents }
            val linkedToSource = exp.filter { it.workId == null && it.sourceId == src.id && it.date in period && !it.isNfcStock }.sumOf { it.amountCents }
            val received = ledger.txns.filter { it.type == TxnType.INCOME && it.date in period && sourceOf(it) == src.id && it.loanId == null }.sumOf { it.amountCents }
            val sp = if (src.type == SourceType.NFC) {
                val sales = ledger.sales.filter { it.date in period }
                SourceProfit(src.id, src.name,
                    revenueCents = sales.sumOf { saleRevenue(it) } + direct.sumOf { it.amountCents },
                    costsCents = sales.sumOf { saleCost(it) + it.extraCostsCents } + linkedToSource,
                    hours = 0.0, receivedCents = received,
                    pendingCents = sales.sumOf { (saleRevenue(it) - saleReceived(it.id)).coerceAtLeast(0) }, jobs = sales.size)
            } else {
                SourceProfit(src.id, src.name,
                    revenueCents = works.sumOf { it.myShareCents } + direct.sumOf { it.amountCents },
                    costsCents = works.sumOf { it.costsCents } + linkedToWorks + linkedToSource,
                    hours = works.sumOf { it.hours }, receivedCents = received,
                    pendingCents = works.sumOf { (it.myShareCents - workReceived(it.id)).coerceAtLeast(0) }, jobs = works.size + direct.size)
            }
            sp.takeIf { it.revenueCents != 0L || it.costsCents != 0L || !src.archived }
        }
    }

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
            otherInCents = otherInEvents().filter { it.date in period }.sumOf { it.amountCents },
            workCostsCents = exp.filter { it.isWorkCost && !it.isNfcStock }.sumOf { it.amountCents },
            transportCents = exp.filter { categoryById[it.categoryId]?.name?.let { n -> QuickEntryParser.normalize(n).startsWith("transporte") } == true }.sumOf { it.amountCents },
            profit = profitability(period),
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
     * Livre para gastar = contas − caixinhas − cartão comprometido − contas fixas do mês ainda não pagas.
     * Dinheiro reservado NUNCA entra aqui.
     */
    fun freeToSpend(): Long = position().freeAfterBillsCents

    /* ----- Net worth / series ----- */

    /** Patrimônio total = contas (incluindo caixinhas) − dívida do cartão − empréstimos a devolver. */
    fun netWorth(upTo: Long? = null): Long = totalAvailable(upTo) - cardDebt(upTo = upTo) - loansOutstanding(upTo)

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

    /**
     * Warning when a movement leaves the FREE balance (never the reserved money) below the minimum,
     * or the daily account itself below the minimum.
     */
    fun lowBalanceWarning(accountId: Long?, deltaCents: Long): String? {
        if (deltaCents >= 0) return null
        val min = minBalance()
        val freeAfter = position().freeCents + deltaCents
        if (freeAfter < min) return "Com isso, seu saldo livre fica em ${Money.format(freeAfter)}, abaixo do mínimo de ${Money.format(min)}."
        val daily = ledger.dailyAccount ?: return null
        if (accountId != daily.id) return null
        val after = accountBalance(daily.id) + deltaCents
        return if (after < min) "Com isso, o saldo de ${daily.name} fica em ${Money.format(after)}, abaixo do mínimo de ${Money.format(min)}." else null
    }
}
