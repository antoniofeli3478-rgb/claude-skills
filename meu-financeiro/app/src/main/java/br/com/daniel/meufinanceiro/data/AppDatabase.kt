package br.com.daniel.meufinanceiro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.File

@Database(
    entities = [
        Account::class, CreditCard::class, Category::class, IncomeSource::class, Box::class, DistributionRule::class,
        Txn::class, BoxMovement::class, CardPurchase::class, CardInstallment::class, WorkEntry::class,
        NfcBatch::class, NfcCustomer::class, NfcSale::class, Bill::class, Setting::class,
        Loan::class, ChatMessage::class, AssistantAction::class,
    ],
    version = 2,
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
        /**
         * v1 -> v2 (app 1.1.0). ONLY ADDS columns and tables: no row is changed or deleted.
         * Every existing balance, purchase and box movement stays exactly as it was.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `income_sources` ADD COLUMN `isEarned` INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE `boxes` ADD COLUMN `accountId` INTEGER")
                db.execSQL("ALTER TABLE `transactions` ADD COLUMN `loanId` INTEGER")
                db.execSQL("ALTER TABLE `card_purchases` ADD COLUMN `workId` INTEGER")
                db.execSQL("ALTER TABLE `card_purchases` ADD COLUMN `sourceId` INTEGER")
                db.execSQL("CREATE TABLE IF NOT EXISTS `loans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `person` TEXT NOT NULL, `principalCents` INTEGER NOT NULL, `date` INTEGER NOT NULL, `dueDate` INTEGER, `notes` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `chat_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `role` TEXT NOT NULL, `text` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `actionId` INTEGER)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `assistant_actions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `createdAt` INTEGER NOT NULL, `tool` TEXT NOT NULL, `summary` TEXT NOT NULL, `status` TEXT NOT NULL, `payload` TEXT NOT NULL, `undo` TEXT, `result` TEXT)")
            }
        }

        val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2)

        /**
         * Before the first open of a new app version, copies the raw database files
         * (db + wal + shm, consistent because nothing has opened them yet) to files/backups/db-v<old>/.
         * Done once per schema version; never overwritten.
         */
        fun backupFilesBeforeUpgrade(context: Context) {
            try {
                val dbFile = context.getDatabasePath(FILE_NAME)
                if (!dbFile.exists()) return
                val version = readUserVersion(dbFile) ?: return
                if (version >= 2) return
                // (the header can lag behind the WAL, so "0" also counts as old; the folder check makes it run once)
                val dir = File(context.filesDir, "backups/banco-antes-da-v1.1").apply { mkdirs() }
                if (File(dir, FILE_NAME).exists()) return
                for (suffix in listOf("", "-wal", "-shm")) {
                    val f = File(dbFile.path + suffix)
                    if (f.exists()) f.copyTo(File(dir, FILE_NAME + suffix), overwrite = false)
                }
            } catch (_: Exception) {
                // Never block app start because of the extra copy.
            }
        }

        /** Reads PRAGMA user_version from the SQLite header (bytes 60..63) without opening the database. */
        fun readUserVersion(f: File): Int? = try {
            f.inputStream().use { input ->
                val header = ByteArray(64)
                if (input.read(header) < 64) null
                else ((header[60].toInt() and 0xFF) shl 24) or ((header[61].toInt() and 0xFF) shl 16) or
                    ((header[62].toInt() and 0xFF) shl 8) or (header[63].toInt() and 0xFF)
            }
        } catch (_: Exception) { null }

        fun build(context: Context): AppDatabase {
            backupFilesBeforeUpgrade(context)
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, FILE_NAME)
                .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING)
                .addMigrations(*ALL_MIGRATIONS)
                .build()
        }

        fun inMemory(context: Context): AppDatabase =
            Room.inMemoryDatabaseBuilder(context.applicationContext, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
    }
}
