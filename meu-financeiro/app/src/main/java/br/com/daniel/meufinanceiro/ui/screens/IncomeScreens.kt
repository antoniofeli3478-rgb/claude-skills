package br.com.daniel.meufinanceiro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.daniel.meufinanceiro.data.DistMode
import br.com.daniel.meufinanceiro.data.FinanceRepository
import br.com.daniel.meufinanceiro.data.PayMethod
import br.com.daniel.meufinanceiro.data.SourceType
import br.com.daniel.meufinanceiro.data.TxnType
import br.com.daniel.meufinanceiro.data.WorkEntry
import br.com.daniel.meufinanceiro.domain.BillState
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.domain.Period
import br.com.daniel.meufinanceiro.domain.PeriodKind
import br.com.daniel.meufinanceiro.domain.Receivable
import br.com.daniel.meufinanceiro.ui.AppState
import br.com.daniel.meufinanceiro.ui.R
import br.com.daniel.meufinanceiro.ui.components.AppIcons
import br.com.daniel.meufinanceiro.ui.components.AppTextField
import br.com.daniel.meufinanceiro.ui.components.ChoiceChips
import br.com.daniel.meufinanceiro.ui.components.ConfirmDialog
import br.com.daniel.meufinanceiro.ui.components.DateField
import br.com.daniel.meufinanceiro.ui.components.Divider
import br.com.daniel.meufinanceiro.ui.components.Dot
import br.com.daniel.meufinanceiro.ui.components.Dropdown
import br.com.daniel.meufinanceiro.ui.components.EmptyState
import br.com.daniel.meufinanceiro.ui.components.FinCard
import br.com.daniel.meufinanceiro.ui.components.IconBadge
import br.com.daniel.meufinanceiro.ui.components.Label
import br.com.daniel.meufinanceiro.ui.components.ListRow
import br.com.daniel.meufinanceiro.ui.components.MoneyField
import br.com.daniel.meufinanceiro.ui.components.MoneyText
import br.com.daniel.meufinanceiro.ui.components.PrimaryButton
import br.com.daniel.meufinanceiro.ui.components.ScreenScaffold
import br.com.daniel.meufinanceiro.ui.components.ScrollColumn
import br.com.daniel.meufinanceiro.ui.components.SectionTitle
import br.com.daniel.meufinanceiro.ui.components.StatTile
import br.com.daniel.meufinanceiro.ui.components.HBarList
import br.com.daniel.meufinanceiro.ui.theme.LocalFin
import kotlin.math.roundToLong

/* ======================= Rendas ======================= */

@Composable
fun IncomeScreen(s: AppState) {
    val l = s.ledger
    val c = s.calc
    val fin = LocalFin.current
    var period by remember { mutableStateOf(Period.of(PeriodKind.MONTH)) }
    val rep = remember(l, period) { c.report(period) }
    val profit = rep.profit.filter { it.revenueCents != 0L || it.costsCents != 0L }
    val receivables = remember(l) { c.receivables() }

    ScreenScaffold("Trabalhos e lucro", actions = { TextButton(onClick = { s.go(R.settings("sources")) }) { Text("Fontes") } }) { pad ->
        ScrollColumn(pad) {
            PeriodSelector(period) { period = it }
            FinCard {
                Label("Lucro líquido · ${period.label.lowercase()}")
                Text(Money.format(rep.netProfitCents), style = MaterialTheme.typography.headlineMedium, color = if (rep.netProfitCents >= 0) fin.income else fin.expense)
                Spacer(Modifier.height(10.dp))
                Row {
                    Column(Modifier.weight(1f)) { Label("Faturamento"); Text(Money.format(rep.grossRevenueCents), style = MaterialTheme.typography.titleSmall) }
                    Column(Modifier.weight(1f)) { Label("Custos"); Text(Money.format(rep.profitCostsCents), style = MaterialTheme.typography.titleSmall) }
                    Column(Modifier.weight(1f)) { Label("Margem"); Text(if (rep.grossRevenueCents > 0) "${rep.netProfitCents * 100 / rep.grossRevenueCents}%" else "—", style = MaterialTheme.typography.titleSmall) }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryButton("Registrar trabalho", onClick = { s.go(R.work()) }, modifier = Modifier.weight(1f))
                OutlinedButton(onClick = { s.go(R.RECEIVE) }, modifier = Modifier.weight(1f).height(54.dp)) {
                    Text(if (receivables.isNotEmpty()) "A receber ${Money.short(receivables.sumOf { it.pendingCents })}" else "Receber")
                }
            }
            if (profit.isEmpty()) EmptyState("Sem trabalhos neste período", "Registre cada turno, festa ou evento com as horas e os custos (Uber, ônibus, lanche). O app calcula o lucro de verdade.")
            profit.sortedByDescending { it.netCents }.forEach { sp ->
                val src = l.source(sp.sourceId)!!
                FinCard(onClick = { if (src.type == SourceType.NFC) s.go(R.NFC) else s.go(R.source(src.id)) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Dot(Color(src.color))
                        Spacer(Modifier.width(10.dp))
                        Text(src.name, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                        Text(Money.format(sp.netCents), style = MaterialTheme.typography.titleMedium, color = if (sp.netCents >= 0) fin.income else fin.expense)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(buildList {
                        add("faturou ${Money.format(sp.revenueCents)}")
                        add("custos ${Money.format(sp.costsCents)}")
                        sp.marginPct?.let { add("margem $it%") }
                        if (sp.hours > 0) add("${fmtHours(sp.hours)} · ${sp.netPerHourCents?.let { Money.format(it) }}/h")
                        if (sp.pendingCents > 0) add("a receber ${Money.format(sp.pendingCents)}")
                    }.joinToString(" · "), style = MaterialTheme.typography.bodySmall, color = fin.muted)
                }
            }
            val perHour = profit.filter { it.netPerHourCents != null && it.hours > 0 }
            if (perHour.size >= 2) {
                SectionTitle("Lucro por hora")
                FinCard { HBarList(perHour.sortedByDescending { it.netPerHourCents }.map { it.name to it.netPerHourCents!! }, fin.chartIncome) }
                Label("Já descontados os custos ligados a cada trabalho. Mostra o que realmente compensa o seu tempo.")
            }
            if (rep.otherInCents > 0 || l.loans.isNotEmpty()) {
                SectionTitle("Entradas que não são renda")
                FinCard(onClick = { s.go(R.LOANS) }) {
                    Text("Família, empréstimos, ajuda e reembolsos: ${Money.format(rep.otherInCents)} no período", style = MaterialTheme.typography.bodyMedium)
                    if (c.loansOutstanding() > 0) Text("Empréstimos a devolver: ${Money.format(c.loansOutstanding())}", style = MaterialTheme.typography.bodySmall, color = fin.warn)
                    Label("Não entram no lucro nem na divisão 50/25/25.")
                }
            }
            TextButton(onClick = { s.go(R.income()) }) { Text("Registrar outra entrada de dinheiro") }
        }
    }
}

fun fmtHours(h: Double): String = if (h == h.toLong().toDouble()) "${h.toLong()}h" else String.format(java.util.Locale.ROOT, "%.1fh", h).replace('.', ',')

/* ======================= Fonte (Tio Rex, recreação, pipoca...) ======================= */

@Composable
fun SourceScreen(s: AppState, sourceId: Long) {
    val l = s.ledger
    val c = s.calc
    val fin = LocalFin.current
    val src = l.source(sourceId) ?: return
    val week = c.report(Period.of(PeriodKind.WEEK))
    val month = c.report(Period.of(PeriodKind.MONTH))
    fun inc(r: br.com.daniel.meufinanceiro.domain.Report) = r.incomeBySource.firstOrNull { it.first == sourceId }?.second ?: 0
    val works = l.works.filter { it.sourceId == sourceId }
    val direct = l.txns.filter { it.type == TxnType.INCOME && it.workId == null && it.sourceId == sourceId }

    ScreenScaffold(src.name, onBack = s::back) { pad ->
        ScrollColumn(pad) {
            val mp = month.profit.firstOrNull { it.sourceId == sourceId }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("Lucro líquido no mês", mp?.netCents ?: 0, Modifier.weight(1f), fin.income,
                    sub = mp?.let { "faturou ${Money.format(it.revenueCents)} · custos ${Money.format(it.costsCents)}" })
                StatTile("Lucro por hora", mp?.netPerHourCents ?: 0, Modifier.weight(1f), sub = mp?.let { "${fmtHours(it.hours)} no mês" })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("Recebido na semana", inc(week), Modifier.weight(1f))
                StatTile("Recebido no mês", inc(month), Modifier.weight(1f))
            }
            val pend = works.sumOf { (it.myShareCents - c.workReceived(it.id)).coerceAtLeast(0) }
            val monthWorks = works.filter { it.date in Period.of(PeriodKind.MONTH) }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("Pendente", pend, Modifier.weight(1f), if (pend > 0) fin.warn else Color.Unspecified)
                StatTile("Trabalhos no mês", monthWorks.sumOf { it.myShareCents }, Modifier.weight(1f), sub = "${monthWorks.size} · ${fmtHours(monthWorks.sumOf { it.hours })}")
            }
            PrimaryButton("Registrar ${if (src.type == SourceType.TIO_REX) "turno" else "trabalho"}", onClick = { s.go(R.work(source = sourceId)) })
            SectionTitle("Histórico")
            if (works.isEmpty() && direct.isEmpty()) EmptyState("Nenhum registro ainda", "Registre cada turno ou evento: data, horas e quanto é seu.")
            FinCard(padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                works.forEachIndexed { i, w ->
                    val got = c.workReceived(w.id)
                    val p = (w.myShareCents - got).coerceAtLeast(0)
                    if (i > 0) Divider()
                    ListRow(
                        w.title.ifBlank { if (src.type == SourceType.TIO_REX) "Turno" else "Trabalho" } + " · " + Dates.relative(w.date),
                        listOfNotNull(w.hours.takeIf { it > 0 }?.let { fmtHours(it) }, c.workCosts(w.id).takeIf { it > 0 }?.let { "custos ${Money.format(it)}" },
                            "lucro ${Money.format(c.workProfit(w.id))}", if (p > 0) "falta receber ${Money.format(p)}" else "pago").joinToString(" · "),
                        leading = { Dot(if (p > 0) fin.warn else fin.income) },
                        trailing = { MoneyText(w.myShareCents, style = MaterialTheme.typography.titleSmall) },
                        onClick = { s.go(R.work(id = w.id)) },
                    )
                }
                direct.forEach { t ->
                    Divider()
                    ListRow(t.description + " · " + Dates.relative(t.date), "entrada direta", leading = { Dot(fin.income) },
                        trailing = { MoneyText(t.amountCents, style = MaterialTheme.typography.titleSmall) }, onClick = { s.go(R.income(txn = t.id)) })
                }
            }
        }
    }
}

/* ======================= Registrar trabalho ======================= */

@Composable
fun WorkForm(s: AppState, sourceId: Long, workId: Long) {
    val l = s.ledger
    val c = s.calc
    val old = remember { l.work(workId.takeIf { it != 0L }) }
    val editing = old != null
    val workSources = l.sources.filter { !it.archived && it.type != SourceType.NFC && it.isEarned && it.type !in SourceType.notEarned }
    var src by remember { mutableStateOf(old?.sourceId ?: sourceId.takeIf { it != 0L } ?: workSources.firstOrNull()?.id) }
    val source = l.source(src)
    var date by remember { mutableStateOf(old?.date ?: c.today) }
    var title by remember { mutableStateOf(old?.title ?: "") }
    var hours by remember { mutableStateOf(old?.hours?.takeIf { it > 0 }?.let { fmtHours(it).removeSuffix("h") } ?: source?.defaultHours?.takeIf { it > 0 }?.let { fmtHours(it).removeSuffix("h") } ?: "") }
    var gross by remember { mutableStateOf(old?.grossCents?.takeIf { it > 0 }?.let { Money.toInput(it) } ?: "") }
    var costs by remember { mutableStateOf(old?.costsCents?.takeIf { it > 0 }?.let { Money.toInput(it) } ?: "") }
    var share by remember { mutableStateOf((old?.myShareCents ?: source?.defaultAmountCents?.takeIf { it > 0 })?.let { Money.toInput(it) } ?: "") }
    var received by remember { mutableStateOf(if (editing) "none" else "all") }
    var partial by remember { mutableStateOf("") }
    var account by remember { mutableStateOf(l.dailyAccount?.id) }
    var mode by remember { mutableStateOf(DistMode.DEFAULT) }
    val custom = remember { mutableStateMapOf<Long, String>() }
    var notes by remember { mutableStateOf(old?.notes ?: "") }
    var askDelete by remember { mutableStateOf(false) }
    // v1.1: real costs of this job, saved as expenses linked to it (they reduce the account and the profit)
    var costGo by remember { mutableStateOf("") }
    var costBack by remember { mutableStateOf("") }
    var costFood by remember { mutableStateOf("") }
    var costOther by remember { mutableStateOf("") }
    var costMethod by remember { mutableStateOf(PayMethod.PIX) }
    val shareV = Money.parse(share)
    val isPipoca = source?.type == SourceType.PIPOCA
    val newCosts = listOf(costGo, costBack, costFood, costOther).sumOf { Money.parse(it) ?: 0 }
    val linkedCosts = old?.let { c.workCosts(it.id) } ?: 0
    val receivedNow = when (received) { "all" -> shareV ?: 0; "part" -> Money.parse(partial) ?: 0; else -> 0 }

    ScreenScaffold(if (editing) "Editar trabalho" else "Registrar trabalho", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            ChoiceChips("Fonte", workSources.map { it.id to it.name }, src, {
                src = it
                val ns = l.source(it)
                if (!editing) {
                    ns?.defaultAmountCents?.takeIf { v -> v > 0 }?.let { v -> share = Money.toInput(v) }
                    ns?.defaultHours?.takeIf { v -> v > 0 }?.let { v -> hours = fmtHours(v).removeSuffix("h") }
                }
            })
            DateField(date, { date = it })
            AppTextField(title, { title = it }, if (isPipoca) "Evento (ex.: festa junina da escola)" else "Descrição (opcional)")
            AppTextField(hours, { hours = it.filter { ch -> ch.isDigit() || ch == ',' || ch == '.' } }, "Horas trabalhadas", keyboardType = KeyboardType.Decimal)
            if (isPipoca || source?.type == SourceType.RECREACAO || source?.type == SourceType.OTHER) {
                MoneyField(gross, { gross = it }, "Faturamento total do evento (opcional)", supporting = "Só para referência: o app não presume que tudo é seu")
                MoneyField(costs, { costs = it }, "Custos que você pagou (opcional)", supporting = "Usado no cálculo do lucro. Se já lançou como despesa, deixe vazio")
            }
            MoneyField(share, { share = it }, if (isPipoca) "Quanto ficou com você (sua parte)" else "Valor do trabalho (o que é seu)")
            SectionTitle("Custos para trabalhar")
            Label(if (editing) "Custos já ligados a este trabalho: ${Money.format(linkedCosts)}. Adicione mais abaixo, se precisar." else "Preencha só o que gastou. Cada custo vira uma despesa ligada a este trabalho.")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MoneyField(costGo, { costGo = it }, "Ida (Uber, ônibus)", Modifier.weight(1f))
                MoneyField(costBack, { costBack = it }, "Volta", Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MoneyField(costFood, { costFood = it }, "Alimentação", Modifier.weight(1f))
                MoneyField(costOther, { costOther = it }, "Outros custos", Modifier.weight(1f))
            }
            if (newCosts > 0) ChoiceChips("Como pagou os custos", methodOptions.filter { it.first != PayMethod.TRANSFER }, costMethod, { costMethod = it })
            if (isPipoca || source?.type == SourceType.RECREACAO || source?.type == SourceType.OTHER) {
                MoneyField(gross, { gross = it }, "Faturamento total do evento (opcional)", supporting = "Só referência: o app não presume que tudo é seu")
                MoneyField(costs, { costs = it }, "Custos pagos fora do app (opcional)", supporting = "Ex.: material dividido. Não vira despesa (para não contar duas vezes)")
            }
            val gV = (Money.parse(costs) ?: 0) + newCosts + linkedCosts
            if (shareV != null) Label("Lucro líquido deste trabalho: ${Money.format(shareV - gV)}" + if (gV > 0) " (${Money.format(shareV)} − ${Money.format(gV)} de custos)" else "")
            if (!editing) {
                ChoiceChips("Já recebeu?", listOf("all" to "Sim, tudo", "part" to "Uma parte", "none" to "Ainda não"), received, { received = it })
                if (received == "part") MoneyField(partial, { partial = it }, "Quanto já recebeu")
                if (received != "none") {
                    Dropdown("Conta que recebeu", accountOptions(l), account, { account = it })
                    SplitEditor(s, receivedNow, mode, { mode = it }, custom)
                } else Label("Fica como pendente: não conta como dinheiro recebido até você registrar o pagamento.")
            } else {
                val got = c.workReceived(old!!.id)
                Label("Já recebido: ${Money.format(got)}. Para registrar mais pagamentos, use \"Registrar pagamento\".")
            }
            AppTextField(notes, { notes = it }, "Observações", singleLine = false)
            PrimaryButton("Salvar", enabled = src != null && shareV != null && (received != "part" || (Money.parse(partial) ?: 0) > 0), onClick = {
                val w = WorkEntry(
                    id = old?.id ?: 0, sourceId = src!!, date = date, title = title.trim(), hours = hours.replace(',', '.').toDoubleOrNull() ?: 0.0,
                    grossCents = Money.parse(gross) ?: 0, costsCents = Money.parse(costs) ?: 0, myShareCents = shareV!!, notes = notes, createdAt = old?.createdAt ?: System.currentTimeMillis(),
                )
                val transporte = l.categories.firstOrNull { it.name.startsWith("Transporte") }?.id
                val alimentacao = l.categories.firstOrNull { it.name.startsWith("Alimenta") }?.id
                val trabalho = l.categories.firstOrNull { it.name == "Trabalho" }?.id ?: l.categories.firstOrNull()?.id
                val costAccount = if (costMethod == PayMethod.CASH) l.cashAccount?.id ?: account else account
                val label = source?.name ?: "trabalho"
                val costList = listOf(
                    Triple(costGo, "Ida para $label", transporte), Triple(costBack, "Volta de $label", transporte),
                    Triple(costFood, "Alimentação no $label", alimentacao), Triple(costOther, "Outros custos ($label)", trabalho),
                ).mapNotNull { (v, d, cat) -> Money.parse(v)?.takeIf { it > 0 }?.let {
                    FinanceRepository.ExpenseInput(it, date, d, cat, costMethod, if (costMethod == PayMethod.CREDIT) null else costAccount, l.mainCard?.id)
                } }
                s.vm.run(if (editing) "Trabalho atualizado" else if (receivedNow > 0) "Trabalho e pagamento registrados" else "Trabalho registrado como pendente", onDone = s::back) {
                    saveWork(w, if (editing) 0 else receivedNow, account, buildSplit(mode, custom), costs = costList)
                }
            })
            if (editing) OutlinedButton(onClick = { askDelete = true }) { Text("Excluir trabalho", color = MaterialTheme.colorScheme.error) }
        }
    }
    if (askDelete) ConfirmDialog("Excluir trabalho?", "Os pagamentos ligados a ele também serão excluídos.", "Excluir", danger = true,
        onConfirm = { s.vm.run("Trabalho excluído", onDone = s::back) { deleteWork(old!!.id) } }, onDismiss = { askDelete = false })
}

/* ======================= Registrar pagamento ======================= */

@Composable
fun ReceiveScreen(s: AppState) {
    val l = s.ledger
    val c = s.calc
    val fin = LocalFin.current
    val rec = remember(l) { c.receivables() }
    var receiving by remember { mutableStateOf<Receivable?>(null) }
    val bills = c.billStatuses().filter { it.state != BillState.PAID }

    ScreenScaffold("Registrar pagamento", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            SectionTitle("Recebi um pagamento")
            if (rec.isEmpty()) Label("Nada pendente para receber. Trabalhos e vendas que ainda não foram pagos aparecem aqui.")
            if (rec.isNotEmpty()) FinCard(padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                rec.forEachIndexed { i, r ->
                    if (i > 0) Divider()
                    ListRow(r.title, "${r.sourceName} · ${Dates.relative(r.date)}" + if (r.receivedCents > 0) " · já recebeu ${Money.format(r.receivedCents)}" else "",
                        leading = { Dot(fin.warn) }, trailing = { MoneyText(r.pendingCents, style = MaterialTheme.typography.titleSmall) }, onClick = { receiving = r })
                }
            }
            PrimaryButton("Outra entrada de dinheiro", onClick = { s.go(R.income()) })

            SectionTitle("Paguei uma conta")
            if (bills.isEmpty()) Label("Nenhuma conta ativa pendente este mês. Ative suas contas em Contas a pagar.")
            if (bills.isNotEmpty()) FinCard(padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                bills.forEachIndexed { i, b ->
                    if (i > 0) Divider()
                    ListRow(b.bill.name, b.dueDate?.let { "vence ${Dates.relative(it)}" } ?: "sem vencimento definido",
                        leading = { Dot(if (b.state == BillState.OVERDUE) fin.expense else fin.warn) },
                        trailing = { MoneyText(b.expectedCents, style = MaterialTheme.typography.titleSmall) }, onClick = { s.go(R.expense(bill = b.bill.id)) })
                }
            }
            SectionTitle("Paguei a fatura do cartão")
            PrimaryButton("Ir para o cartão", onClick = { s.go(R.CARD) })
        }
    }

    receiving?.let { r ->
        var amount by remember(r) { mutableStateOf(Money.toInput(r.pendingCents)) }
        var account by remember(r) { mutableStateOf(l.dailyAccount?.id) }
        var date by remember(r) { mutableStateOf(c.today) }
        var mode by remember(r) { mutableStateOf(DistMode.DEFAULT) }
        val custom = remember(r) { mutableStateMapOf<Long, String>() }
        AlertDialog(
            onDismissRequest = { receiving = null },
            title = { Text("Receber: ${r.title}") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    MoneyField(amount, { amount = it }, "Valor recebido")
                    Dropdown("Conta", accountOptions(l), account, { account = it })
                    DateField(date, { date = it })
                    SplitEditor(s, Money.parse(amount), mode, { mode = it }, custom)
                }
            },
            confirmButton = {
                TextButton(enabled = (Money.parse(amount) ?: 0) > 0 && account != null, onClick = {
                    val v = Money.parse(amount)!!
                    val srcId = r.workId?.let { l.work(it)?.sourceId } ?: l.sourceOfType(SourceType.NFC)?.id
                    s.vm.run("Pagamento registrado") { addIncome(v, date, r.title, account!!, srcId, buildSplit(mode, custom), workId = r.workId, saleId = r.saleId) }
                    receiving = null
                }) { Text("Registrar") }
            },
            dismissButton = { TextButton(onClick = { receiving = null }) { Text("Cancelar") } },
        )
    }
}
