@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package br.com.daniel.meufinanceiro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.daniel.meufinanceiro.data.PayMethod
import br.com.daniel.meufinanceiro.data.TxnType
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.FinanceCalc
import br.com.daniel.meufinanceiro.domain.Ledger
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.domain.Period
import br.com.daniel.meufinanceiro.domain.PeriodKind
import br.com.daniel.meufinanceiro.domain.QuickEntryParser
import br.com.daniel.meufinanceiro.data.FinanceRepository
import br.com.daniel.meufinanceiro.data.Split
import br.com.daniel.meufinanceiro.ui.AppState
import br.com.daniel.meufinanceiro.ui.R
import br.com.daniel.meufinanceiro.ui.components.AppTextField
import br.com.daniel.meufinanceiro.ui.components.ChoiceChips
import br.com.daniel.meufinanceiro.ui.components.Chip
import br.com.daniel.meufinanceiro.ui.components.DateField
import br.com.daniel.meufinanceiro.ui.components.Dropdown
import br.com.daniel.meufinanceiro.ui.components.Label
import br.com.daniel.meufinanceiro.ui.components.MoneyField
import br.com.daniel.meufinanceiro.ui.theme.FinColors
import br.com.daniel.meufinanceiro.ui.theme.LocalFin
import java.time.Instant
import java.time.ZoneOffset

/* ---------------- Period selector ---------------- */

@Composable
fun PeriodSelector(period: Period, onChange: (Period) -> Unit) {
    var custom by remember { mutableStateOf(false) }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PeriodKind.entries.forEach { k ->
            Chip(if (k == PeriodKind.CUSTOM && period.kind == k) period.label else k.label, period.kind == k) {
                if (k == PeriodKind.CUSTOM) custom = true else onChange(Period.of(k))
            }
        }
    }
    if (custom) {
        val st = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { custom = false },
            confirmButton = {
                TextButton(enabled = st.selectedStartDateMillis != null, onClick = {
                    fun d(ms: Long) = Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate().toEpochDay()
                    val s = st.selectedStartDateMillis!!
                    val e = st.selectedEndDateMillis ?: s
                    onChange(Period.of(PeriodKind.CUSTOM, customStart = d(s), customEnd = d(e)))
                    custom = false
                }) { Text("Aplicar") }
            },
            dismissButton = { TextButton(onClick = { custom = false }) { Text("Cancelar") } },
        ) { DateRangePicker(state = st, title = { Text("  Escolha o período") }, modifier = Modifier.height(480.dp)) }
    }
}

/* ---------------- Movement list items ---------------- */

enum class MoveTone { IN, OUT, NEUTRAL }

data class MoveItem(
    val key: String, val date: Long, val title: String, val subtitle: String, val amountCents: Long,
    val tone: MoveTone, val route: String?, val kind: String,
)

fun MoveTone.color(f: FinColors): Color = when (this) { MoveTone.IN -> f.income; MoveTone.OUT -> f.expense; MoveTone.NEUTRAL -> f.muted }

/** Unified history: account transactions + credit-card purchases. */
fun movements(l: Ledger, calc: FinanceCalc): List<MoveItem> {
    val tx = l.txns.map { t ->
        when (t.type) {
            TxnType.INCOME -> MoveItem("t${t.id}", t.date, t.description.ifBlank { "Entrada" },
                listOfNotNull(l.source(calc.sourceOf(t))?.name, l.account(t.accountId)?.name).joinToString(" · "), t.amountCents, MoveTone.IN, R.income(txn = t.id), "Entradas")
            TxnType.EXPENSE -> MoveItem("t${t.id}", t.date, t.description.ifBlank { "Despesa" },
                listOfNotNull(l.category(t.categoryId)?.name, PayMethod.label(t.method), l.account(t.accountId)?.name).joinToString(" · "), -t.amountCents, MoveTone.OUT, R.expense(txn = t.id), "Despesas")
            TxnType.TRANSFER -> MoveItem("t${t.id}", t.date, t.description.ifBlank { "Transferência" },
                "${l.account(t.accountId)?.name ?: "?"} → ${l.account(t.toAccountId)?.name ?: "?"} · não é receita nem despesa", t.amountCents, MoveTone.NEUTRAL, null, "Transferências")
            TxnType.CARD_PAYMENT -> MoveItem("t${t.id}", t.date, t.description, "${l.account(t.accountId)?.name ?: ""} · não conta como gasto de novo", -t.amountCents, MoveTone.NEUTRAL, null, "Transferências")
            else -> MoveItem("t${t.id}", t.date, "Ajuste de saldo", l.account(t.accountId)?.name ?: "", t.amountCents, MoveTone.NEUTRAL, null, "Transferências")
        }
    }
    val cp = l.purchases.map { p ->
        MoveItem("p${p.id}", p.date, p.description,
            listOfNotNull(l.category(p.categoryId)?.name, "Crédito" + if (p.installments > 1) " ${p.installments}x de ${Money.format(p.totalCents / p.installments)}" else "", l.card(p.cardId)?.name).joinToString(" · "),
            -p.totalCents, MoveTone.OUT, R.expense(purchase = p.id), "Despesas")
    }
    return (tx + cp).sortedWith(compareByDescending<MoveItem> { it.date }.thenByDescending { it.key })
}

/* ---------------- Option lists ---------------- */

fun accountOptions(l: Ledger) = l.accounts.filter { !it.archived }.map { it.id to it.name }
fun categoryOptions(l: Ledger) = l.categories.filter { !it.archived }.map { it.id to it.name }
fun sourceOptions(l: Ledger) = l.sources.filter { !it.archived }.map { it.id to it.name }
fun boxOptions(l: Ledger) = l.boxes.filter { !it.archived }.sortedBy { it.sortOrder }.map { it.id to it.name }
val methodOptions = PayMethod.all.map { it to PayMethod.label(it) }

/* ---------------- Quick entry ---------------- */

/**
 * Confirmation of a natural-language quick entry. Nothing is saved until Daniel taps "Salvar".
 */
@Composable
fun QuickEntryConfirm(state: AppState, text: String, onDismiss: () -> Unit) {
    val l = state.ledger
    val parsed = remember(text) { QuickEntryParser.parse(text, l.categories, l.sources) }
    var isIncome by remember { mutableStateOf(parsed.isIncome) }
    var amount by remember { mutableStateOf(parsed.amountCents?.let { Money.toInput(it) } ?: "") }
    var desc by remember { mutableStateOf(parsed.description) }
    var method by remember { mutableStateOf(parsed.method ?: PayMethod.PIX) }
    var cat by remember { mutableStateOf(parsed.categoryId) }
    var src by remember { mutableStateOf(parsed.sourceId) }
    var date by remember { mutableStateOf(parsed.date) }
    var inst by remember { mutableStateOf(parsed.installments) }
    var account by remember { mutableStateOf(if (method == PayMethod.CASH) l.cashAccount?.id else l.dailyAccount?.id) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confira antes de salvar") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Entendi: " + parsed.understood.joinToString(" · "), style = MaterialTheme.typography.bodySmall, color = LocalFin.current.muted)
                ChoiceChips(null, listOf(false to "Despesa", true to "Entrada"), isIncome, { isIncome = it })
                MoneyField(amount, { amount = it }, "Valor")
                AppTextField(desc, { desc = it }, "Descrição")
                if (isIncome) {
                    Dropdown("Fonte", sourceOptions(l), src, { src = it })
                    Dropdown("Conta que recebeu", accountOptions(l), account, { account = it })
                } else {
                    Dropdown("Categoria", categoryOptions(l), cat, { cat = it })
                    ChoiceChips("Forma de pagamento", methodOptions, method, {
                        method = it
                        account = if (it == PayMethod.CASH) l.cashAccount?.id else l.dailyAccount?.id
                    })
                    if (method == PayMethod.CREDIT) {
                        ChoiceChips("Parcelas", (1..12).map { it to "${it}x" }, inst, { inst = it })
                    } else {
                        Dropdown("Conta", accountOptions(l), account, { account = it })
                    }
                }
                DateField(date, { date = it })
            }
        },
        confirmButton = {
            TextButton(enabled = Money.parse(amount)?.let { it > 0 } == true, onClick = {
                val v = Money.parse(amount) ?: return@TextButton
                if (isIncome) {
                    val acc = account ?: l.dailyAccount?.id ?: return@TextButton
                    state.vm.run("Entrada salva e dividida nas caixinhas", onDismiss) { addIncome(v, date, desc, acc, src, Split.Default) }
                } else {
                    val warn = if (method != PayMethod.CREDIT) state.calc.lowBalanceWarning(account, -v) else null
                    state.vm.run(warn ?: "Despesa salva", onDismiss) {
                        addExpense(FinanceRepository.ExpenseInput(v, date, desc, cat, method, if (method == PayMethod.CREDIT) null else account, l.mainCard?.id, inst))
                    }
                }
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
fun SmallNote(text: String) {
    Spacer(Modifier.height(2.dp))
    Label(text)
}

fun dateTitle(d: Long) = Dates.relative(d)
