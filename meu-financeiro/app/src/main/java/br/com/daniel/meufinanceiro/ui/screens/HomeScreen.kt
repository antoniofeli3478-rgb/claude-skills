package br.com.daniel.meufinanceiro.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.daniel.meufinanceiro.domain.Alerts
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.Keys
import br.com.daniel.meufinanceiro.domain.Level
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.domain.Period
import br.com.daniel.meufinanceiro.domain.PeriodKind
import br.com.daniel.meufinanceiro.ui.AppState
import br.com.daniel.meufinanceiro.ui.R
import br.com.daniel.meufinanceiro.ui.components.AppIcons
import br.com.daniel.meufinanceiro.ui.components.Divider
import br.com.daniel.meufinanceiro.ui.components.FinCard
import br.com.daniel.meufinanceiro.ui.components.Label
import br.com.daniel.meufinanceiro.ui.theme.LocalFin
import br.com.daniel.meufinanceiro.ui.theme.MoneyLarge

/**
 * Home, redesigned in 1.1: one question per block, in this order —
 * 1 saldo livre · 2 lucro do mês · 3 gastos do mês · 4 fatura · 5 ações · 6 assistente.
 * Everything else lives in its own section.
 */
@Composable
fun HomeScreen(s: AppState) {
    val l = s.ledger
    val c = s.calc
    val fin = LocalFin.current
    val pos = remember(l) { c.position() }
    val month = remember(l) { c.report(Period.of(PeriodKind.MONTH, c.today)) }
    val alert = remember(l) { Alerts.compute(c).firstOrNull { it.level == Level.ALERT || it.level == Level.WARN } }
    var quick by rememberSaveable { mutableStateOf("") }
    var confirmQuick by remember { mutableStateOf<String?>(null) }
    var hide by rememberSaveable { mutableStateOf(false) }
    var explain by remember { mutableStateOf(false) }
    var moreActions by remember { mutableStateOf(false) }
    fun m(v: Long) = if (hide) "R$ ••••" else Money.format(v)

    Column(
        Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).statusBarsPadding().padding(horizontal = 20.dp).padding(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Olá, ${l.userName}", style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(Dates.format(c.today), style = MaterialTheme.typography.bodySmall, color = fin.muted)
            }
            IconButton(onClick = { s.vm.setSetting(Keys.THEME, if (fin.isDark) "light" else "dark") }) {
                Icon(if (fin.isDark) AppIcons.Sun else AppIcons.Moon, contentDescription = if (fin.isDark) "Mudar para o modo claro" else "Mudar para o modo escuro", tint = fin.muted)
            }
            IconButton(onClick = { s.go(R.SETTINGS) }) { Icon(AppIcons.Settings, contentDescription = "Configurações", tint = fin.muted) }
        }

        // One-time review after the 1.1 update
        if (l.settingBool(Keys.REVIEW_BALANCES)) ReviewCard(s)

        // 1 — Saldo livre
        FinCard(padding = PaddingValues(22.dp), onClick = { explain = true }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Label("Saldo livre para gastar", Modifier.weight(1f))
                Text(if (hide) "Mostrar" else "Ocultar", style = MaterialTheme.typography.labelMedium, color = fin.muted,
                    modifier = Modifier.clip(MaterialTheme.shapes.small).clickable { hide = !hide }.padding(4.dp))
            }
            Spacer(Modifier.height(4.dp))
            Text(m(pos.freeCents), style = MoneyLarge, color = if (pos.freeCents < 0) fin.expense else Color.Unspecified)
            if (pos.unpaidBillsCents > 0) Text("Depois das contas do mês: ${m(pos.freeAfterBillsCents)}", style = MaterialTheme.typography.bodySmall, color = fin.muted)
            Spacer(Modifier.height(16.dp))
            Row {
                Column(Modifier.weight(1f)) { Label("Reservado"); Text(m(pos.reservedCents), style = MaterialTheme.typography.titleSmall) }
                Column(Modifier.weight(1f)) { Label("Patrimônio total"); Text(m(pos.patrimonyCents), style = MaterialTheme.typography.titleSmall) }
            }
        }

        alert?.let { a ->
            Surface(
                Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).clickable { s.go(R.ALERTS) },
                color = (if (a.level == Level.ALERT) fin.expense else fin.warn).copy(alpha = 0.10f), shape = MaterialTheme.shapes.medium,
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(a.title, style = MaterialTheme.typography.titleSmall, color = if (a.level == Level.ALERT) fin.expense else fin.warn)
                    Text(a.body, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        // 2, 3, 4 — month in three lines
        FinCard(padding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)) {
            MetricRow("Lucro líquido do mês", m(month.netProfitCents), if (month.netProfitCents >= 0) fin.income else fin.expense,
                "faturou ${m(month.grossRevenueCents)} · custos ${m(month.profitCostsCents)}") { s.go(R.INCOME) }
            Divider()
            MetricRow("Gastos do mês", m(month.personalExpenseCents), null,
                if (month.workCostsCents > 0) "inclui ${m(month.workCostsCents)} de custos de trabalho" else null) { s.go(R.REPORTS) }
            Divider()
            val card = l.mainCard
            val inv = card?.let { c.invoice(it, c.currentInvoiceKey(it)) }
            MetricRow("Fatura atual", m(inv?.remainingCents ?: 0), null,
                inv?.let { i -> i.dueDate?.let { "vence ${Dates.formatShort(it)}" } ?: "fecha ${Dates.formatShort(i.closingDate)}" }) { s.go(R.CARD) }
        }

        // Quick entry (kept and improved)
        OutlinedTextField(
            value = quick, onValueChange = { quick = it },
            placeholder = { Text("Lançamento rápido: \"uber 15 pix\", \"tio rex ganhei 80\"", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (quick.isNotBlank()) confirmQuick = quick }),
            trailingIcon = { TextButton(enabled = quick.isNotBlank(), onClick = { confirmQuick = quick }) { Text("OK") } },
        )

        // 5 — Quick actions (4 + more)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAction("Entrada", AppIcons.Income, fin.income, Modifier.weight(1f)) { s.go(R.income()) }
            QuickAction("Despesa", AppIcons.Expense, fin.expense, Modifier.weight(1f)) { s.go(R.expense()) }
            QuickAction("Trabalho", AppIcons.Work, MaterialTheme.colorScheme.primary, Modifier.weight(1f)) { s.go(R.work()) }
            QuickAction("Outras", AppIcons.More, fin.muted, Modifier.weight(1f)) { moreActions = true }
        }

        // 6 — Assistant
        FinCard(onClick = { s.go(R.ASSISTANT) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(AppIcons.Spark, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Assistente financeira", style = MaterialTheme.typography.titleSmall)
                    Text("Pergunte \"quanto posso gastar?\" ou peça para registrar algo", style = MaterialTheme.typography.bodySmall, color = fin.muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }

    confirmQuick?.let { text -> QuickEntryConfirm(s, text) { confirmQuick = null; quick = "" } }

    if (explain) AlertDialog(
        onDismissRequest = { explain = false },
        title = { Text("Como o saldo livre é calculado") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ExplainRow("Dinheiro nas contas", pos.accountsCents)
                ExplainRow("− Caixinhas (reservado)", -pos.reservedCents)
                ExplainRow("− Já comprometido no cartão", -pos.cardDebtCents)
                Divider()
                ExplainRow("= Saldo livre", pos.freeCents, bold = true)
                if (pos.unpaidBillsCents > 0) ExplainRow("− Contas do mês a pagar", -pos.unpaidBillsCents)
                if (pos.loansCents > 0) Text("Empréstimos a devolver (${Money.format(pos.loansCents)}) entram só no patrimônio.", style = MaterialTheme.typography.bodySmall, color = fin.muted)
                Spacer(Modifier.height(4.dp))
                Text("O dinheiro das caixinhas é protegido: nunca aparece como disponível para gastar. Guardar numa caixinha diminui o livre; retirar aumenta.",
                    style = MaterialTheme.typography.bodySmall, color = fin.muted)
            }
        },
        confirmButton = { TextButton(onClick = { explain = false }) { Text("Entendi") } },
    )

    if (moreActions) AlertDialog(
        onDismissRequest = { moreActions = false },
        title = { Text("Ações") },
        text = {
            Column {
                listOf(
                    "Venda de plaquinhas NFC" to R.sale(), "Registrar pagamento recebido" to R.RECEIVE, "Empréstimo recebido" to R.LOAN,
                    "Transferir entre contas" to R.TRANSFER, "Minhas caixinhas" to R.BOXES, "Cartão de crédito" to R.CARD, "Contas a pagar" to R.BILLS,
                ).forEach { (label, route) ->
                    Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small)
                        .clickable { moreActions = false; s.go(route) }.padding(vertical = 12.dp, horizontal = 4.dp))
                }
            }
        },
        confirmButton = { TextButton(onClick = { moreActions = false }) { Text("Fechar") } },
    )
}

@Composable
private fun MetricRow(label: String, value: String, color: Color?, sub: String?, onClick: () -> Unit) {
    val fin = LocalFin.current
    Row(Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small).clickable(onClick = onClick).padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            if (sub != null) Text(sub, style = MaterialTheme.typography.bodySmall, color = fin.muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(value, style = MaterialTheme.typography.titleMedium, color = color ?: Color.Unspecified)
    }
}

@Composable
private fun ExplainRow(label: String, cents: Long, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, style = if (bold) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(Money.format(cents), style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
private fun QuickAction(label: String, icon: ImageVector, tint: Color, modifier: Modifier, onClick: () -> Unit) {
    val fin = LocalFin.current
    Surface(
        modifier = modifier.height(78.dp).clip(MaterialTheme.shapes.large).clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainer, border = BorderStroke(1.dp, fin.cardBorder),
    ) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(6.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, maxLines = 1)
        }
    }
}

/** Shown once after updating to 1.1: lets Antônio check the new free/reserved split before trusting it. */
@Composable
private fun ReviewCard(s: AppState) {
    val c = s.calc
    val fin = LocalFin.current
    val pos = c.position()
    FinCard {
        Text("Confira seus saldos (nova versão)", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        Text("Agora o app separa dinheiro livre do dinheiro das caixinhas. Nenhum dado foi apagado. Veja se bate com seus bancos:",
            style = MaterialTheme.typography.bodySmall, color = fin.muted)
        Spacer(Modifier.height(8.dp))
        c.activeAccounts.forEach { a ->
            val boxes = c.boxesInAccount(a.id)
            Text("${a.name}: ${Money.format(c.accountBalance(a.id))}" + if (boxes > 0) " (caixinhas ${Money.format(boxes)}, fora delas ${Money.format(c.accountOutsideBoxes(a.id))})" else "",
                style = MaterialTheme.typography.bodySmall)
        }
        Text("Livre ${Money.format(pos.freeCents)} · Reservado ${Money.format(pos.reservedCents)} · Patrimônio ${Money.format(pos.patrimonyCents)}", style = MaterialTheme.typography.bodySmall)
        Row {
            TextButton(onClick = { s.vm.setSetting(Keys.REVIEW_BALANCES, null) }) { Text("Está certo") }
            TextButton(onClick = { s.go(R.settings("accounts")) }) { Text("Corrigir saldos") }
        }
    }
}
