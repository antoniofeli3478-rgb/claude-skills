package br.com.daniel.meufinanceiro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.daniel.meufinanceiro.data.DistMode
import br.com.daniel.meufinanceiro.data.FinanceRepository
import br.com.daniel.meufinanceiro.data.PayMethod
import br.com.daniel.meufinanceiro.data.Split
import br.com.daniel.meufinanceiro.data.Txn
import br.com.daniel.meufinanceiro.data.TxnType
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.Distribution
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.ui.AppState
import br.com.daniel.meufinanceiro.ui.components.AppTextField
import br.com.daniel.meufinanceiro.ui.components.Banner
import br.com.daniel.meufinanceiro.ui.components.ChoiceChips
import br.com.daniel.meufinanceiro.ui.components.ConfirmDialog
import br.com.daniel.meufinanceiro.ui.components.DateField
import br.com.daniel.meufinanceiro.ui.components.Dropdown
import br.com.daniel.meufinanceiro.ui.components.FinCard
import br.com.daniel.meufinanceiro.ui.components.Label
import br.com.daniel.meufinanceiro.ui.components.MoneyField
import br.com.daniel.meufinanceiro.ui.components.PrimaryButton
import br.com.daniel.meufinanceiro.ui.components.ScreenScaffold
import br.com.daniel.meufinanceiro.ui.components.ScrollColumn
import br.com.daniel.meufinanceiro.ui.components.SwitchRow
import br.com.daniel.meufinanceiro.ui.theme.LocalFin

/* ======================= Nova despesa ======================= */

@Composable
fun ExpenseForm(s: AppState, txnId: Long, purchaseId: Long, billId: Long) {
    val l = s.ledger
    val fin = LocalFin.current
    val oldTxn = remember { l.txns.firstOrNull { it.id == txnId && txnId != 0L } }
    val oldP = remember { l.purchases.firstOrNull { it.id == purchaseId && purchaseId != 0L } }
    val bill = remember { l.bill(billId.takeIf { it != 0L } ?: oldTxn?.billId ?: oldP?.billId) }
    val editing = oldTxn != null || oldP != null

    var amount by remember { mutableStateOf((oldTxn?.amountCents ?: oldP?.totalCents ?: bill?.amountCents?.takeIf { it > 0 })?.let { Money.toInput(it) } ?: "") }
    var desc by remember { mutableStateOf(oldTxn?.description ?: oldP?.description ?: bill?.name ?: "") }
    var cat by remember { mutableStateOf(oldTxn?.categoryId ?: oldP?.categoryId ?: bill?.categoryId) }
    var method by remember { mutableStateOf(oldTxn?.method ?: (if (oldP != null) PayMethod.CREDIT else bill?.method ?: PayMethod.PIX)) }
    var account by remember { mutableStateOf(oldTxn?.accountId ?: bill?.accountId ?: if (method == PayMethod.CASH) l.cashAccount?.id else l.dailyAccount?.id) }
    var card by remember { mutableStateOf(oldP?.cardId ?: bill?.cardId ?: l.mainCard?.id) }
    var installments by remember { mutableStateOf(oldP?.installments ?: 1) }
    var date by remember { mutableStateOf(oldTxn?.date ?: oldP?.date ?: s.calc.today) }
    var box by remember { mutableStateOf(oldTxn?.boxId ?: oldP?.boxId) }
    var recurring by remember { mutableStateOf(oldTxn?.recurring ?: oldP?.recurring ?: (bill != null)) }
    var notes by remember { mutableStateOf(oldTxn?.notes ?: oldP?.notes ?: "") }
    var more by remember { mutableStateOf(editing) }
    var askDelete by remember { mutableStateOf(false) }

    val value = Money.parse(amount)
    val effectiveBox = box ?: l.defaultBoxForCategory(cat)?.id
    val currentEffect = oldTxn?.let { s.calc.effectOn(it, account ?: -1) } ?: 0L
    val warn = if (method != PayMethod.CREDIT && value != null) s.calc.lowBalanceWarning(account, -value - currentEffect) else null

    ScreenScaffold(if (editing) "Editar despesa" else if (bill != null) "Pagar ${bill.name}" else "Nova despesa", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            MoneyField(amount, { amount = it }, "Valor", autoFocus = !editing && bill == null)
            AppTextField(desc, { desc = it }, "Descrição (ex.: almoço)")
            ChoiceChips("Forma de pagamento", methodOptions, method, {
                method = it
                if (it == PayMethod.CASH) account = l.cashAccount?.id ?: account
                else if (l.account(account)?.kind == "CASH") account = l.dailyAccount?.id ?: account
            })
            if (method == PayMethod.CREDIT) {
                if (l.cards.count { !it.archived } > 1) Dropdown("Cartão", l.cards.filter { !it.archived }.map { it.id to it.name }, card, { card = it })
                ChoiceChips("Parcelas", listOf(1, 2, 3, 4, 5, 6, 8, 10, 12).map { it to if (it == 1) "À vista" else "${it}x" }, installments, { installments = it })
                if (value != null && installments > 1) Label("${installments}x de ${Money.format(value / installments)}: cada parcela entra na fatura do seu mês")
                Label("No crédito: não sai da conta agora. Entra na fatura do ${l.card(card)?.name ?: "cartão"}.")
            } else {
                Dropdown("Conta usada", accountOptions(l), account, { account = it })
            }
            ChoiceChips("Categoria", categoryOptions(l), cat, { cat = it })
            DateField(date, { date = it })
            warn?.let { Banner(it, fin.warn) }

            TextButton(onClick = { more = !more }) { Text(if (more) "Menos opções" else "Mais opções (caixinha, recorrente, observações)") }
            if (more) {
                Dropdown("Caixinha que paga", boxOptions(l), effectiveBox, { box = it })
                SwitchRow("Despesa recorrente", recurring, { recurring = it }, "Marque para contas que se repetem todo mês")
                AppTextField(notes, { notes = it }, "Observações", singleLine = false)
            }

            Spacer(Modifier.height(4.dp))
            PrimaryButton("Salvar despesa", enabled = value != null && value > 0, onClick = {
                val input = FinanceRepository.ExpenseInput(
                    amountCents = value!!, date = date, description = desc.ifBlank { l.category(cat)?.name ?: "Despesa" }, categoryId = cat,
                    method = method, accountId = if (method == PayMethod.CREDIT) null else account, cardId = card, installments = if (method == PayMethod.CREDIT) installments else 1,
                    boxId = box, recurring = recurring, notes = notes,
                    billId = bill?.id, billMonth = if (bill != null) (oldTxn?.billMonth ?: oldP?.billMonth ?: Dates.ym(date)) else null,
                    nfcBatchId = oldTxn?.nfcBatchId ?: oldP?.nfcBatchId,
                )
                s.vm.run(warn ?: if (editing) "Despesa atualizada" else "Despesa salva", onDone = s::back) {
                    if (editing) replaceExpense(oldTxn?.id, oldP?.id, input) else addExpense(input)
                }
            })
            if (editing) OutlinedButton(onClick = { askDelete = true }, modifier = Modifier.padding(top = 4.dp)) { Text("Excluir despesa", color = MaterialTheme.colorScheme.error) }
        }
    }
    if (askDelete) ConfirmDialog("Excluir despesa?", "Os saldos, a fatura e as caixinhas serão recalculados.", "Excluir", danger = true, onConfirm = {
        s.vm.run("Despesa excluída", onDone = s::back) { oldTxn?.let { deleteTxn(it.id) }; oldP?.let { deletePurchase(it.id) } }
    }, onDismiss = { askDelete = false })
}

/* ======================= Nova entrada ======================= */

@Composable
fun SplitEditor(s: AppState, amountCents: Long?, mode: String, onMode: (String) -> Unit, custom: MutableMap<Long, String>) {
    val l = s.ledger
    val fin = LocalFin.current
    val rulesLabel = l.rules.sortedBy { l.box(it.boxId)?.sortOrder ?: 0 }.joinToString(" / ") { Money.formatPercent(it.basisPoints) }
    ChoiceChips("Divisão nas caixinhas", listOf(DistMode.DEFAULT to "Padrão ($rulesLabel)", DistMode.NONE to "Não dividir", DistMode.CUSTOM to "Personalizar"), mode, onMode)
    when (mode) {
        DistMode.DEFAULT -> if (amountCents != null && amountCents > 0) {
            val rules = l.rules.sortedBy { l.box(it.boxId)?.sortOrder ?: 0 }.map { it.boxId to it.basisPoints }
            FinCard {
                Distribution.split(amountCents, rules).forEach { (b, v) ->
                    Row(Modifier.padding(vertical = 2.dp)) {
                        Text(l.box(b)?.name ?: "?", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text(Money.format(v), style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
        }
        DistMode.NONE -> Label("O valor fica como \"dinheiro sem caixinha\" até você distribuir.")
        DistMode.CUSTOM -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            boxOptions(l).forEach { (id, name) -> MoneyField(custom[id] ?: "", { custom[id] = it }, name) }
            val sum = custom.values.sumOf { Money.parse(it) ?: 0 }
            val rest = (amountCents ?: 0) - sum
            Label(if (rest >= 0) "Sem caixinha: ${Money.format(rest)}" else "Passou ${Money.format(-rest)} do valor", color = if (rest < 0) fin.expense else fin.muted)
        }
    }
}

fun buildSplit(mode: String, custom: Map<Long, String>): Split = when (mode) {
    DistMode.NONE -> Split.None
    DistMode.CUSTOM -> Split.Custom(custom.mapNotNull { (k, v) -> Money.parse(v)?.takeIf { it > 0 }?.let { k to it } })
    else -> Split.Default
}

@Composable
fun IncomeForm(s: AppState, txnId: Long, sourceId: Long) {
    val l = s.ledger
    val old = remember { l.txns.firstOrNull { it.id == txnId && txnId != 0L && it.type == TxnType.INCOME } }
    val editing = old != null
    var amount by remember { mutableStateOf(old?.amountCents?.let { Money.toInput(it) } ?: "") }
    var src by remember { mutableStateOf(old?.let { s.calc.sourceOf(it) } ?: sourceId.takeIf { it != 0L }) }
    var desc by remember { mutableStateOf(old?.description ?: "") }
    var account by remember { mutableStateOf(old?.accountId ?: l.dailyAccount?.id) }
    var date by remember { mutableStateOf(old?.date ?: s.calc.today) }
    var mode by remember { mutableStateOf(old?.distMode ?: DistMode.DEFAULT) }
    val custom = remember {
        mutableStateMapOf<Long, String>().apply {
            if (old?.distMode == DistMode.CUSTOM) l.moves.filter { it.txnId == old.id }.forEach { put(it.boxId, Money.toInput(it.amountCents)) }
        }
    }
    var notes by remember { mutableStateOf(old?.notes ?: "") }
    var askDelete by remember { mutableStateOf(false) }
    val value = Money.parse(amount)
    val linked = old?.workId != null || old?.nfcSaleId != null

    ScreenScaffold(if (editing) "Editar entrada" else "Nova entrada", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            MoneyField(amount, { amount = it }, "Valor recebido", autoFocus = !editing)
            ChoiceChips("De onde veio", sourceOptions(l), src, { src = it })
            AppTextField(desc, { desc = it }, "Descrição (opcional)")
            Dropdown("Conta que recebeu", accountOptions(l), account, { account = it })
            DateField(date, { date = it })
            if (old?.nfcSaleId != null) Label("Pagamento de venda NFC: o custo das plaquinhas volta para a caixinha Estoque NFC e só o lucro é dividido.")
            SplitEditor(s, value, mode, { mode = it }, custom)
            AppTextField(notes, { notes = it }, "Observações", singleLine = false)
            Label("Para trabalhos ainda não pagos, use \"Registrar trabalho\": assim o valor fica como pendente e não conta como recebido.")
            PrimaryButton("Salvar entrada", enabled = value != null && value > 0 && account != null, onClick = {
                val split = buildSplit(mode, custom)
                val d = desc.ifBlank { l.source(src)?.name ?: "Entrada" }
                s.vm.run(if (editing) "Entrada atualizada" else "Entrada salva", onDone = s::back) {
                    if (editing) replaceIncome(old!!.id, value!!, date, d, account!!, src, split, notes)
                    else addIncome(value!!, date, d, account!!, src, split, notes = notes)
                }
            })
            if (editing) OutlinedButton(onClick = { askDelete = true }) { Text("Excluir entrada", color = MaterialTheme.colorScheme.error) }
            if (linked) Label("Esta entrada está ligada a um trabalho ou venda. Excluir volta o valor para \"a receber\".")
        }
    }
    if (askDelete) ConfirmDialog("Excluir entrada?", "A divisão nas caixinhas também será desfeita.", "Excluir", danger = true,
        onConfirm = { s.vm.run("Entrada excluída", onDone = s::back) { deleteTxn(old!!.id) } }, onDismiss = { askDelete = false })
}

/* ======================= Transferência ======================= */

@Composable
fun TransferForm(s: AppState) {
    val l = s.ledger
    val fin = LocalFin.current
    val accs = accountOptions(l)
    var amount by remember { mutableStateOf("") }
    var from by remember { mutableStateOf(accs.firstOrNull()?.first) }
    var to by remember { mutableStateOf(accs.getOrNull(1)?.first) }
    var date by remember { mutableStateOf(s.calc.today) }
    val value = Money.parse(amount)
    val warn = value?.let { s.calc.lowBalanceWarning(from, -it) }
    ScreenScaffold("Transferir entre contas", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            Label("Transferência entre suas próprias contas não é receita nem despesa.")
            MoneyField(amount, { amount = it }, "Valor", autoFocus = true)
            Dropdown("De", accs, from, { from = it })
            Dropdown("Para", accs, to, { to = it })
            DateField(date, { date = it })
            warn?.let { Banner(it, fin.warn) }
            PrimaryButton("Transferir", enabled = value != null && value > 0 && from != null && to != null && from != to, onClick = {
                s.vm.run(warn ?: "Transferência registrada", onDone = s::back) { addTransfer(from!!, to!!, value!!, date, "Transferência ${l.account(from)?.name} → ${l.account(to)?.name}") }
            })
        }
    }
}

/** Helper used by lists: a transfer / invoice payment / adjustment can be removed directly. */
@Composable
fun SimpleTxnDialog(s: AppState, t: Txn, onDismiss: () -> Unit) {
    ConfirmDialog("Excluir lançamento?", "${t.description} · ${Money.format(t.amountCents)} em ${Dates.format(t.date)}", "Excluir", danger = true,
        onConfirm = { s.vm.run("Lançamento excluído") { deleteTxn(t.id) } }, onDismiss = onDismiss)
}
