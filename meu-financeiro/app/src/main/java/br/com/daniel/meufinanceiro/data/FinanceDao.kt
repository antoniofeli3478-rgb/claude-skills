package br.com.daniel.meufinanceiro.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface FinanceDao {
    // ---- Snapshot reads (all done inside one transaction by the repository) ----
    @Query("SELECT * FROM accounts ORDER BY sortOrder, id") suspend fun accounts(): List<Account>
    @Query("SELECT * FROM cards ORDER BY id") suspend fun cards(): List<CreditCard>
    @Query("SELECT * FROM categories ORDER BY sortOrder, id") suspend fun categories(): List<Category>
    @Query("SELECT * FROM income_sources ORDER BY sortOrder, id") suspend fun sources(): List<IncomeSource>
    @Query("SELECT * FROM boxes ORDER BY sortOrder, id") suspend fun boxes(): List<Box>
    @Query("SELECT * FROM distribution_rules") suspend fun rules(): List<DistributionRule>
    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC") suspend fun txns(): List<Txn>
    @Query("SELECT * FROM box_movements ORDER BY date DESC, id DESC") suspend fun moves(): List<BoxMovement>
    @Query("SELECT * FROM card_purchases ORDER BY date DESC, id DESC") suspend fun purchases(): List<CardPurchase>
    @Query("SELECT * FROM card_installments") suspend fun installments(): List<CardInstallment>
    @Query("SELECT * FROM work_entries ORDER BY date DESC, id DESC") suspend fun works(): List<WorkEntry>
    @Query("SELECT * FROM nfc_batches ORDER BY date DESC, id DESC") suspend fun batches(): List<NfcBatch>
    @Query("SELECT * FROM nfc_customers ORDER BY name") suspend fun customers(): List<NfcCustomer>
    @Query("SELECT * FROM nfc_sales ORDER BY date DESC, id DESC") suspend fun sales(): List<NfcSale>
    @Query("SELECT * FROM bills ORDER BY sortOrder, id") suspend fun bills(): List<Bill>
    @Query("SELECT * FROM settings") suspend fun settings(): List<Setting>

    @Query("SELECT * FROM transactions WHERE id = :id") suspend fun txn(id: Long): Txn?
    @Query("SELECT * FROM card_purchases WHERE id = :id") suspend fun purchase(id: Long): CardPurchase?
    @Query("SELECT COUNT(*) FROM accounts") suspend fun accountCount(): Int

    // ---- Writes ----
    @Upsert suspend fun upsertAccount(a: Account): Long
    @Upsert suspend fun upsertCard(c: CreditCard): Long
    @Upsert suspend fun upsertCategory(c: Category): Long
    @Upsert suspend fun upsertSource(s: IncomeSource): Long
    @Upsert suspend fun upsertBox(b: Box): Long
    @Upsert suspend fun upsertRule(r: DistributionRule)
    @Query("DELETE FROM distribution_rules") suspend fun clearRules()
    @Upsert suspend fun upsertBill(b: Bill): Long
    @Upsert suspend fun upsertCustomer(c: NfcCustomer): Long
    @Upsert suspend fun upsertSetting(s: Setting)
    @Query("DELETE FROM settings WHERE `key` = :key") suspend fun deleteSetting(key: String)

    @Insert suspend fun insertTxn(t: Txn): Long
    @Insert suspend fun insertMove(m: BoxMovement): Long
    @Insert suspend fun insertMoves(m: List<BoxMovement>)
    @Insert suspend fun insertPurchase(p: CardPurchase): Long
    @Insert suspend fun insertInstallments(i: List<CardInstallment>)
    @Upsert suspend fun upsertWork(w: WorkEntry): Long
    @Upsert suspend fun upsertBatch(b: NfcBatch): Long
    @Upsert suspend fun upsertSale(s: NfcSale): Long

    @Query("DELETE FROM transactions WHERE id = :id") suspend fun deleteTxn(id: Long)
    @Query("DELETE FROM card_purchases WHERE id = :id") suspend fun deletePurchase(id: Long)
    @Query("DELETE FROM box_movements WHERE groupId = :groupId") suspend fun deleteMoveGroup(groupId: String)
    @Query("DELETE FROM box_movements WHERE id = :id") suspend fun deleteMove(id: Long)
    @Query("DELETE FROM work_entries WHERE id = :id") suspend fun deleteWork(id: Long)
    @Query("DELETE FROM transactions WHERE workId = :workId") suspend fun deleteTxnsOfWork(workId: Long)
    @Query("DELETE FROM nfc_sales WHERE id = :id") suspend fun deleteSale(id: Long)
    @Query("DELETE FROM transactions WHERE nfcSaleId = :saleId") suspend fun deleteTxnsOfSale(saleId: Long)
    @Query("DELETE FROM nfc_batches WHERE id = :id") suspend fun deleteBatch(id: Long)
    @Query("DELETE FROM transactions WHERE nfcBatchId = :batchId") suspend fun deleteTxnsOfBatch(batchId: Long)
    @Query("DELETE FROM card_purchases WHERE nfcBatchId = :batchId") suspend fun deletePurchasesOfBatch(batchId: Long)
    @Query("DELETE FROM nfc_customers WHERE id = :id") suspend fun deleteCustomer(id: Long)
    @Query("UPDATE nfc_sales SET customerId = NULL WHERE customerId = :id") suspend fun unlinkCustomer(id: Long)
    @Delete suspend fun deleteBill(b: Bill)
    @Query("UPDATE transactions SET billId = NULL WHERE billId = :id") suspend fun unlinkBillTxns(id: Long)
    @Query("UPDATE card_purchases SET billId = NULL WHERE billId = :id") suspend fun unlinkBillPurchases(id: Long)

    // ---- Full wipe (restore from backup) ----
    @Query("DELETE FROM box_movements") suspend fun wipeMoves()
    @Query("DELETE FROM card_installments") suspend fun wipeInstallments()
    @Query("DELETE FROM transactions") suspend fun wipeTxns()
    @Query("DELETE FROM card_purchases") suspend fun wipePurchases()
    @Query("DELETE FROM work_entries") suspend fun wipeWorks()
    @Query("DELETE FROM nfc_sales") suspend fun wipeSales()
    @Query("DELETE FROM nfc_batches") suspend fun wipeBatches()
    @Query("DELETE FROM nfc_customers") suspend fun wipeCustomers()
    @Query("DELETE FROM bills") suspend fun wipeBills()
    @Query("DELETE FROM distribution_rules") suspend fun wipeRules()
    @Query("DELETE FROM boxes") suspend fun wipeBoxes()
    @Query("DELETE FROM income_sources") suspend fun wipeSources()
    @Query("DELETE FROM categories") suspend fun wipeCategories()
    @Query("DELETE FROM cards") suspend fun wipeCards()
    @Query("DELETE FROM accounts") suspend fun wipeAccounts()
    @Query("DELETE FROM settings WHERE `key` NOT IN ('pin_hash','pin_salt','biometric','theme')") suspend fun wipeDataSettings()

    @Insert suspend fun insertAccounts(x: List<Account>)
    @Insert suspend fun insertCards(x: List<CreditCard>)
    @Insert suspend fun insertCategories(x: List<Category>)
    @Insert suspend fun insertSources(x: List<IncomeSource>)
    @Insert suspend fun insertBoxes(x: List<Box>)
    @Insert suspend fun insertRules(x: List<DistributionRule>)
    @Insert suspend fun insertTxns(x: List<Txn>)
    @Insert suspend fun insertPurchases(x: List<CardPurchase>)
    @Insert suspend fun insertWorks(x: List<WorkEntry>)
    @Insert suspend fun insertBatches(x: List<NfcBatch>)
    @Insert suspend fun insertCustomers(x: List<NfcCustomer>)
    @Insert suspend fun insertSales(x: List<NfcSale>)
    @Insert suspend fun insertBills(x: List<Bill>)
    @Upsert suspend fun upsertSettings(x: List<Setting>)
}
