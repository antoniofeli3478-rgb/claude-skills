package br.com.daniel.meufinanceiro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.daniel.meufinanceiro.data.Bill
import br.com.daniel.meufinanceiro.data.Box as MoneyBox
import br.com.daniel.meufinanceiro.data.BoxKind
import br.com.daniel.meufinanceiro.data.BoxMovement
import br.com.daniel.meufinanceiro.data.MoveKind
import br.com.daniel.meufinanceiro.data.PayMethod
import br.com.daniel.meufinanceiro.domain.BillState
import br.com.daniel.meufinanceiro.domain.CardBilling
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.ui.AppState
import br.com.daniel.meufinanceiro.ui.R
import br.com.daniel.meufinanceiro.ui.components.AppTextField
import br.com.daniel.meufinanceiro.ui.components.Banner
import br.com.daniel.meufinanceiro.ui.components.ChoiceChips
import br.com.daniel.meufinanceiro.ui.components.ConfirmDialog
import br.com.daniel.meufinanceiro.ui.components.DateField
import br.com.daniel.meufinanceiro.ui.components.Divider
import br.com.daniel.meufinanceiro.ui.components.Dot
import br.com.daniel.meufinanceiro.ui.components.Dropdown
import br.com.daniel.meufinanceiro.ui.components.EmptyState
import br.com.daniel.meufinanceiro.ui.components.FinCard
import br.com.daniel.meufinanceiro.ui.components.Label
import br.com.daniel.meufinanceiro.ui.components.LineChart
import br.com.daniel.meufinanceiro.ui.components.ListRow
import br.com.daniel.meufinanceiro.ui.components.MoneyDialog
import br.com.daniel.meufinanceiro.ui.components.MoneyField
import br.com.daniel.meufinanceiro.ui.components.MoneyText
import br.com.daniel.meufinanceiro.ui.components.PrimaryButton
import br.com.daniel.meufinanceiro.ui.components.ProgressBar
import br.com.daniel.meufinanceiro.ui.components.ProgressRing
import br.com.daniel.meufinanceiro.ui.components.ScreenScaffold
import br.com.daniel.meufinanceiro.ui.components.ScrollColumn
import br.com.daniel.meufinanceiro.ui.components.SectionTitle
import br.com.daniel.meufinanceiro.ui.components.StatTile
import br.com.daniel.meufinanceiro.ui.components.SwitchRow
import br.com.daniel.meufinanceiro.ui.theme.LocalFin
import br.com.daniel.meufinanceiro.ui.theme.MoneyLarge
import kotlin.math.ceil

/* ======================= Cartão ======================= */

@Composable
fun CardScreen(s: AppState) {
    val l = s.ledger
    val c = s.calc
    val fin = LocalFin.current
    val card = l.mainCard
    if (card == null) {
        ScreenScaffold("Cartão") { pad -> ScrollColumn(pad) { EmptyState("Nenhum cartão", "Cadastre seu cartão em Configurações.", "Configurar") { s.go(R.settings("card")) } } }
        return
    }
    val currentKey = c.currentInvoiceKey(card)
    var offset by remember { mutableIntStateOf(0) }
    val key = Dates.ymPlus(currentKey, offset.toLong())
    val inv = remember(l, key) { c.invoice(card, key) }
    var paying by remember { mutableStateOf(false) }
    val warning = CardBilling.configWarning(card)

    ScreenScaffold("Cartão", actions = { TextButton(onClick = { s.go(R.settings("card")) }) { Text("Ajustes") } }) { pad ->
        ScrollColumn(pad) {
            // Card visual
            Box(Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large).background(Brush.linearGradient(listOf(Color(0xFFFF7A00), Color(0xFFB34700)))).padding(20.dp)) {
                Column {
                    Text(card.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text("Banco ${card.bank}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    Spacer(Modifier.height(18.dp))
                    Text("Total em aberto (todas as faturas)", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.85f))
                    Text(Money.format(c.cardDebt(card.id)), style = MoneyLarge, color = Color.White)
                    Spacer(Modifier.height(10.dp))
                    Text("Melhor dia de compra: ${card.bestPurchaseDay} · Fechamento: ${card.closingDay} · Vencimento: ${card.dueDay?.toString() ?: "a configurar"}",
                        style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                    card.limitCents?.let { lim -> Text("Limite disponível: ${Money.format(lim - c.cardDebt(card.id))}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f)) }
                }
            }
            if (card.dueDay == null) Banner("Defina o dia de vencimento da fatura em Ajustes (não foi inventado).", fin.warn) { s.go(R.settings("card")) }
            warning?.let { Banner(it, fin.info) }

            // Invoice selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { offset-- }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Fatura anterior") }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(when (offset) { 0 -> "Fatura atual"; -1 -> "Fatura anterior"; 1 -> "Próxima fatura"; else -> "Fatura" }, style = MaterialTheme.typography.titleMedium)
                    Label(Dates.monthLabel(key) + (if (inv.isPaid) " · paga" else ""))
                }
                IconButton(onClick = { offset++ }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Próxima fatura") }
            }
            ChoiceChips(null, listOf(-1 to "Anterior", 0 to "Atual", 1 to "Próxima"), offset, { offset = it })

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("Total da fatura", inv.totalCents, Modifier.weight(1f))
                StatTile("Já pago", inv.paymentsCents, Modifier.weight(1f), fin.income)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("Falta pagar", inv.remainingCents, Modifier.weight(1f), if (inv.remainingCents > 0) fin.expense else Color.Unspecified)
                FinCard(Modifier.weight(1f), padding = PaddingValues(14.dp)) {
                    Label("Fecha / vence")
                    Text(Dates.formatShort(inv.closingDate) + " / " + (inv.dueDate?.let { Dates.formatShort(it) } ?: "—"), style = MaterialTheme.typography.titleMedium)
                }
            }
            if (inv.remainingCents > 0) PrimaryButton("Pagar fatura", onClick = { paying = true })
            Label("Pagar a fatura tira o dinheiro da conta, mas não conta as compras de novo: elas já entraram nos gastos quando foram feitas.")

            SectionTitle("Compras desta fatura (${inv.items.size})")
            if (inv.items.isEmpty()) Label("Nenhuma compra nesta fatura.")
            else FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                inv.items.forEachIndexed { i, it ->
                    if (i > 0) Divider()
                    val p = it.purchase
                    ListRow(p.description + if (p.installments > 1) " (${it.installment.number}/${p.installments})" else "",
                        Dates.relative(p.date) + (l.category(p.categoryId)?.let { " · ${it.name}" } ?: ""),
                        trailing = { MoneyText(it.installment.amountCents, style = MaterialTheme.typography.titleSmall) },
                        onClick = { s.go(R.expense(purchase = p.id)) })
                }
            }
            val future = c.futureCommitments(card)
            val parcelados = l.purchases.filter { it.cardId == card.id && it.installments > 1 }
            if (parcelados.isNotEmpty()) {
                SectionTitle("Parcelamentos")
                FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    parcelados.forEachIndexed { i, p ->
                        val rest = l.installments.filter { it.purchaseId == p.id && it.invoiceKey > currentKey }
                        if (i > 0) Divider()
                        ListRow(p.description, "${p.installments}x de ${Money.format(p.totalCents / p.installments)} · faltam ${rest.size} parcela(s) depois desta fatura",
                            trailing = { MoneyText(rest.sumOf { it.amountCents }, style = MaterialTheme.typography.titleSmall) })
                    }
                }
                Label("Compras futuras (depois da próxima fatura): ${Money.format(future)}")
            }
            val monthSpend = c.report(br.com.daniel.meufinanceiro.domain.Period.of(br.com.daniel.meufinanceiro.domain.PeriodKind.MONTH)).expenseEvents.filter { it.viaCard }.sumOf { it.amountCents }
            Label("Total gasto no crédito este mês (por competência): ${Money.format(monthSpend)}")
            PrimaryButton("Nova compra no cartão", onClick = { s.go(R.expense()) })
        }
    }

    if (paying) {
        var account by remember { mutableStateOf(card.paymentAccountId ?: l.dailyAccount?.id) }
        var date by remember { mutableStateOf(c.today) }
        MoneyDialog("Pagar fatura ${Dates.monthLabel(key)}", "Valor pago", inv.remainingCents, "Sai da conta escolhida. Não cria despesa nova.", "Pagar",
            onConfirm = { v ->
                val warn = c.lowBalanceWarning(account, -v)
                s.vm.run(warn ?: "Pagamento da fatura registrado") { payInvoice(card.id, key, v, account!!, date) }
            }, onDismiss = { paying = false }) {
            Dropdown("Pagar com a conta", accountOptions(l), account, { account = it })
            DateField(date, { date = it })
        }
    }
}

/* ======================= Caixinhas ======================= */

@Composable
fun BoxesScreen(s: AppState) {
    val l = s.ledger
    val c = s.calc
    val fin = LocalFin.current
    val un = c.unallocated()
    var distribute by remember { mutableStateOf(false) }
    var transfer by remember { mutableStateOf(false) }
    var newBox by remember { mutableStateOf(false) }

    ScreenScaffold("Minhas Caixinhas", onBack = s::back, actions = { TextButton(onClick = { newBox = true }) { Text("Nova") } }) { pad ->
        ScrollColumn(pad) {
            FinCard {
                Label("Total nas caixinhas")
                Text(Money.format(c.totalInBoxes()), style = MoneyLarge)
                Spacer(Modifier.height(6.dp))
                Row {
                    Column(Modifier.weight(1f)) { Label("Guardado (metas e reserva)"); MoneyText(c.totalSaved()) }
                    Column(Modifier.weight(1f)) { Label("Sem caixinha"); MoneyText(un, color = if (un < 0) fin.expense else Color.Unspecified) }
                }
            }
            if (un > 0) Banner("${Money.format(un)} ainda não tem caixinha. Toque para distribuir.", fin.info) { distribute = true }
            if (un < 0) Banner("As caixinhas somam ${Money.format(-un)} a mais do que você tem nas contas (descontada a fatura). Confira os saldos das contas.", fin.warn)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { distribute = true }, modifier = Modifier.weight(1f)) { Text("Distribuir") }
                OutlinedButton(onClick = { transfer = true }, modifier = Modifier.weight(1f)) { Text("Transferir") }
            }
            c.activeBoxes.forEach { b ->
                val g = c.goal(b)
                FinCard(onClick = { s.go(R.box(b.id)) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(52.dp), contentAlignment = Alignment.Center) {
                            ProgressRing(g.progress, Color(b.color), Modifier.size(52.dp))
                            Text(if (g.goalCents != null) "${(g.progress * 100).toInt()}%" else "—", style = MaterialTheme.typography.labelMedium)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(b.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                when {
                                    g.goalCents != null && g.missingCents == 0L -> "Meta atingida!"
                                    g.goalCents != null -> "Faltam ${Money.format(g.missingCents ?: 0)}" + (g.monthsToGoal?.let { " · ~${ceil(it).toInt()} mês(es)" } ?: "")
                                    b.kind == BoxKind.SPENDING -> "Livre para gastar: ${Money.format(c.freeToSpend())}"
                                    b.kind == BoxKind.NFC_STOCK -> "Para repor estoque · ${c.nfcStock()} un. em estoque"
                                    else -> g.goalNote ?: "Sem meta definida"
                                },
                                style = MaterialTheme.typography.bodySmall, color = fin.muted,
                            )
                        }
                        MoneyText(g.balanceCents, color = if (g.balanceCents < 0) fin.expense else Color.Unspecified)
                    }
                }
            }
            Label("A divisão automática (${l.rules.sortedBy { l.box(it.boxId)?.sortOrder ?: 0 }.joinToString(" / ") { "${l.box(it.boxId)?.name} ${Money.formatPercent(it.basisPoints)}" }}) pode ser alterada em Configurações → Divisão.")
        }
    }

    if (distribute) {
        var box by remember { mutableStateOf(c.activeBoxes.firstOrNull()?.id) }
        MoneyDialog("Distribuir dinheiro sem caixinha", "Valor", un.coerceAtLeast(0), "Disponível sem caixinha: ${Money.format(un)}", "Guardar",
            onConfirm = { v -> s.vm.run("Valor guardado na caixinha") { boxDeposit(box!!, v, c.today, "Distribuição manual") } }, onDismiss = { distribute = false }) {
            Dropdown("Caixinha", boxOptions(l), box, { box = it })
        }
    }
    if (transfer) BoxTransferDialog(s, null) { transfer = false }
    if (newBox) BoxEditDialog(s, MoneyBox(name = "", color = 0xFF94A3B8, sortOrder = l.boxes.size)) { newBox = false }
}

@Composable
fun BoxTransferDialog(s: AppState, fromId: Long?, onDismiss: () -> Unit) {
    val l = s.ledger
    var from by remember { mutableStateOf(fromId ?: s.calc.activeBoxes.firstOrNull()?.id) }
    var to by remember { mutableStateOf(s.calc.activeBoxes.firstOrNull { it.id != from }?.id) }
    MoneyDialog("Transferir entre caixinhas", "Valor", null, "Não é receita nem despesa: só muda o destino do dinheiro.", "Transferir",
        onConfirm = { v -> if (from != to) s.vm.run("Transferência entre caixinhas feita") { boxTransfer(from!!, to!!, v, s.calc.today) } }, onDismiss = onDismiss) {
        Dropdown("De", boxOptions(l), from, { from = it })
        Dropdown("Para", boxOptions(l), to, { to = it })
    }
}

@Composable
fun BoxEditDialog(s: AppState, b: MoneyBox, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(b.name) }
    var goal by remember { mutableStateOf(b.goalCents?.let { Money.toInput(it) } ?: "") }
    var goalMax by remember { mutableStateOf(b.goalMaxCents?.let { Money.toInput(it) } ?: "") }
    var months by remember { mutableStateOf(b.emergencyMonths?.toString() ?: "") }
    var note by remember { mutableStateOf(b.note) }
    var archive by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (b.id == 0L) "Nova caixinha" else "Editar caixinha") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(name, { name = it }, "Nome")
                MoneyField(goal, { goal = it }, if (b.kind == BoxKind.EMERGENCY) "Meta fixa (opcional)" else "Meta", supporting = "Deixe vazio para não ter meta")
                if (b.kind == BoxKind.COMPUTER || b.kind == BoxKind.OTHER || b.id == 0L) MoneyField(goalMax, { goalMax = it }, "Meta máxima (opcional, para faixa)")
                if (b.kind == BoxKind.EMERGENCY) AppTextField(months, { months = it.filter(Char::isDigit).take(2) }, "Meta em meses de gastos (ex.: 6)", keyboardType = KeyboardType.Number,
                    supporting = "Usado quando não há meta fixa e já existem 2 meses de dados")
                AppTextField(note, { note = it }, "Anotação", singleLine = false)
                if (b.id != 0L && b.kind != BoxKind.SPENDING && b.kind != BoxKind.NFC_STOCK) SwitchRow("Arquivar caixinha", archive, { archive = it }, "Some da lista; o histórico continua salvo")
            }
        },
        confirmButton = {
            TextButton(enabled = name.isNotBlank(), onClick = {
                s.vm.run("Caixinha salva") {
                    saveBox(b.copy(name = name.trim(), goalCents = Money.parse(goal)?.takeIf { it > 0 }, goalMaxCents = Money.parse(goalMax)?.takeIf { it > 0 },
                        emergencyMonths = months.toIntOrNull()?.takeIf { it > 0 } ?: if (b.kind == BoxKind.EMERGENCY) null else b.emergencyMonths, note = note, archived = archive))
                }
                onDismiss()
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
fun BoxDetailScreen(s: AppState, boxId: Long) {
    val l = s.ledger
    val c = s.calc
    val fin = LocalFin.current
    val b = l.box(boxId) ?: return
    val g = c.goal(b)
    var deposit by remember { mutableStateOf<Int?>(null) } // 1 = guardar, -1 = retirar
    var transfer by remember { mutableStateOf(false) }
    var edit by remember { mutableStateOf(false) }
    var removing by remember { mutableStateOf<BoxMovement?>(null) }
    val moves = l.moves.filter { it.boxId == boxId }

    ScreenScaffold(b.name, onBack = s::back, actions = { TextButton(onClick = { edit = true }) { Text("Editar") } }) { pad ->
        ScrollColumn(pad) {
            FinCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Label("Saldo atual")
                        Text(Money.format(g.balanceCents), style = MoneyLarge)
                    }
                    Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                        ProgressRing(g.progress, Color(b.color), Modifier.size(72.dp), 7f)
                        Text(if (g.goalCents != null) "${(g.progress * 100).toInt()}%" else "—", style = MaterialTheme.typography.titleSmall)
                    }
                }
                if (g.goalCents != null) {
                    Spacer(Modifier.height(10.dp))
                    ProgressBar(g.progress, Color(b.color))
                    Spacer(Modifier.height(8.dp))
                    Text("Meta ${Money.format(g.goalCents)}" + (g.goalMaxCents?.let { " a ${Money.format(it)}" } ?: "") + " · faltam ${Money.format(g.missingCents ?: 0)}", style = MaterialTheme.typography.bodyMedium)
                    g.goalMaxCents?.let { mx -> if (g.balanceCents < mx) Label("Para o topo da faixa faltam ${Money.format(mx - g.balanceCents)}") }
                    Label(g.monthsToGoal?.let { m -> if (m == 0.0) "Meta atingida!" else "No ritmo atual (${Money.format(g.monthlyPaceCents)}/mês): ~${ceil(m).toInt()} mês(es)" } ?: "Sem ritmo recente para prever.")
                }
                g.goalNote?.let { Spacer(Modifier.height(6.dp)); Label(it) }
                if (b.note.isNotBlank()) { Spacer(Modifier.height(6.dp)); Text(b.note, style = MaterialTheme.typography.bodySmall, color = fin.muted) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("Total depositado", g.depositedCents, Modifier.weight(1f), fin.income)
                StatTile("Total retirado", g.withdrawnCents, Modifier.weight(1f), fin.expense)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryButton("Guardar", onClick = { deposit = 1 }, modifier = Modifier.weight(1f))
                OutlinedButton(onClick = { deposit = -1 }, modifier = Modifier.weight(1f).height(54.dp)) { Text("Retirar") }
            }
            OutlinedButton(onClick = { transfer = true }, modifier = Modifier.fillMaxWidth()) { Text("Transferir para outra caixinha") }
            FinCard {
                Text("Evolução (6 meses)", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LineChart(c.boxBalanceByMonth(b.id).map { Dates.monthShortLabel(it.first) to it.second }, Color(b.color), description = "Saldo da caixinha")
            }
            SectionTitle("Histórico")
            if (moves.isEmpty()) Label("Sem movimentações ainda.")
            else FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                moves.take(60).forEachIndexed { i, m ->
                    if (i > 0) Divider()
                    val t = l.txns.firstOrNull { it.id == m.txnId }
                    val p = l.purchases.firstOrNull { it.id == m.cardPurchaseId }
                    val title = when (m.kind) {
                        MoveKind.DISTRIBUTION -> "Divisão de: " + (t?.description ?: "entrada")
                        MoveKind.SPEND -> (t?.description ?: p?.description ?: "Despesa") + if (p != null) " (crédito)" else ""
                        MoveKind.DEPOSIT -> "Guardado" + m.note.takeIf { it.isNotBlank() }?.let { " · $it" }.orEmpty()
                        MoveKind.WITHDRAW -> "Retirado" + m.note.takeIf { it.isNotBlank() }?.let { " · $it" }.orEmpty()
                        else -> if (m.amountCents > 0) "Transferência recebida" else "Transferência enviada"
                    }
                    ListRow(title, Dates.relative(m.date), leading = { Dot(if (m.amountCents >= 0) fin.income else fin.expense) },
                        trailing = { Text(Money.format(m.amountCents, showPlus = true), style = MaterialTheme.typography.titleSmall, color = if (m.amountCents >= 0) fin.income else fin.expense) },
                        onClick = if (m.txnId == null && m.cardPurchaseId == null) ({ removing = m }) else null)
                }
            }
        }
    }
    deposit?.let { sign ->
        MoneyDialog(if (sign > 0) "Guardar em ${b.name}" else "Retirar de ${b.name}", "Valor", null,
            if (sign > 0) "Vem do dinheiro sem caixinha (${Money.format(c.unallocated())})." else "Volta para o dinheiro sem caixinha.", if (sign > 0) "Guardar" else "Retirar",
            onConfirm = { v -> s.vm.run(if (sign > 0) "Guardado" else "Retirado") { boxDeposit(b.id, v * sign, c.today) } }, onDismiss = { deposit = null })
    }
    if (transfer) BoxTransferDialog(s, b.id) { transfer = false }
    if (edit) BoxEditDialog(s, b) { edit = false }
    removing?.let { m -> ConfirmDialog("Desfazer movimentação?", "${Money.format(m.amountCents)} em ${Dates.format(m.date)}", "Desfazer", danger = true,
        onConfirm = { s.vm.run("Movimentação desfeita") { deleteBoxMove(m) } }, onDismiss = { removing = null }) }
}

/* ======================= Contas a pagar ======================= */

@Composable
fun BillsScreen(s: AppState) {
    val l = s.ledger
    val c = s.calc
    val fin = LocalFin.current
    val month = Dates.ym(c.today)
    val st = c.billStatuses(month)
    var editing by remember { mutableStateOf<Bill?>(null) }
    val nextMonth = c.billStatuses(Dates.ymPlus(month, 1))

    ScreenScaffold("Contas a pagar", onBack = s::back, actions = { TextButton(onClick = { editing = Bill(name = "", amountCents = 0, active = true, sortOrder = l.bills.size) }) { Text("Nova") } }) { pad ->
        ScrollColumn(pad) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("Compromissos do mês", c.billsMonthTotal(month), Modifier.weight(1f), sub = Dates.monthLabel(month))
                StatTile("Falta pagar", c.billsUnpaid(month), Modifier.weight(1f), if (c.billsUnpaid(month) > 0) fin.expense else Color.Unspecified)
            }
            val groups = listOf(
                Triple("Atrasadas", st.filter { it.state == BillState.OVERDUE }, fin.expense),
                Triple("Vencem em breve", st.filter { it.state == BillState.DUE_SOON }, fin.warn),
                Triple("Próximas", st.filter { it.state == BillState.UPCOMING || it.state == BillState.NO_DATE }, fin.info),
                Triple("Pagas", st.filter { it.state == BillState.PAID }, fin.income),
            )
            groups.forEach { (title, list, color) ->
                if (list.isNotEmpty()) {
                    SectionTitle(title)
                    FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                        list.forEachIndexed { i, b ->
                            if (i > 0) Divider()
                            ListRow(b.bill.name,
                                (b.dueDate?.let { "vence ${Dates.relative(it)}" } ?: "defina o vencimento") + if (b.state == BillState.PAID) " · paga ${Money.format(b.paidCents)}" else "",
                                leading = { Dot(color) },
                                trailing = {
                                    if (b.state == BillState.PAID) MoneyText(b.paidCents, style = MaterialTheme.typography.titleSmall)
                                    else TextButton(onClick = { s.go(R.expense(bill = b.bill.id)) }) { Text("Pagar " + if (b.expectedCents > 0) Money.short(b.expectedCents) else "") }
                                },
                                onClick = { editing = b.bill })
                        }
                    }
                }
            }
            if (nextMonth.isNotEmpty()) {
                SectionTitle("Próximos pagamentos (${Dates.monthLabel(Dates.ymPlus(month, 1))})")
                Label(nextMonth.joinToString(" · ") { "${it.bill.name} ${it.dueDate?.let { d -> Dates.formatShort(d) } ?: ""}" })
            }
            val inactive = l.bills.filter { !it.active }
            if (inactive.isNotEmpty()) {
                SectionTitle("Desativadas / a configurar")
                FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    inactive.forEachIndexed { i, b ->
                        if (i > 0) Divider()
                        ListRow(b.name, (if (b.amountCents > 0) Money.format(b.amountCents) else "valor a configurar") + (b.dueDay?.let { " · dia $it" } ?: "") + " · toque para ativar",
                            leading = { Dot(fin.faint) }, onClick = { editing = b })
                    }
                }
                Label("Nenhuma conta é considerada ativa ou paga até você ativar e definir o vencimento.")
            }
        }
    }
    editing?.let { b -> BillEditDialog(s, b) { editing = null } }
}

@Composable
fun BillEditDialog(s: AppState, b: Bill, onDismiss: () -> Unit) {
    val l = s.ledger
    var name by remember { mutableStateOf(b.name) }
    var amount by remember { mutableStateOf(if (b.amountCents > 0) Money.toInput(b.amountCents) else "") }
    var due by remember { mutableStateOf(b.dueDay?.toString() ?: "") }
    var active by remember { mutableStateOf(b.active) }
    var variable by remember { mutableStateOf(b.variable) }
    var cat by remember { mutableStateOf(b.categoryId) }
    var method by remember { mutableStateOf(b.method) }
    var account by remember { mutableStateOf(b.accountId ?: l.dailyAccount?.id) }
    var askDelete by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (b.id == 0L) "Nova conta" else b.name) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SwitchRow("Ativa", active, { active = it }, "Só contas ativas aparecem no mês")
                AppTextField(name, { name = it }, "Nome")
                MoneyField(amount, { amount = it }, "Valor de referência")
                AppTextField(due, { due = it.filter(Char::isDigit).take(2) }, "Dia do vencimento (1 a 31)", keyboardType = KeyboardType.Number)
                SwitchRow("Valor variável", variable, { variable = it })
                Dropdown("Categoria", categoryOptions(l), cat, { cat = it })
                ChoiceChips("Forma de pagamento", methodOptions, method, { method = it })
                if (method != PayMethod.CREDIT) Dropdown("Conta", accountOptions(l), account, { account = it })
            }
        },
        confirmButton = {
            TextButton(enabled = name.isNotBlank(), onClick = {
                s.vm.run("Conta salva") {
                    saveBill(b.copy(name = name.trim(), amountCents = Money.parse(amount) ?: 0, dueDay = due.toIntOrNull()?.coerceIn(1, 31), active = active,
                        variable = variable, categoryId = cat, method = method, accountId = if (method == PayMethod.CREDIT) null else account, cardId = if (method == PayMethod.CREDIT) l.mainCard?.id else null))
                }
                onDismiss()
            }) { Text("Salvar") }
        },
        dismissButton = {
            Row {
                if (b.id != 0L) TextButton(onClick = { askDelete = true }) { Text("Excluir", color = MaterialTheme.colorScheme.error) }
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        },
    )
    if (askDelete) ConfirmDialog("Excluir conta?", "Os pagamentos já feitos continuam nos seus gastos.", "Excluir", danger = true,
        onConfirm = { s.vm.run("Conta excluída") { deleteBill(b) }; onDismiss() }, onDismiss = { askDelete = false })
}
