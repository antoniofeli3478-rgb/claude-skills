package br.com.daniel.meufinanceiro.backup

import android.content.Context
import android.net.Uri
import br.com.daniel.meufinanceiro.data.BackupData
import br.com.daniel.meufinanceiro.data.FinanceRepository
import br.com.daniel.meufinanceiro.data.PayMethod
import br.com.daniel.meufinanceiro.data.TxnType
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.FinanceCalc
import br.com.daniel.meufinanceiro.domain.Ledger
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.domain.Period
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Backup / restore / CSV / report export. Everything stays on the phone unless Daniel shares the file. */
class BackupManager(private val context: Context, private val repo: FinanceRepository) {
    private val json = Json { prettyPrint = false; ignoreUnknownKeys = true; encodeDefaults = true }
    private val autoDir get() = File(context.filesDir, "backups").apply { mkdirs() }

    suspend fun exportJson(): String = json.encodeToString(BackupData.serializer(), repo.exportAll())

    fun parse(text: String): BackupData = json.decodeFromString(BackupData.serializer(), text)

    suspend fun writeTo(uri: Uri, text: String) {
        context.contentResolver.openOutputStream(uri, "wt")?.use { it.write(text.toByteArray(Charsets.UTF_8)) } ?: error("Não foi possível gravar o arquivo")
    }

    fun readFrom(uri: Uri): String =
        context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) } ?: error("Não foi possível ler o arquivo")

    /** Restores from a file. Takes an automatic safety snapshot first. */
    suspend fun restore(text: String) {
        val data = parse(text) // validates before touching anything
        autoSnapshot(force = true, tag = "antes-de-restaurar")
        repo.restoreAll(data)
    }

    /** Daily automatic copy inside the app (keeps the last 10). Protects against mistakes, not against uninstalling. */
    suspend fun autoSnapshot(force: Boolean = false, tag: String = "auto"): File? {
        val today = Dates.today()
        val files = jsonFiles()
        if (!force && files.any { it.name.startsWith("auto-" + Dates.format(today).replace('/', '-')) }) return null
        val stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm"))
        val f = File(autoDir, "$tag-$stamp.json")
        f.writeText(exportJson())
        // Keeps the 10 newest daily copies. Copies taken before a restore/upgrade and folders are never deleted here.
        jsonFiles().filter { it.name.startsWith("auto-") }.sortedByDescending { it.lastModified() }.drop(10).forEach { it.delete() }
        return f
    }

    private fun jsonFiles(): List<File> = autoDir.listFiles()?.filter { it.isFile && it.name.endsWith(".json") } ?: emptyList()

    fun snapshots(): List<File> = jsonFiles().sortedByDescending { it.lastModified() }

    /** Semicolon CSV with comma decimals, opens correctly in Excel / Google Sheets in Portuguese. */
    fun csv(l: Ledger): String {
        val sb = StringBuilder("﻿")
        sb.append("Data;Tipo;Descrição;Categoria/Fonte;Conta/Cartão;Forma;Valor;Parcelas;Observações\n")
        fun esc(s: String) = "\"" + s.replace("\"", "\"\"") + "\""
        val calc = FinanceCalc(l)
        l.txns.sortedBy { it.date }.forEach { t ->
            val tipo = when (t.type) {
                TxnType.INCOME -> "Entrada"; TxnType.EXPENSE -> "Despesa"; TxnType.TRANSFER -> "Transferência"
                TxnType.CARD_PAYMENT -> "Pagamento de fatura"; else -> "Ajuste de saldo"
            }
            val catSrc = if (t.type == TxnType.INCOME) l.source(calc.sourceOf(t))?.name ?: "" else l.category(t.categoryId)?.name ?: ""
            val conta = l.account(t.accountId)?.name.orEmpty() + (l.account(t.toAccountId)?.let { " → " + it.name } ?: "")
            val valor = if (t.type == TxnType.EXPENSE || t.type == TxnType.CARD_PAYMENT) -t.amountCents else t.amountCents
            sb.append(listOf(Dates.format(t.date), tipo, esc(t.description), esc(catSrc), esc(conta), PayMethod.label(t.method),
                Money.format(valor, withSymbol = false), "", esc(t.notes)).joinToString(";")).append('\n')
        }
        l.purchases.sortedBy { it.date }.forEach { p ->
            sb.append(listOf(Dates.format(p.date), "Compra no crédito", esc(p.description), esc(l.category(p.categoryId)?.name ?: ""),
                esc(l.card(p.cardId)?.name ?: ""), "Crédito", Money.format(-p.totalCents, withSymbol = false), p.installments.toString(), esc(p.notes)).joinToString(";")).append('\n')
        }
        return sb.toString()
    }

    /** Printable HTML report for a period (open in the browser, share or "print to PDF"). */
    fun htmlReport(l: Ledger, period: Period): String {
        val c = FinanceCalc(l)
        val r = c.report(period)
        fun row(a: String, b: String) = "<tr><td>$a</td><td class=n>$b</td></tr>"
        val cats = r.expenseByCategory.joinToString("") { row(l.category(it.first)?.name ?: "Sem categoria", Money.format(it.second)) }
        val srcs = r.incomeBySource.joinToString("") { row(l.source(it.first)?.name ?: "Sem fonte", Money.format(it.second)) }
        val boxes = c.activeBoxes.joinToString("") { row(it.name, Money.format(c.boxBalance(it.id))) }
        val accs = c.activeAccounts.joinToString("") { row(it.name, Money.format(c.accountBalance(it.id))) }
        return """<!doctype html><html lang="pt-BR"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Relatório financeiro</title><style>
body{font-family:system-ui,sans-serif;margin:24px;color:#111714;background:#fff;max-width:720px}
h1{font-size:22px;margin:0 0 4px}h2{font-size:16px;margin:28px 0 8px}p{color:#56635b;margin:0}
table{width:100%;border-collapse:collapse;font-size:14px}td{padding:8px 4px;border-bottom:1px solid #e3e9e5}
.n{text-align:right;font-variant-numeric:tabular-nums}.k{display:grid;grid-template-columns:1fr 1fr;gap:8px;margin-top:16px}
.k div{border:1px solid #e3e9e5;border-radius:12px;padding:12px}.k b{display:block;font-size:18px;margin-top:4px}
</style></head><body>
<h1>Relatório financeiro</h1><p>${period.label} · gerado em ${Dates.format(Dates.today())} pelo Meu Financeiro</p>
<div class=k><div>Entradas<b>${Money.format(r.incomeCents)}</b></div><div>Saídas<b>${Money.format(r.expenseCents)}</b></div>
<div>Gastos pessoais<b>${Money.format(r.personalExpenseCents)}</b></div><div>Reinvestido em estoque NFC<b>${Money.format(r.nfcStockCents)}</b></div>
<div>Lucro líquido NFC<b>${Money.format(r.nfc.netProfitCents)}</b></div><div>Saldo do período<b>${Money.format(r.balanceCents)}</b></div></div>
<h2>Para onde foi o dinheiro</h2><table>$cats</table>
<h2>De onde veio o dinheiro</h2><table>$srcs</table>
<h2>Caixinhas hoje</h2><table>$boxes</table>
<h2>Contas hoje</h2><table>$accs${row("<b>Fatura do cartão em aberto</b>", Money.format(-c.cardDebt()))}</table>
</body></html>"""
    }
}
