package br.com.daniel.meufinanceiro.data

import androidx.room.InvalidationTracker
import androidx.room.withTransaction
import br.com.daniel.meufinanceiro.domain.CardBilling
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.Distribution
import br.com.daniel.meufinanceiro.domain.Keys
import br.com.daniel.meufinanceiro.domain.Ledger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.Serializable
import java.util.UUID

/** Everything in the database, for backup/restore. */
@Serializable
data class BackupData(
    val app: String = "MeuFinanceiro",
    val schema: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val accounts: List<Account>, val cards: List<CreditCard>, val categories: List<Category>,
    val sources: List<IncomeSource>, val boxes: List<Box>, val rules: List<DistributionRule>,
    val txns: List<Txn>, val moves: List<BoxMovement>, val purchases: List<CardPurchase>,
    val installments: List<CardInstallment>, val works: List<WorkEntry>, val batches: List<NfcBatch>,
    val customers: List<NfcCustomer>, val sales: List<NfcSale>, val bills: List<Bill>, val settings: List<Setting>,
)

/** How an income is split between boxes. */
sealed interface Split {
    data object Default : Split
    data object None : Split
    data class Custom(val parts: List<Pair<Long, Long>>) : Split
}

/**
 * Single entry point for every write. Each operation runs in ONE database transaction,
 * so a crash can never leave half a purchase or a payment without its box movements.
 */
class FinanceRepository(private val db: AppDatabase) {
    private val dao = db.dao()

    private val allTables = arrayOf(
        "accounts", "cards", "categories", "income_sources", "boxes", "distribution_rules", "transactions",
        "box_movements", "card_purchases", "card_installments", "work_entries", "nfc_batches", "nfc_customers",
        "nfc_sales", "bills", "settings",
    )

    /** Emits a fresh, consistent snapshot every time anything changes. */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun ledgerFlow(): Flow<Ledger> = callbackFlow {
        val observer = object : InvalidationTracker.Observer(allTables) {
            override fun onInvalidated(tables: Set<String>) { trySend(Unit) }
        }
        db.invalidationTracker.addObserver(observer)
        trySend(Unit)
        awaitClose { db.invalidationTracker.removeObserver(observer) }
    }.conflate().mapLatest { snapshot() }

    suspend fun snapshot(): Ledger = db.withTransaction {
        Ledger(
            dao.accounts(), dao.cards(), dao.categories(), dao.sources(), dao.boxes(), dao.rules(), dao.txns(),
            dao.moves(), dao.purchases(), dao.installments(), dao.works(), dao.batches(), dao.customers(),
            dao.sales(), dao.bills(), dao.settings().associate { it.key to it.value },
        )
    }

    suspend fun seedIfEmpty() = db.withTransaction { Seed.run(dao) }

    /* ================= Income ================= */

    private suspend fun boxMovesForIncome(txnId: Long, amount: Long, date: Long, split: Split, saleId: Long?): List<BoxMovement> {
        val moves = mutableListOf<BoxMovement>()
        var personal = amount
        if (saleId != null) {
            val sale = dao.sales().first { it.id == saleId }
            val (stock, profit) = Distribution.nfcPaymentShares(amount, sale.unitPriceCents * sale.quantity, sale.unitCostCents * sale.quantity)
            val stockBox = dao.boxes().firstOrNull { it.kind == BoxKind.NFC_STOCK && !it.archived }
            if (stockBox != null && stock > 0) {
                moves += BoxMovement(boxId = stockBox.id, amountCents = stock, date = date, kind = MoveKind.DISTRIBUTION, txnId = txnId, note = "Reposição de estoque (custo das plaquinhas)")
                personal = profit
            }
        }
        when (split) {
            Split.None -> Unit
            Split.Default -> {
                val boxes = dao.boxes().associateBy { it.id }
                val rules = dao.rules().filter { boxes[it.boxId]?.archived == false }
                    .sortedBy { boxes[it.boxId]?.sortOrder ?: 0 }.map { it.boxId to it.basisPoints }
                Distribution.split(personal, rules).forEach { (box, v) ->
                    moves += BoxMovement(boxId = box, amountCents = v, date = date, kind = MoveKind.DISTRIBUTION, txnId = txnId)
                }
            }
            is Split.Custom -> {
                require(split.parts.all { it.second >= 0 }) { "Valores negativos na divisão" }
                require(split.parts.sumOf { it.second } <= personal) { "A divisão passa do valor da entrada" }
                split.parts.filter { it.second > 0 }.forEach { (box, v) ->
                    moves += BoxMovement(boxId = box, amountCents = v, date = date, kind = MoveKind.DISTRIBUTION, txnId = txnId)
                }
            }
        }
        return moves
    }

    private fun Split.mode() = when (this) { Split.Default -> DistMode.DEFAULT; Split.None -> DistMode.NONE; is Split.Custom -> DistMode.CUSTOM }

    /** Money received. Splits it between the boxes. */
    suspend fun addIncome(
        amountCents: Long, date: Long, description: String, accountId: Long, sourceId: Long?, split: Split = Split.Default,
        workId: Long? = null, saleId: Long? = null, notes: String = "",
    ): Long = db.withTransaction {
        require(amountCents > 0) { "Valor precisa ser maior que zero" }
        val id = dao.insertTxn(Txn(type = TxnType.INCOME, amountCents = amountCents, date = date, description = description.trim(),
            accountId = accountId, sourceId = sourceId, workId = workId, nfcSaleId = saleId, distMode = split.mode(), notes = notes))
        dao.insertMoves(boxMovesForIncome(id, amountCents, date, split, saleId))
        id
    }

    /* ================= Expenses ================= */

    data class ExpenseInput(
        val amountCents: Long, val date: Long, val description: String, val categoryId: Long?, val method: String,
        val accountId: Long?, val cardId: Long? = null, val installments: Int = 1, val boxId: Long? = null,
        val recurring: Boolean = false, val notes: String = "", val billId: Long? = null, val billMonth: Int? = null,
        val nfcBatchId: Long? = null,
    )

    /** Returns the Txn id (debit/pix/cash...) or the CardPurchase id (credit) — see [ExpenseResult]. */
    data class ExpenseResult(val txnId: Long?, val purchaseId: Long?)

    suspend fun addExpense(e: ExpenseInput): ExpenseResult = db.withTransaction {
        require(e.amountCents > 0) { "Valor precisa ser maior que zero" }
        val ledger = snapshot()
        val box = e.boxId ?: ledger.defaultBoxForCategory(e.categoryId)?.id
        if (e.method == PayMethod.CREDIT) {
            val card = ledger.card(e.cardId) ?: ledger.mainCard ?: error("Cadastre um cartão de crédito")
            val pid = dao.insertPurchase(CardPurchase(cardId = card.id, description = e.description.trim(), totalCents = e.amountCents,
                installments = e.installments.coerceIn(1, 48), date = e.date, categoryId = e.categoryId, boxId = box, billId = e.billId,
                billMonth = e.billMonth, nfcBatchId = e.nfcBatchId, recurring = e.recurring, notes = e.notes))
            val inst = CardBilling.installments(e.amountCents, e.installments, Dates.day(e.date), card)
                .map { (n, v, key) -> CardInstallment(purchaseId = pid, number = n, amountCents = v, invoiceKey = key) }
            dao.insertInstallments(inst)
            // The whole purchase is committed money: it leaves the box now, not when the invoice is paid.
            if (box != null) dao.insertMove(BoxMovement(boxId = box, amountCents = -e.amountCents, date = e.date, kind = MoveKind.SPEND, cardPurchaseId = pid, note = e.description.trim()))
            ExpenseResult(null, pid)
        } else {
            val acc = e.accountId ?: (if (e.method == PayMethod.CASH) ledger.cashAccount?.id else ledger.dailyAccount?.id) ?: error("Escolha a conta")
            val id = dao.insertTxn(Txn(type = TxnType.EXPENSE, amountCents = e.amountCents, date = e.date, description = e.description.trim(),
                accountId = acc, categoryId = e.categoryId, method = e.method, billId = e.billId, billMonth = e.billMonth,
                nfcBatchId = e.nfcBatchId, boxId = box, recurring = e.recurring, notes = e.notes))
            if (box != null) dao.insertMove(BoxMovement(boxId = box, amountCents = -e.amountCents, date = e.date, kind = MoveKind.SPEND, txnId = id, note = e.description.trim()))
            ExpenseResult(id, null)
        }
    }

    /* ================= Transfers / card / adjustments ================= */

    /** Between Daniel's own accounts: not income, not expense. */
    suspend fun addTransfer(fromAccount: Long, toAccount: Long, amountCents: Long, date: Long, description: String = "Transferência"): Long = db.withTransaction {
        require(amountCents > 0 && fromAccount != toAccount) { "Transferência inválida" }
        dao.insertTxn(Txn(type = TxnType.TRANSFER, amountCents = amountCents, date = date, description = description, accountId = fromAccount, toAccountId = toAccount, method = PayMethod.TRANSFER))
    }

    /** Paying the invoice: money leaves the account, but the purchases were already counted as expenses. */
    suspend fun payInvoice(cardId: Long, invoiceKey: Int, amountCents: Long, accountId: Long, date: Long): Long = db.withTransaction {
        require(amountCents > 0) { "Valor precisa ser maior que zero" }
        dao.insertTxn(Txn(type = TxnType.CARD_PAYMENT, amountCents = amountCents, date = date, description = "Pagamento da fatura ${Dates.monthLabel(invoiceKey)}",
            accountId = accountId, cardId = cardId, invoiceKey = invoiceKey, method = PayMethod.TRANSFER))
    }

    /** Sets the account balance to [newBalanceCents] today with a correction entry. */
    suspend fun adjustBalance(accountId: Long, currentBalanceCents: Long, newBalanceCents: Long, date: Long): Long? = db.withTransaction {
        val delta = newBalanceCents - currentBalanceCents
        if (delta == 0L) null else dao.insertTxn(Txn(type = TxnType.ADJUSTMENT, amountCents = delta, date = date, description = "Ajuste de saldo", accountId = accountId, method = PayMethod.OTHER))
    }

    /** Edit = delete the old record (and its box movements / installments) and save the new one, atomically. */
    suspend fun replaceExpense(oldTxnId: Long?, oldPurchaseId: Long?, e: ExpenseInput): ExpenseResult = db.withTransaction {
        oldTxnId?.let { dao.deleteTxn(it) }
        oldPurchaseId?.let { dao.deletePurchase(it) }
        addExpense(e)
    }

    suspend fun replaceIncome(
        oldTxnId: Long, amountCents: Long, date: Long, description: String, accountId: Long, sourceId: Long?, split: Split, notes: String = "",
    ): Long = db.withTransaction {
        val old = dao.txn(oldTxnId) ?: error("Lançamento não encontrado")
        dao.deleteTxn(oldTxnId)
        addIncome(amountCents, date, description, accountId, sourceId, split, old.workId, old.nfcSaleId, notes)
    }

    suspend fun replaceSimple(t: Txn) = db.withTransaction {
        require(t.type == TxnType.TRANSFER || t.type == TxnType.CARD_PAYMENT || t.type == TxnType.ADJUSTMENT)
        dao.deleteTxn(t.id)
        dao.insertTxn(t)
    }

    suspend fun deleteTxn(id: Long) = db.withTransaction { dao.deleteTxn(id) }
    suspend fun deletePurchase(id: Long) = db.withTransaction { dao.deletePurchase(id) }

    /* ================= Boxes ================= */

    /** Puts money that is "sem caixinha" into a box (negative = takes it out back to "sem caixinha"). */
    suspend fun boxDeposit(boxId: Long, amountCents: Long, date: Long, note: String = "") = db.withTransaction {
        require(amountCents != 0L)
        dao.insertMove(BoxMovement(boxId = boxId, amountCents = amountCents, date = date, kind = if (amountCents > 0) MoveKind.DEPOSIT else MoveKind.WITHDRAW, note = note))
    }

    suspend fun boxTransfer(fromBox: Long, toBox: Long, amountCents: Long, date: Long, note: String = "") = db.withTransaction {
        require(amountCents > 0 && fromBox != toBox)
        val g = UUID.randomUUID().toString()
        dao.insertMoves(listOf(
            BoxMovement(boxId = fromBox, amountCents = -amountCents, date = date, kind = MoveKind.TRANSFER, groupId = g, note = note),
            BoxMovement(boxId = toBox, amountCents = amountCents, date = date, kind = MoveKind.TRANSFER, groupId = g, note = note),
        ))
    }

    suspend fun deleteBoxMove(m: BoxMovement) = db.withTransaction {
        if (m.groupId != null) dao.deleteMoveGroup(m.groupId) else dao.deleteMove(m.id)
    }

    suspend fun saveRules(rules: List<DistributionRule>) = db.withTransaction {
        require(rules.sumOf { it.basisPoints } == 10_000) { "Os percentuais precisam somar 100%" }
        dao.clearRules()
        rules.filter { it.basisPoints > 0 }.forEach { dao.upsertRule(it) }
    }

    /* ================= Work (Tio Rex, recreação, pipoca, outros) ================= */

    suspend fun saveWork(w: WorkEntry, receivedNowCents: Long = 0, accountId: Long? = null, split: Split = Split.Default, date: Long = w.date): Long = db.withTransaction {
        require(w.myShareCents >= 0)
        val id = dao.upsertWork(w).let { if (w.id != 0L) w.id else it }
        if (receivedNowCents > 0 && accountId != null) {
            val src = dao.sources().firstOrNull { it.id == w.sourceId }
            addIncome(receivedNowCents, date, w.title.ifBlank { src?.name ?: "Trabalho" }, accountId, w.sourceId, split, workId = id)
        }
        id
    }

    suspend fun deleteWork(id: Long) = db.withTransaction { dao.deleteTxnsOfWork(id); dao.deleteWork(id) }

    /* ================= NFC ================= */

    /** Registers a batch; optionally also the payment (expense from the NFC stock box). */
    suspend fun saveBatch(b: NfcBatch, payMethod: String? = null, accountId: Long? = null, cardId: Long? = null, installments: Int = 1): Long = db.withTransaction {
        require(b.quantity > 0 && b.totalCostCents >= 0)
        val isNew = b.id == 0L
        val id = dao.upsertBatch(b).let { if (!isNew) b.id else it }
        if (isNew && payMethod != null && b.totalCostCents > 0) {
            val cat = dao.categories().firstOrNull { it.isNfcStock }
            addExpense(ExpenseInput(b.totalCostCents, b.date, "Lote NFC (${b.quantity} un.)", cat?.id, payMethod, accountId, cardId, installments, nfcBatchId = id))
        }
        id
    }

    suspend fun deleteBatch(id: Long) = db.withTransaction { dao.deleteTxnsOfBatch(id); dao.deletePurchasesOfBatch(id); dao.deleteBatch(id) }

    suspend fun saveSale(s: NfcSale, receivedNowCents: Long = 0, accountId: Long? = null, split: Split = Split.Default): Long = db.withTransaction {
        require(s.quantity > 0 && s.unitPriceCents >= 0)
        val id = dao.upsertSale(s).let { if (s.id != 0L) s.id else it }
        if (receivedNowCents > 0 && accountId != null) {
            val nfc = dao.sources().firstOrNull { it.type == SourceType.NFC }
            addIncome(receivedNowCents, s.date, "Venda NFC (${s.quantity} un.)", accountId, nfc?.id, split, saleId = id)
        }
        id
    }

    suspend fun deleteSale(id: Long) = db.withTransaction { dao.deleteTxnsOfSale(id); dao.deleteSale(id) }
    suspend fun saveCustomer(c: NfcCustomer): Long = dao.upsertCustomer(c).let { if (c.id != 0L) c.id else it }
    suspend fun deleteCustomer(id: Long) = db.withTransaction { dao.unlinkCustomer(id); dao.deleteCustomer(id) }

    /* ================= Config entities ================= */

    suspend fun saveAccount(a: Account): Long = db.withTransaction {
        val id = dao.upsertAccount(a).let { if (a.id != 0L) a.id else it }
        if (a.isDaily) dao.accounts().filter { it.id != id && it.isDaily }.forEach { dao.upsertAccount(it.copy(isDaily = false)) }
        id
    }
    suspend fun saveCard(c: CreditCard): Long = dao.upsertCard(c).let { if (c.id != 0L) c.id else it }
    suspend fun saveCategory(c: Category): Long = dao.upsertCategory(c).let { if (c.id != 0L) c.id else it }
    suspend fun saveSource(s: IncomeSource): Long = dao.upsertSource(s).let { if (s.id != 0L) s.id else it }
    suspend fun saveBox(b: Box): Long = dao.upsertBox(b).let { if (b.id != 0L) b.id else it }
    suspend fun saveBill(b: Bill): Long = dao.upsertBill(b).let { if (b.id != 0L) b.id else it }
    suspend fun deleteBill(b: Bill) = db.withTransaction { dao.unlinkBillTxns(b.id); dao.unlinkBillPurchases(b.id); dao.deleteBill(b) }

    suspend fun setSetting(key: String, value: String?) {
        if (value == null) dao.deleteSetting(key) else dao.upsertSetting(Setting(key, value))
    }

    /* ================= Backup ================= */

    suspend fun exportAll(): BackupData = db.withTransaction {
        BackupData(
            accounts = dao.accounts(), cards = dao.cards(), categories = dao.categories(), sources = dao.sources(),
            boxes = dao.boxes(), rules = dao.rules(), txns = dao.txns(), moves = dao.moves(), purchases = dao.purchases(),
            installments = dao.installments(), works = dao.works(), batches = dao.batches(), customers = dao.customers(),
            sales = dao.sales(), bills = dao.bills(),
            // Security settings (PIN) are device-specific and never exported.
            settings = dao.settings().filter { it.key !in setOf(Keys.PIN_HASH, Keys.PIN_SALT, Keys.BIOMETRIC) },
        )
    }

    /** Replaces ALL data with the backup, atomically. If anything fails, nothing changes. */
    suspend fun restoreAll(b: BackupData) = db.withTransaction {
        require(b.app == "MeuFinanceiro") { "Este arquivo não é um backup do Meu Financeiro" }
        require(b.schema <= 1) { "Backup de uma versão mais nova do app" }
        dao.wipeMoves(); dao.wipeInstallments(); dao.wipeTxns(); dao.wipePurchases(); dao.wipeWorks(); dao.wipeSales()
        dao.wipeBatches(); dao.wipeCustomers(); dao.wipeBills(); dao.wipeRules(); dao.wipeBoxes(); dao.wipeSources()
        dao.wipeCategories(); dao.wipeCards(); dao.wipeAccounts(); dao.wipeDataSettings()
        dao.insertAccounts(b.accounts); dao.insertCards(b.cards); dao.insertCategories(b.categories); dao.insertSources(b.sources)
        dao.insertBoxes(b.boxes); dao.insertRules(b.rules); dao.insertTxns(b.txns); dao.insertPurchases(b.purchases)
        dao.insertInstallments(b.installments); dao.insertMoves(b.moves); dao.insertWorks(b.works); dao.insertBatches(b.batches)
        dao.insertCustomers(b.customers); dao.insertSales(b.sales); dao.insertBills(b.bills)
        dao.upsertSettings(b.settings.filter { it.key !in setOf(Keys.PIN_HASH, Keys.PIN_SALT, Keys.BIOMETRIC) })
    }
}
