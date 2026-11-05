package br.com.daniel.meufinanceiro.data

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.com.daniel.meufinanceiro.domain.FinanceCalc
import br.com.daniel.meufinanceiro.domain.Keys
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.File

/**
 * The most important test of version 1.1: a database written by version 1.0 (the one installed on
 * Antônio's phone) must open in 1.1 with every row and every balance exactly as before.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class MigrationTest {
    private val dbName = "migration-test.db"

    @get:Rule
    val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), AppDatabase::class.java)

    private val today = 20400L

    /** Realistic v1 data, inserted with the exact v1 schema (same rows the 1.0 app writes). */
    private fun createV1() {
        helper.createDatabase(dbName, 1).apply {
            execSQL("INSERT INTO accounts VALUES (1,'Nubank','BANK',0,0,'Caixinhas',4287258870,0,0)")
            execSQL("INSERT INTO accounts VALUES (2,'Banco Inter','BANK',0,1,'Dia a dia',4294932992,0,1)")
            execSQL("INSERT INTO accounts VALUES (3,'Dinheiro (carteira)','CASH',0,0,'',4282244247,0,2)")
            execSQL("INSERT INTO cards VALUES (1,'Cartão Inter','Inter',20,14,NULL,'BEST_DAY',NULL,2,0)")
            execSQL("INSERT INTO categories VALUES (1,'Alimentação',4294287627,0,0,0)")
            execSQL("INSERT INTO categories VALUES (2,'Transporte',4282090486,0,0,1)")
            execSQL("INSERT INTO categories VALUES (8,'Estoque NFC',4278630100,1,0,7)")
            execSQL("INSERT INTO income_sources VALUES (1,'Plaquinhas NFC','NFC',8990,0.0,4278630100,0,0)")
            execSQL("INSERT INTO income_sources VALUES (2,'Tio Rex Restaurante','TIO_REX',8000,4.0,4280469590,0,1)")
            execSQL("INSERT INTO boxes VALUES (1,'Gastos pessoais','SPENDING',NULL,NULL,NULL,NULL,4282244247,'',0,0)")
            execSQL("INSERT INTO boxes VALUES (2,'Reserva de emergência','EMERGENCY',NULL,NULL,NULL,6,4282090486,'',0,1)")
            execSQL("INSERT INTO boxes VALUES (3,'Sair do Brasil','ABROAD',NULL,NULL,NULL,NULL,4287060214,'',0,2)")
            execSQL("INSERT INTO boxes VALUES (4,'Estoque NFC','NFC_STOCK',NULL,NULL,NULL,NULL,4278630100,'',0,3)")
            execSQL("INSERT INTO boxes VALUES (5,'Computador','COMPUTER',400000,600000,NULL,NULL,4294288384,'',0,4)")
            execSQL("INSERT INTO distribution_rules VALUES (1,5000)")
            execSQL("INSERT INTO distribution_rules VALUES (2,2500)")
            execSQL("INSERT INTO distribution_rules VALUES (3,2500)")
            // Balance adjustment (Nubank had 212), income of 80 (split), debit expense, credit purchase in 3x
            execSQL("INSERT INTO transactions VALUES (1,'ADJUSTMENT',21200,${today - 10},'Ajuste de saldo',1,NULL,NULL,NULL,'OTHER',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'DEFAULT',NULL,0,'',1)")
            execSQL("INSERT INTO work_entries VALUES (1,2,${today - 2},'Turno',4.0,0,0,8000,'',2)")
            execSQL("INSERT INTO transactions VALUES (2,'INCOME',8000,${today - 2},'Tio Rex',2,NULL,NULL,2,'PIX',NULL,NULL,NULL,NULL,1,NULL,NULL,'DEFAULT',NULL,0,'',3)")
            execSQL("INSERT INTO box_movements VALUES (1,1,4000,${today - 2},'DISTRIBUTION',2,NULL,NULL,'')")
            execSQL("INSERT INTO box_movements VALUES (2,2,2000,${today - 2},'DISTRIBUTION',2,NULL,NULL,'')")
            execSQL("INSERT INTO box_movements VALUES (3,3,2000,${today - 2},'DISTRIBUTION',2,NULL,NULL,'')")
            execSQL("INSERT INTO box_movements VALUES (4,5,17000,${today - 9},'DEPOSIT',NULL,NULL,NULL,'Distribuição manual')")
            execSQL("INSERT INTO transactions VALUES (3,'EXPENSE',2500,${today - 1},'Almoço',2,NULL,1,NULL,'PIX',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'DEFAULT',1,0,'',4)")
            execSQL("INSERT INTO box_movements VALUES (5,1,-2500,${today - 1},'SPEND',3,NULL,NULL,'Almoço')")
            execSQL("INSERT INTO card_purchases VALUES (1,1,'Tênis',30000,3,${today - 1},NULL,1,NULL,NULL,NULL,0,'',5)")
            execSQL("INSERT INTO card_installments VALUES (1,1,1,10000,202512)")
            execSQL("INSERT INTO card_installments VALUES (2,1,2,10000,202601)")
            execSQL("INSERT INTO card_installments VALUES (3,1,3,10000,202602)")
            execSQL("INSERT INTO box_movements VALUES (6,1,-30000,${today - 1},'SPEND',NULL,1,NULL,'Tênis')")
            execSQL("INSERT INTO nfc_batches VALUES (1,${today - 30},10,14000,'','')")
            execSQL("INSERT INTO nfc_sales VALUES (1,${today - 3},NULL,1,8990,1400,0,'',6)")
            execSQL("INSERT INTO bills VALUES (1,'Academia',16000,10,1,0,NULL,'PIX',2,NULL,'',0)")
            execSQL("INSERT INTO settings VALUES ('min_balance_cents','3000')")
            execSQL("INSERT INTO settings VALUES ('theme','dark')")
            close()
        }
    }

    @Test fun migration1to2KeepsEverySchemaRule() {
        createV1()
        helper.runMigrationsAndValidate(dbName, 2, true, AppDatabase.MIGRATION_1_2).close()
    }

    @Test fun migratedDataIsIdenticalAndBalancesDoNotChange() = runBlocking {
        createV1()
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val db = Room.databaseBuilder(ctx, AppDatabase::class.java, dbName).addMigrations(*AppDatabase.ALL_MIGRATIONS).allowMainThreadQueries().build()
        val repo = FinanceRepository(db)
        val before = repo.snapshot()
        // every v1 row is still there
        assertEquals(3, before.accounts.size); assertEquals(3, before.txns.size); assertEquals(6, before.moves.size)
        assertEquals(1, before.purchases.size); assertEquals(3, before.installments.size); assertEquals(1, before.works.size)
        assertEquals(1, before.batches.size); assertEquals(1, before.sales.size); assertEquals(1, before.bills.size)
        assertEquals("dark", before.setting("theme"))
        val c0 = FinanceCalc(before, today)
        val accountsBefore = before.accounts.associate { it.id to c0.accountBalance(it.id) }

        repo.seedIfEmpty() // the app runs this on start: v2 data additions
        repo.seedIfEmpty() // twice: must not duplicate anything
        val after = repo.snapshot()
        val c = FinanceCalc(after, today)
        // money is untouched
        after.accounts.forEach { assertEquals(accountsBefore[it.id], c.accountBalance(it.id)) }
        assertEquals(3, after.accounts.size); assertEquals(3, after.txns.size); assertEquals(6, after.moves.size)
        assertEquals(30000L, c.cardDebt())
        // additions only, once
        assertEquals(1, after.sources.count { it.type == SourceType.FAMILY })
        assertEquals(1, after.sources.count { it.type == SourceType.LOAN })
        assertTrue(after.sources.first { it.type == SourceType.TIO_REX }.isEarned)
        assertEquals("Antônio Felipe", after.userName)
        assertEquals("2", after.setting(Keys.DATA_VERSION))
        assertEquals(1L, after.boxes.first { it.kind == BoxKind.COMPUTER }.accountId) // linked to Nubank
        // v2 position: Nubank 212 + Inter (80-25) = 267 in accounts; reserved = 20+20+170 = 210; card 300
        val p = c.position()
        assertEquals(26700L, p.accountsCents)
        assertEquals(21000L, p.reservedCents)
        assertEquals(26700L - 21000L - 30000L, p.freeCents)
        db.close()
    }

    @Test fun appMakesARawCopyOfTheOldDatabaseBeforeUpgrading() {
        createV1()
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        // simulate the installed 1.0 database file at the app's real path
        val src = ctx.getDatabasePath(dbName)
        val real = ctx.getDatabasePath(AppDatabase.FILE_NAME)
        real.parentFile?.mkdirs()
        src.copyTo(real, overwrite = true)
        AppDatabase.backupFilesBeforeUpgrade(ctx)
        val copy = File(ctx.filesDir, "backups/banco-antes-da-v1.1/${AppDatabase.FILE_NAME}")
        assertTrue(copy.exists())
        assertEquals(real.length(), copy.length())
        assertNotNull(AppDatabase.readUserVersion(copy))
    }
}
