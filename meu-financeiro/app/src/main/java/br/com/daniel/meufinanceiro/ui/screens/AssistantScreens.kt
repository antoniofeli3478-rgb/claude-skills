@file:OptIn(ExperimentalLayoutApi::class)

package br.com.daniel.meufinanceiro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import br.com.daniel.meufinanceiro.data.ActionStatus
import br.com.daniel.meufinanceiro.domain.Alerts
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.Keys
import br.com.daniel.meufinanceiro.domain.Level
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.ui.AppState
import br.com.daniel.meufinanceiro.ui.R
import br.com.daniel.meufinanceiro.ui.components.AppTextField
import br.com.daniel.meufinanceiro.ui.components.Chip
import br.com.daniel.meufinanceiro.ui.components.ConfirmDialog
import br.com.daniel.meufinanceiro.ui.components.DateField
import br.com.daniel.meufinanceiro.ui.components.Divider
import br.com.daniel.meufinanceiro.ui.components.Dot
import br.com.daniel.meufinanceiro.ui.components.Dropdown
import br.com.daniel.meufinanceiro.ui.components.EmptyState
import br.com.daniel.meufinanceiro.ui.components.FinCard
import br.com.daniel.meufinanceiro.ui.components.Label
import br.com.daniel.meufinanceiro.ui.components.ListRow
import br.com.daniel.meufinanceiro.ui.components.MoneyDialog
import br.com.daniel.meufinanceiro.ui.components.MoneyField
import br.com.daniel.meufinanceiro.ui.components.MoneyText
import br.com.daniel.meufinanceiro.ui.components.PrimaryButton
import br.com.daniel.meufinanceiro.ui.components.ScreenScaffold
import br.com.daniel.meufinanceiro.ui.components.ScrollColumn
import br.com.daniel.meufinanceiro.ui.components.SectionTitle
import br.com.daniel.meufinanceiro.ui.components.SwitchRow
import br.com.daniel.meufinanceiro.ui.theme.LocalFin

/* ======================= Assistente ======================= */

@Composable
fun AssistantScreen(s: AppState) {
    val vm = s.vm
    val fin = LocalFin.current
    val chat by vm.chat.collectAsState()
    val actions by vm.actions.collectAsState()
    val thinking by vm.thinking.collectAsState()
    var input by rememberSaveable { mutableStateOf("") }
    var showLog by remember { mutableStateOf(false) }
    var askClear by remember { mutableStateOf(false) }
    val list = rememberLazyListState()
    val claudeOn = s.ledger.settingBool(Keys.AI_ENABLED) && vm.secrets.has("anthropic_key")
    LaunchedEffect(Unit) { vm.refreshChat() }
    LaunchedEffect(chat.size, thinking) { if (chat.isNotEmpty()) list.animateScrollToItem(chat.size - 1) }
    fun send(t: String) { if (t.isNotBlank() && !thinking) { vm.ask(t); input = "" } }

    ScreenScaffold("Assistente", onBack = s::back, actions = {
        TextButton(onClick = { showLog = !showLog }) { Text(if (showLog) "Conversa" else "Ações") }
    }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).imePadding()) {
            Text(if (claudeOn) "Conversando com o Claude: consulta e altera seus dados reais, sempre pedindo confirmação."
                else "Modo offline: entende perguntas e pedidos comuns. Para conversa livre, ative o Claude em Configurações.",
                style = MaterialTheme.typography.bodySmall, color = fin.muted, modifier = Modifier.padding(horizontal = 16.dp))
            if (thinking) LinearProgressIndicator(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp))

            if (showLog) {
                LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    item { Label("Histórico de ações da assistente (mais recentes primeiro)") }
                    if (actions.isEmpty()) item { Text("Nenhuma ação ainda.", color = fin.muted) }
                    items(actions, key = { it.id }) { a ->
                        ListRow(a.summary, "${java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale("pt", "BR")).format(java.util.Date(a.createdAt))} · " + when (a.status) {
                            ActionStatus.DONE -> "executada"; ActionStatus.PENDING -> "aguardando confirmação"; ActionStatus.CANCELLED -> "cancelada"
                            ActionStatus.UNDONE -> "desfeita"; else -> "falhou" },
                            leading = { Dot(when (a.status) { ActionStatus.DONE -> fin.income; ActionStatus.PENDING -> fin.warn; ActionStatus.FAILED -> fin.expense; else -> fin.faint }) },
                            trailing = { if (a.status == ActionStatus.DONE && a.undo != null) TextButton(onClick = { vm.undoAction(a.id) }) { Text("Desfazer") } })
                    }
                    item { TextButton(onClick = { askClear = true }) { Text("Apagar a conversa (o histórico de ações fica)") } }
                }
            } else {
                LazyColumn(Modifier.weight(1f), state = list, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (chat.isEmpty()) item {
                        Text("Pergunte qualquer coisa sobre seu dinheiro ou peça para registrar algo.", style = MaterialTheme.typography.bodyMedium, color = fin.muted)
                    }
                    items(chat, key = { it.id }) { m ->
                        when {
                            m.role == "user" -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.large, modifier = Modifier.widthIn(max = 300.dp)) {
                                    Text(m.text, Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                            m.actionId != null -> {
                                val a = actions.firstOrNull { it.id == m.actionId }
                                FinCard(padding = PaddingValues(14.dp)) {
                                    Label("Ação proposta")
                                    Text(m.text, style = MaterialTheme.typography.bodyMedium)
                                    when (a?.status) {
                                        ActionStatus.PENDING -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                                            Button(onClick = { vm.confirmAction(a.id) }, enabled = !thinking) { Text("Confirmar") }
                                            OutlinedButton(onClick = { vm.cancelAction(a.id) }, enabled = !thinking) { Text("Cancelar") }
                                        }
                                        ActionStatus.DONE -> Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Executada", color = fin.income, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                                            if (a.undo != null) TextButton(onClick = { vm.undoAction(a.id) }, enabled = !thinking) { Text("Desfazer") }
                                        }
                                        ActionStatus.CANCELLED -> Text("Cancelada", color = fin.muted, style = MaterialTheme.typography.labelLarge)
                                        ActionStatus.UNDONE -> Text("Desfeita", color = fin.muted, style = MaterialTheme.typography.labelLarge)
                                        else -> Text("Não executada", color = fin.expense, style = MaterialTheme.typography.labelLarge)
                                    }
                                }
                            }
                            else -> Box(Modifier.fillMaxWidth()) {
                                Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh, shape = MaterialTheme.shapes.large, modifier = Modifier.widthIn(max = 330.dp)) {
                                    Column(Modifier.padding(12.dp)) { MarkdownText(m.text) }
                                }
                            }
                        }
                    }
                }
            }

            if (chat.isEmpty() && !showLog) FlowRow(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Quanto posso gastar?", "Quanto lucrei esta semana?", "Qual trabalho compensa mais?", "Quanto está minha fatura?", "Como estou financeiramente?")
                    .forEach { q -> Chip(q, false) { send(q) } }
            }
            Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(input, { input = it }, placeholder = { Text("Escreva aqui…") }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.large,
                    maxLines = 4, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send), keyboardActions = KeyboardActions(onSend = { send(input) }))
                TextButton(onClick = { send(input) }, enabled = input.isNotBlank() && !thinking) { Text("Enviar") }
            }
        }
    }
    if (askClear) ConfirmDialog("Apagar a conversa?", "As mensagens somem. As ações registradas e seus dados continuam.", "Apagar", danger = true,
        onConfirm = { vm.clearChat() }, onDismiss = { askClear = false })
}

/* ======================= Alertas ======================= */

@Composable
fun AlertsScreen(s: AppState) {
    val l = s.ledger
    val fin = LocalFin.current
    val all = remember(l) { Alerts.compute(s.calc) }
    val off = l.setting(Keys.ALERTS_OFF)?.split(',')?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val permission = androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.RequestPermission()) { granted ->
        s.vm.setAlertNotifications(granted)
        if (!granted) s.vm.toast("Sem permissão de notificação: os alertas continuam aparecendo dentro do app.")
    }
    ScreenScaffold("Alertas", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            if (all.isEmpty()) EmptyState("Tudo certo", "Nenhum alerta agora. Os alertas só aparecem quando há dados suficientes.")
            all.forEach { a ->
                val color = when (a.level) { Level.ALERT -> fin.expense; Level.WARN -> fin.warn; else -> fin.info }
                FinCard(onClick = a.route?.let { r -> { s.go(if (r == "home") R.HOME else r) } }) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Dot(color); Spacer(Modifier.padding(start = 8.dp)); Text(a.title, style = MaterialTheme.typography.titleSmall) }
                    Spacer(Modifier.height(4.dp))
                    Text(a.body, style = MaterialTheme.typography.bodyMedium)
                }
            }
            SectionTitle("Quais alertas receber")
            FinCard(padding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                Alerts.catalog.forEach { (id, label) ->
                    SwitchRow(label, id !in off, { on ->
                        val n = if (on) off - id else off + id
                        s.vm.setSetting(Keys.ALERTS_OFF, n.joinToString(",").ifBlank { null })
                    })
                }
            }
            SectionTitle("Notificações no celular")
            SwitchRow("Avisar com notificação", l.settingBool(Keys.ALERT_NOTIFY), { on ->
                if (on && android.os.Build.VERSION.SDK_INT >= 33 &&
                    androidx.core.content.ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED)
                    permission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                else s.vm.setAlertNotifications(on)
            }, "No máximo 1 notificação por dia, com o alerta mais importante. Sem notificação, os alertas aparecem só no app.")
        }
    }
}

/* ======================= Empréstimos ======================= */

@Composable
fun LoanForm(s: AppState) {
    val l = s.ledger
    var person by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var account by remember { mutableStateOf(l.dailyAccount?.id) }
    var date by remember { mutableStateOf(s.calc.today) }
    var notes by remember { mutableStateOf("") }
    val v = Money.parse(amount)
    ScreenScaffold("Empréstimo recebido", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            Label("Dinheiro emprestado entra na conta, mas não é renda e não entra no lucro. Fica registrado como \"a devolver\".")
            AppTextField(person, { person = it }, "Quem emprestou (ex.: mãe)", autoFocus = true)
            MoneyField(amount, { amount = it }, "Valor")
            Dropdown("Conta que recebeu", accountOptions(l), account, { account = it })
            DateField(date, { date = it })
            AppTextField(notes, { notes = it }, "Observações (ex.: devolver no fim do mês)", singleLine = false)
            PrimaryButton("Registrar empréstimo", enabled = person.isNotBlank() && v != null && v > 0 && account != null, onClick = {
                s.vm.run("Empréstimo registrado", onDone = s::back) { addLoan(person, v!!, date, account!!, notes = notes) }
            })
        }
    }
}

@Composable
fun LoansScreen(s: AppState) {
    val l = s.ledger
    val c = s.calc
    val fin = LocalFin.current
    var repay by remember { mutableStateOf<Long?>(null) }
    ScreenScaffold("Empréstimos", onBack = s::back, actions = { TextButton(onClick = { s.go(R.LOAN) }) { Text("Novo") } }) { pad ->
        ScrollColumn(pad) {
            if (l.loans.isEmpty()) EmptyState("Nenhum empréstimo", "Quando alguém te emprestar dinheiro, registre aqui (ou escreva \"minha mãe me emprestou 30 reais\" no lançamento rápido).")
            else FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                l.loans.forEachIndexed { i, loan ->
                    if (i > 0) Divider()
                    val out = c.loanOutstanding(loan.id)
                    ListRow(loan.person, "${Dates.format(loan.date)} · emprestou ${Money.format(loan.principalCents)}" + if (out == 0L) " · quitado" else "",
                        leading = { Dot(if (out > 0) fin.warn else fin.income) },
                        trailing = { if (out > 0) TextButton(onClick = { repay = loan.id }) { Text("Devolver") } else MoneyText(0) })
                    if (out > 0) Text("Falta devolver ${Money.format(out)}", style = MaterialTheme.typography.bodySmall, color = fin.muted, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
                }
            }
        }
    }
    repay?.let { id ->
        var account by remember(id) { mutableStateOf(l.dailyAccount?.id) }
        MoneyDialog("Devolver empréstimo", "Valor devolvido", c.loanOutstanding(id), "Sai da conta, mas não é despesa.", "Registrar",
            onConfirm = { v -> s.vm.run("Devolução registrada") { repayLoan(id, v, account!!, c.today) } }, onDismiss = { repay = null }) {
            Dropdown("Conta", accountOptions(l), account, { account = it })
        }
    }
}
