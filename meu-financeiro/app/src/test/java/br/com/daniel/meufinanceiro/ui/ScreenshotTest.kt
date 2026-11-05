package br.com.daniel.meufinanceiro.ui

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onRoot
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.daniel.meufinanceiro.MainActivity
import br.com.daniel.meufinanceiro.data.BoxKind
import br.com.daniel.meufinanceiro.data.FinanceRepository
import br.com.daniel.meufinanceiro.data.NfcBatch
import br.com.daniel.meufinanceiro.data.NfcSale
import br.com.daniel.meufinanceiro.data.PayMethod
import br.com.daniel.meufinanceiro.data.SourceType
import br.com.daniel.meufinanceiro.data.WorkEntry
import br.com.daniel.meufinanceiro.domain.Dates
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/** Renders key screens with sample data to app/build/screens (visual review only; run with -Dscreens=true). */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class ScreenshotTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test fun screens() {
        if (System.getProperty("screens") != "true") return
        rule.waitUntil(15_000) { rule.onAllNodesWithText("Saldo livre para gastar").fetchSemanticsNodes().isNotEmpty() }
        val vm = ViewModelProvider(rule.activity)[MainViewModel::class.java]
        val t = Dates.today()
        runBlocking {
            vm.repo.setSetting("review_balances", null)
            val r = vm.repo
            val l = r.snapshot()
            val inter = l.accounts.first { it.isDaily }.id
            val nu = l.accounts.first { it.name == "Nubank" }.id
            val cash = l.accounts.first { it.kind == "CASH" }.id
            fun cat(n: String) = l.categories.first { it.name == n }.id
            fun src(tp: String) = l.sources.first { it.type == tp }.id
            r.adjustBalance(nu, 0, 85000, t - 40)
            r.boxDeposit(l.boxOfKind(BoxKind.COMPUTER)!!.id, 60000, t - 40)
            r.boxDeposit(l.boxOfKind(BoxKind.EMERGENCY)!!.id, 25000, t - 40)
            val tr = cat("Transporte")
            for (i in 0..8) r.saveWork(WorkEntry(sourceId = src(SourceType.TIO_REX), date = t - i * 4, hours = 4.0, myShareCents = 8000), 8000, inter,
                costs = listOf(FinanceRepository.ExpenseInput(1500, t - i * 4, "Uber ida", tr, PayMethod.PIX, inter), FinanceRepository.ExpenseInput(500, t - i * 4, "Ônibus volta", tr, PayMethod.PIX, inter)))
            r.addLoan("Mãe", 3000, t - 1, inter)
            r.saveWork(WorkEntry(sourceId = src(SourceType.RECREACAO), date = t - 6, title = "Festa infantil", hours = 4.0, myShareCents = 12000), 12000, inter)
            r.saveWork(WorkEntry(sourceId = src(SourceType.PIPOCA), date = t - 2, title = "Festa junina", hours = 5.0, grossCents = 60000, costsCents = 2000, myShareCents = 12000), 0, null)
            r.saveBatch(NfcBatch(date = t - 30, quantity = 30, totalCostCents = 35000), PayMethod.PIX, nu)
            r.saveSale(NfcSale(date = t - 5, quantity = 2, unitPriceCents = 7990, unitCostCents = 1166), 15980, nu)
            r.saveSale(NfcSale(date = t - 1, quantity = 1, unitPriceCents = 8990, unitCostCents = 1166), 0, null)
            listOf(Triple("Almoço", 2500, "Alimentação"), Triple("Uber", 1800, "Transporte"), Triple("Lanche", 1200, "Alimentação"), Triple("Mercado", 9800, "Alimentação"), Triple("Cinema", 3500, "Lazer"))
                .forEachIndexed { i, (d, v, c) -> r.addExpense(FinanceRepository.ExpenseInput(v.toLong(), t - i * 3, d, cat(c), PayMethod.PIX, inter)) }
            r.addExpense(FinanceRepository.ExpenseInput(30000, t - 3, "Tênis", cat("Compras pessoais"), PayMethod.CREDIT, null, l.mainCard!!.id, 3))
            r.addExpense(FinanceRepository.ExpenseInput(16000, t - 8, "Academia", cat("Academia"), PayMethod.CREDIT, null, l.mainCard!!.id))
        }
        runBlocking { vm.assistant.send("Quanto posso gastar?"); vm.assistant.send("Mude meu limite mínimo de saldo para 50") }
        vm.refreshChat()
        rule.waitForIdle()
        val out = File("build/screens").apply { mkdirs() }
        fun shot(name: String) {
            rule.waitForIdle(); Thread.sleep(300); rule.mainClock.advanceTimeBy(1500); rule.waitForIdle()
            var bmp: Bitmap? = null
            rule.runOnUiThread {
                val v = rule.activity.window.decorView
                bmp = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888).also { v.draw(android.graphics.Canvas(it)) }
            }
            val b = bmp!!
            val bmpOut = b
            File(out, "$name.png").outputStream().use { bmpOut.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
        for (theme in listOf("dark", "light")) {
            runBlocking { vm.repo.setSetting("theme", theme) }
            rule.waitForIdle()
            rule.runOnUiThread { navForTest?.navigate(R.HOME) }
            shot("$theme-home")
            for ((n, route) in listOf("card" to R.CARD, "boxes" to R.BOXES, "income" to R.INCOME, "reports" to R.REPORTS, "assistant" to R.ASSISTANT, "work" to R.work(), "alerts" to R.ALERTS, "txns" to R.TXNS)) {
                rule.runOnUiThread { navForTest?.navigate(route) }
                shot("$theme-$n")
            }
        }
    }
}
