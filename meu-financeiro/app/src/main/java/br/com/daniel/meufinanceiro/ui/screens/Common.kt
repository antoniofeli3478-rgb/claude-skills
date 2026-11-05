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
import br.com.daniel.meufinanceiro.data.SourceType
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
                listOfNotNull(l.source(calc.sourceOf(t))?.name, l.account(t.accountId)?.name, "não é renda".takeIf { !calc.isEarned(t) }).joinToString(" · "),
                t.amountCents, MoveTone.IN, if (t.loanId != null) R.LOANS else R.income(txn = t.id), "Entradas")
            TxnType.EXPENSE -> MoveItem("t${t.id}", t.date, t.description.ifBlank { "Despesa" },
                listOfNotNull(l.category(t.categoryId)?.name, PayMethod.label(t.method), l.account(t.accountId)?.name,
                    (t.sourceId ?: t.workId?.let { l.work(it)?.sourceId })?.let { "custo: ${l.source(it)?.name}" }).joinToString(" · "), -t.amountCents, MoveTone.OUT, R.expense(txn = t.id), "Despesas")
            TxnType.TRANSFER -> MoveItem("t${t.id}", t.date, t.description.ifBlank { "Transferência" },
                "${l.account(t.accountId)?.name ?: "?"} → ${l.account(t.toAccountId)?.name ?: "?"} · não é receita nem despesa", t.amountCents, MoveTone.NEUTRAL, null, "Transferências")
            TxnType.LOAN_PAYMENT -> MoveItem("t${t.id}", t.date, t.description, "${l.account(t.accountId)?.name ?: ""} · devolução de empréstimo, não é despesa", -t.amountCents, MoveTone.NEUTRAL, null, "Transferências")
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
/** Caixinhas (reserved money). The old "Gastos pessoais" envelope is not a caixinha anymore (it is the free balance). */
fun boxOptions(l: Ledger) = l.boxes.filter { !it.archived && it.kind != br.com.daniel.meufinanceiro.data.BoxKind.SPENDING }.sortedBy { it.sortOrder }.map { it.id to it.name }
val methodOptions = PayMethod.all.map { it to PayMethod.label(it) }

/* ---------------- Quick entry ---------------- */

/**
 * Confirmation of a natural-language quick entry. Nothing is saved until "Salvar".
 * Anything the parser could not understand must be answered here first.
 */
@Composable
fun QuickEntryConfirm(state: AppState, text: String, onDismiss: () -> Unit) {
    val l = state.ledger
    val fin = LocalFin.current
    val parsed = remember(text) { QuickEntryParser.parse(text, l.categories, l.sources) }
    var kind by remember { mutableStateOf(parsed.kind) }
    var amount by remember { mutableStateOf(parsed.amountCents?.let { Money.toInput(it) } ?: "") }
    var desc by remember { mutableStateOf(parsed.description) }
    var method by remember { mutableStateOf(parsed.method) }
    var cat by remember { mutableStateOf(parsed.categoryId) }
    var src by remember { mutableStateOf(parsed.sourceId) }
    var date by remember { mutableStateOf(parsed.date) }
    var inst by remember { mutableStateOf(parsed.installments) }
    var account by remember { mutableStateOf(if (parsed.method == PayMethod.CASH) l.cashAccount?.id else l.dailyAccount?.id) }
    // work cost: null = not answered yet, 0 = personal (no job)
    var workSrc by remember { mutableStateOf<Long?>(if (parsed.workCost) parsed.workSourceId else 0L) }
    var person by remember { mutableStateOf(parsed.person?.let { if (it == "mae") "Mãe" else it.replaceFirstChar { c -> c.uppercase() } } ?: "") }
    var qty by remember { mutableStateOf(parsed.quantity ?: 1) }
    var unit by remember { mutableStateOf(parsed.unitPriceCents?.let { Money.toInput(it) } ?: Money.toInput(l.settingLong(br.com.daniel.meufinanceiro.domain.Keys.NFC_PRICE, 8990))) }
    var hours by remember { mutableStateOf(l.source(parsed.sourceId)?.defaultHours?.takeIf { it > 0 }?.let { fmtHours(it).removeSuffix("h") } ?: "") }

    val v = if (kind == QuickEntryParser.Kind.NFC_SALE) Money.parse(unit)?.times(qty) else Money.parse(amount)
    val isOut = kind == QuickEntryParser.Kind.EXPENSE || kind == QuickEntryParser.Kind.NFC_MATERIAL
    val missing = buildList {
        if (v == null || v <= 0) add("valor")
        if (isOut && cat == null) add("categoria")
        if (isOut && method == null) add("forma de pagamento")
        if (kind == QuickEntryParser.Kind.EXPENSE && workSrc == null) add("se é custo de trabalho (e de qual)")
        if ((kind == QuickEntryParser.Kind.WORK_INCOME || kind == QuickEntryParser.Kind.INCOME) && src == null) add("de onde veio")
        if (kind == QuickEntryParser.Kind.LOAN && person.isBlank()) add("quem emprestou")
        if (!isOut && account == null) add("conta")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confira antes de salvar") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Entendi: " + parsed.understood.joinToString(" · "), style = MaterialTheme.typography.bodySmall, color = fin.muted)
                Dropdown("Tipo", QuickEntryParser.Kind.entries.map { it to it.label }, kind, { kind = it })
                when (kind) {
                    QuickEntryParser.Kind.NFC_SALE -> {
                        ChoiceChips("Quantidade", (1..10).map { it to "$it" }, qty, { qty = it })
                        MoneyField(unit, { unit = it }, "Preço por unidade")
                        Label("Total ${v?.let { Money.format(it) } ?: "?"} · custo médio ${Money.format(state.calc.nfcAvgUnitCost())}/un. volta para o Estoque NFC")
                    }
                    else -> MoneyField(amount, { amount = it }, "Valor")
                }
                if (kind != QuickEntryParser.Kind.NFC_SALE && kind != QuickEntryParser.Kind.LOAN) AppTextField(desc, { desc = it }, "Descrição")
                when (kind) {
                    QuickEntryParser.Kind.EXPENSE, QuickEntryParser.Kind.NFC_MATERIAL -> {
                        Dropdown("Categoria", categoryOptions(l).filter { kind != QuickEntryParser.Kind.NFC_MATERIAL || l.category(it.first)?.isNfcStock == true }, cat, { cat = it })
                        ChoiceChips("Forma de pagamento", methodOptions, method, {
                            method = it
                            account = if (it == PayMethod.CASH) l.cashAccount?.id else l.dailyAccount?.id
                        })
                        if (method == PayMethod.CREDIT) ChoiceChips("Parcelas", (1..12).map { it to "${it}x" }, inst, { inst = it })
                        else Dropdown("Conta", accountOptions(l), account, { account = it })
                        if (kind == QuickEntryParser.Kind.EXPENSE) {
                            ChoiceChips("É custo de trabalho?", listOf(0L to "Não, é pessoal") + l.sources.filter { !it.archived && it.isEarned && it.type !in SourceType.notEarned }.map { it.id to it.name },
                                workSrc, { workSrc = it })
                            if (workSrc == null) Text("Pergunta: esse gasto foi para algum trabalho? Escolha acima.", style = MaterialTheme.typography.bodySmall, color = fin.warn)
                        }
                    }
                    QuickEntryParser.Kind.WORK_INCOME -> {
                        Dropdown("Trabalho", l.sources.filter { !it.archived && it.isEarned && it.type != SourceType.NFC && it.type !in SourceType.notEarned }.map { it.id to it.name }, src, { src = it })
                        AppTextField(hours, { hours = it.filter { ch -> ch.isDigit() || ch == ',' } }, "Horas (opcional)", keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal)
                        Dropdown("Conta que recebeu", accountOptions(l), account, { account = it })
                        Label("Vai registrar o trabalho como recebido. Custos (Uber, ônibus) você pode lançar em seguida: \"uber 15 para o tio rex\".")
                    }
                    QuickEntryParser.Kind.LOAN -> {
                        AppTextField(person, { person = it }, "Quem emprestou")
                        Dropdown("Conta que recebeu", accountOptions(l), account, { account = it })
                        Label("Não é renda: fica como empréstimo a devolver.")
                    }
                    QuickEntryParser.Kind.FAMILY -> {
                        Dropdown("Conta que recebeu", accountOptions(l), account, { account = it })
                        Label("Dinheiro de família/ajuda não é renda: não entra no lucro nem na divisão 50/25/25.")
                    }
                    QuickEntryParser.Kind.NFC_SALE -> Dropdown("Conta que recebeu", accountOptions(l), account, { account = it })
                    QuickEntryParser.Kind.INCOME -> {
                        Dropdown("De onde veio", sourceOptions(l), src, { src = it })
                        Dropdown("Conta que recebeu", accountOptions(l), account, { account = it })
                    }
                }
                DateField(date, { date = it })
                if (missing.isNotEmpty()) Text("Falta: ${missing.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = fin.warn)
            }
        },
        confirmButton = {
            TextButton(enabled = missing.isEmpty(), onClick = {
                val value = v ?: return@TextButton
                val vm = state.vm
                when (kind) {
                    QuickEntryParser.Kind.EXPENSE, QuickEntryParser.Kind.NFC_MATERIAL -> {
                        val m = method!!
                        val warn = if (m != PayMethod.CREDIT) state.calc.lowBalanceWarning(account, -value) else null
                        val ws = workSrc?.takeIf { it != 0L }
                        val job = ws?.let { s -> l.works.filter { it.sourceId == s && kotlin.math.abs(it.date - date) <= 1 }.minByOrNull { kotlin.math.abs(it.date - date) }?.id }
                        vm.run(warn ?: if (ws != null) "Custo ligado a ${l.source(ws)?.name}" else "Despesa salva", onDismiss) {
                            addExpense(FinanceRepository.ExpenseInput(value, date, desc, cat, m, if (m == PayMethod.CREDIT) null else account, l.mainCard?.id, inst, workId = job, sourceId = ws))
                        }
                    }
                    QuickEntryParser.Kind.WORK_INCOME -> vm.run("Trabalho registrado", onDismiss) {
                        saveWork(br.com.daniel.meufinanceiro.data.WorkEntry(sourceId = src!!, date = date, hours = hours.replace(',', '.').toDoubleOrNull() ?: 0.0, myShareCents = value), value, account)
                    }
                    QuickEntryParser.Kind.LOAN -> vm.run("Empréstimo registrado (não é renda)", onDismiss) { addLoan(person, value, date, account!!) }
                    QuickEntryParser.Kind.FAMILY -> vm.run("Entrada registrada (não é renda)", onDismiss) {
                        addIncome(value, date, desc, account!!, l.sourceOfType(SourceType.FAMILY)?.id, Split.None)
                    }
                    QuickEntryParser.Kind.NFC_SALE -> vm.run("Venda registrada", onDismiss) {
                        saveSale(br.com.daniel.meufinanceiro.data.NfcSale(date = date, quantity = qty, unitPriceCents = Money.parse(unit)!!, unitCostCents = state.calc.nfcAvgUnitCost(date)), value, account)
                    }
                    QuickEntryParser.Kind.INCOME -> vm.run("Entrada salva", onDismiss) { addIncome(value, date, desc, account!!, src, Split.Default) }
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
