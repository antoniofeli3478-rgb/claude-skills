package br.com.daniel.meufinanceiro.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.daniel.meufinanceiro.MainActivity
import br.com.daniel.meufinanceiro.MeuFinanceiroApp
import br.com.daniel.meufinanceiro.data.AppDatabase
import br.com.daniel.meufinanceiro.data.FinanceRepository
import br.com.daniel.meufinanceiro.data.TxnType
import br.com.daniel.meufinanceiro.domain.FinanceCalc
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Starts the real MainActivity (real Application, real on-disk Room DB) and drives the UI.
 * This is the "does it open?" test: if startup crashes, this fails.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi") // ~ Galaxy S23 FE screen
class AppLaunchTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    /** The exact ViewModel (and repository) the UI is using. */
    private fun vm(): MainViewModel = androidx.lifecycle.ViewModelProvider(rule.activity)[MainViewModel::class.java]

    private fun waitText(text: String, timeout: Long = 10_000) {
        rule.waitUntil(timeout) { rule.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty() }
    }

    @Test fun appOpensAndShowsDashboard() {
        waitText("Saldo total disponível")
        rule.onNodeWithText("Saldo total disponível").assertIsDisplayed()
        rule.onNodeWithText("Nova despesa").assertIsDisplayed()
        rule.onNodeWithText("Nova entrada").assertIsDisplayed()
        rule.onAllNodesWithText("Início").onFirst().assertIsDisplayed()
    }

    @Test fun quickExpenseIsSavedToTheDatabase() {
        waitText("Nova despesa")
        rule.onNodeWithText("Nova despesa").performClick()
        waitText("Salvar despesa")
        rule.onNode(hasSetTextAction() and hasText("Valor")).performTextInput("25")
        rule.onNode(hasSetTextAction() and hasText("Descrição", substring = true)).performTextInput("Almoço")
        rule.onNodeWithText("Alimentação").performScrollTo().performClick()
        rule.onNodeWithText("Salvar despesa").performScrollTo().performClick()
        waitText("Saldo total disponível")
        rule.waitForIdle()
        rule.waitUntil(5000) { runBlocking { vm().repo.snapshot() }.txns.isNotEmpty() }
        val l = runBlocking { vm().repo.snapshot() }
        val e = l.txns.single { it.type == TxnType.EXPENSE }
        assertEquals(2500L, e.amountCents)
        assertEquals("Almoço", e.description)
        assertEquals("Alimentação", l.category(e.categoryId)?.name)
    }

    @Test fun everyMainScreenOpensWithoutCrashing() {
        waitText("Saldo total disponível")
        for (tab in listOf("Extrato", "Rendas", "Cartão", "Mais")) {
            rule.onAllNodesWithText(tab).onFirst().performClick()
            rule.waitForIdle()
        }
        val routes = listOf(R.BOXES, R.BILLS, R.REPORTS, R.ANALYST, R.NFC, R.SETTINGS, R.TRANSFER, R.RECEIVE, R.BATCH,
            R.expense(), R.income(), R.work(), R.sale(), R.settings("accounts"), R.settings("card"), R.settings("split"),
            R.settings("categories"), R.settings("sources"), R.settings("nfc"), R.settings("security"), R.settings("backup"), R.settings("ai"))
        val l = runBlocking { vm().repo.snapshot() }
        val all = routes + listOf(R.box(l.boxes.first().id), R.source(l.sources.first { it.type == "TIO_REX" }.id))
        for (r in all) {
            rule.runOnUiThread { navForTest?.navigate(r) }
            rule.waitForIdle()
        }
        assertTrue(true)
    }

    @Test fun themeToggleSwitchesToLightAndBack() {
        waitText("Saldo total disponível")
        val app = vm()
        rule.onNodeWithContentDescription("modo", substring = true).performClick()
        rule.waitForIdle()
        rule.waitUntil(5000) { runBlocking { app.repo.snapshot() }.setting("theme") != null }
        val first = runBlocking { app.repo.snapshot() }.setting("theme")
        assertEquals("dark", first) // Robolectric device is in light mode -> first tap goes dark
        rule.waitUntil(5000) { rule.onAllNodes(androidx.compose.ui.test.hasContentDescription("modo claro", substring = true)).fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithContentDescription("modo claro", substring = true).performClick()
        rule.waitUntil(5000) { runBlocking { app.repo.snapshot() }.setting("theme") != first }
        assertTrue(runBlocking { app.repo.snapshot() }.setting("theme") in setOf("light", "dark"))
    }
}

/** Data written to the on-disk database must survive closing and reopening it (app closed / phone restarted). */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class PersistenceTest {
    @Test fun dataSurvivesReopeningTheDatabase() = runBlocking {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        ctx.deleteDatabase(AppDatabase.FILE_NAME)
        val db1 = AppDatabase.build(ctx)
        val repo1 = FinanceRepository(db1)
        repo1.seedIfEmpty()
        val l1 = repo1.snapshot()
        repo1.addIncome(20000, 20000, "Tio Rex", l1.accounts.first().id, null)
        db1.close()

        val db2 = AppDatabase.build(ctx)
        val l2 = FinanceRepository(db2).snapshot()
        assertEquals(1, l2.txns.size)
        assertEquals(20000L, FinanceCalc(l2, 20000).totalAvailable())
        assertEquals(2, l2.moves.size) // v2: the free 50% has no box movement
        db2.close()
    }
}

/** Opens the app on the oldest supported Android (8.0), Android 11 and Android 15 (target). */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [26, 30, 35], qualifiers = "w411dp-h891dp-xxhdpi")
class AppLaunchAcrossAndroidVersionsTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()
    @Test fun opens() {
        rule.waitUntil(15_000) { rule.onAllNodesWithText("Saldo total disponível").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithText("Nova despesa").assertIsDisplayed()
    }
}
