package br.com.daniel.meufinanceiro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Account::class, CreditCard::class, Category::class, IncomeSource::class, Box::class, DistributionRule::class,
        Txn::class, BoxMovement::class, CardPurchase::class, CardInstallment::class, WorkEntry::class,
        NfcBatch::class, NfcCustomer::class, NfcSale::class, Bill::class, Setting::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): FinanceDao

    companion object {
        const val FILE_NAME = "meufinanceiro.db"

        /**
         * IMPORTANT: no fallbackToDestructiveMigration(). A future schema change must ship a real
         * Migration; the app will refuse to wipe Daniel's data silently.
         */
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, FILE_NAME)
                .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING)
                .build()

        fun inMemory(context: Context): AppDatabase =
            Room.inMemoryDatabaseBuilder(context.applicationContext, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
    }
}
