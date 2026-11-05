@file:OptIn(ExperimentalLayoutApi::class)

package br.com.daniel.meufinanceiro.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.daniel.meufinanceiro.data.BoxKind
import br.com.daniel.meufinanceiro.domain.Analyst
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.Keys
import br.com.daniel.meufinanceiro.domain.Level
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.domain.Period
import br.com.daniel.meufinanceiro.domain.PeriodKind
import br.com.daniel.meufinanceiro.ui.AppState
import br.com.daniel.meufinanceiro.ui.R
import br.com.daniel.meufinanceiro.ui.components.AppIcons
import br.com.daniel.meufinanceiro.ui.components.Banner
import br.com.daniel.meufinanceiro.ui.components.FinCard
import br.com.daniel.meufinanceiro.ui.components.HBarList
import br.com.daniel.meufinanceiro.ui.components.IconBadge
import br.com.daniel.meufinanceiro.ui.components.IncomeExpenseChart
import br.com.daniel.meufinanceiro.ui.components.Label
import br.com.daniel.meufinanceiro.ui.components.LineChart
import br.com.daniel.meufinanceiro.ui.components.MoneyText
import br.com.daniel.meufinanceiro.ui.components.ProgressBar
import br.com.daniel.meufinanceiro.ui.components.SectionTitle
import br.com.daniel.meufinanceiro.ui.components.StatTile
import br.com.daniel.meufinanceiro.ui.theme.LocalFin
import br.com.daniel.meufinanceiro.ui.theme.MoneyLarge
import java.time.LocalTime

@Composable
fun HomeScreen(s: AppState) {
    val l = s.ledger
    val c = s.calc
    val fin = LocalFin.current
    val period by s.vm.period.collectAsState()
    val week = remember(l) { c.report(Period.of(PeriodKind.WEEK, c.today)) }
    val month = remember(l) { c.report(Period.of(PeriodKind.MONTH, c.today)) }
    val rep = remember(l, period) { c.report(period) }
    val insights = remember(l) { Analyst.analyze(c) }
    var quick by rememberSaveable { mutableStateOf("") }
    var confirmQuick by remember { mutableStateOf<String?>(null) }
    var hideValues by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).statusBarsPadding().padding(horizontal = 16.dp).padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Header
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                val h = LocalTime.now().hour
                Text(if (h < 12) "Bom dia, Daniel" else if (h < 18) "Boa tarde, Daniel" else "Boa noite, Daniel", style = MaterialTheme.typography.headlineSmall)
                Text(Dates.relative(c.today).let { if (it == "Hoje") Dates.format(c.today) else it }, style = MaterialTheme.typography.bodySmall, color = fin.muted)
            }
            IconButton(onClick = { s.vm.setSetting(Keys.THEME, if (fin.isDark) "light" else "dark") }) {
                Icon(if (fin.isDark) AppIcons.Sun else AppIcons.Moon, contentDescription = if (fin.isDark) "Mudar para o modo claro" else "Mudar para o modo escuro")
            }
            IconButton(onClick = { s.go(R.SETTINGS) }) { Icon(AppIcons.Settings, contentDescription = "Configurações") }
        }

        // Hero balance
        FinCard(padding = PaddingValues(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Label("Saldo total disponível", Modifier.weight(1f))
                Text(if (hideValues) "Mostrar" else "Ocultar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clip(MaterialTheme.shapes.small).clickable { hideValues = !hideValues }.padding(6.dp))
            }
            Text(if (hideValues) "R$ ••••" else Money.format(c.totalAvailable()), style = MoneyLarge)
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(Modifier.weight(1f)) {
                    Label("Livre para gastar")
                    val free = c.freeToSpend()
                    Text(if (hideValues) "••••" else Money.format(free), style = MaterialTheme.typography.titleMedium, color = if (free < 0) fin.expense else fin.income)
                }
                Column(Modifier.weight(1f)) {
                    Label("Patrimônio (contas − cartão)")
                    Text(if (hideValues) "••••" else Money.format(c.netWorth()), style = MaterialTheme.typography.titleMedium)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                c.activeAccounts.forEach { a ->
                    Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.clip(MaterialTheme.shapes.medium).clickable { s.go(R.settings("accounts")) }) {
                        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(a.name, style = MaterialTheme.typography.labelMedium, color = fin.muted, maxLines = 1)
                            Text(if (hideValues) "••••" else Money.format(c.accountBalance(a.id)), style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }
            }
        }

        // Alerts
        insights.filter { it.level == Level.ALERT }.take(2).forEach { Banner(it.title + ": " + it.body, fin.expense, onClick = { s.go(R.ANALYST) }) }

        // Quick entry
        OutlinedTextField(
            value = quick, onValueChange = { quick = it },
            label = { Text("Lançamento rápido") },
            placeholder = { Text("ex.: almoço 25 pix · recebi 80 tio rex") },
            singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (quick.isNotBlank()) confirmQuick = quick }),
            trailingIcon = { TextButton(enabled = quick.isNotBlank(), onClick = { confirmQuick = quick }) { Text("OK") } },
        )

        // Big actions
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BigAction("Nova entrada", AppIcons.Income, fin.income, Modifier.weight(1f)) { s.go(br.com.daniel.meufinanceiro.ui.R.income()) }
            BigAction("Nova despesa", AppIcons.Expense, fin.expense, Modifier.weight(1f)) { s.go(br.com.daniel.meufinanceiro.ui.R.expense()) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SmallAction("Venda NFC", AppIcons.Tag, Modifier.weight(1f)) { s.go(br.com.daniel.meufinanceiro.ui.R.sale()) }
            SmallAction("Registrar trabalho", AppIcons.Work, Modifier.weight(1f)) { s.go(br.com.daniel.meufinanceiro.ui.R.work()) }
            SmallAction("Registrar pagamento", AppIcons.Pay, Modifier.weight(1f)) { s.go(R.RECEIVE) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SmallAction("Minhas caixinhas", AppIcons.Box, Modifier.weight(1f)) { s.go(R.BOXES) }
            SmallAction("Cartão de crédito", AppIcons.Card, Modifier.weight(1f)) { s.go(R.CARD) }
            SmallAction("Transferir", AppIcons.Swap, Modifier.weight(1f)) { s.go(R.TRANSFER) }
        }

        // Week / month
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile("Recebido na semana", week.incomeCents, Modifier.weight(1f), fin.income)
            StatTile("Recebido no mês", month.incomeCents, Modifier.weight(1f), fin.income)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile("Gasto na semana", week.personalExpenseCents, Modifier.weight(1f), fin.expense)
            StatTile("Gasto no mês", month.personalExpenseCents, Modifier.weight(1f), fin.expense,
                sub = if (month.nfcStockCents > 0) "+ ${Money.format(month.nfcStockCents)} em estoque NFC" else null)
        }

        // Status tiles
        val card = l.mainCard
        val inv = card?.let { c.invoice(it, c.currentInvoiceKey(it)) }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile("Fatura atual", inv?.remainingCents ?: 0, Modifier.weight(1f), sub = inv?.let { "fecha ${Dates.formatShort(it.closingDate)}" }) { s.go(R.CARD) }
            StatTile("Contas do mês a pagar", c.billsUnpaid(), Modifier.weight(1f), sub = "${c.billStatuses().count { it.state != br.com.daniel.meufinanceiro.domain.BillState.PAID }} pendente(s)") { s.go(R.BILLS) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile("Guardado nas caixinhas", c.totalSaved(), Modifier.weight(1f), sub = "sem contar gastos e estoque") { s.go(R.BOXES) }
            StatTile("A receber", c.pendingTotal(), Modifier.weight(1f), sub = "não conta como recebido") { s.go(R.RECEIVE) }
        }

        // Period analysis
        SectionTitle("Análise do período")
        PeriodSelector(period) { s.vm.period.value = it }

        FinCard {
            Text("Entradas × saídas", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            IncomeExpenseChart(c.incomeVsExpense(period))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile("Lucro líquido NFC", rep.nfc.netProfitCents, Modifier.weight(1f), sub = "${rep.nfc.unitsSold} un. vendidas") { s.go(R.NFC) }
            StatTile("Sobra do período", rep.incomeCents - rep.personalExpenseCents, Modifier.weight(1f), sub = "entradas − gastos pessoais")
        }

        FinCard(onClick = { s.go(R.REPORTS) }) {
            Text("Gastos por categoria", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            if (rep.expenseByCategory.isEmpty()) Text("Sem gastos neste período.", color = fin.muted, style = MaterialTheme.typography.bodyMedium)
            else HBarList(rep.expenseByCategory.map { (l.category(it.first)?.name ?: "Sem categoria") to it.second }, fin.chartExpense, maxItems = 6)
        }

        FinCard(onClick = { s.go(R.INCOME) }) {
            Text("Renda por fonte", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            if (rep.incomeBySource.isEmpty()) Text("Nenhuma entrada neste período.", color = fin.muted, style = MaterialTheme.typography.bodyMedium)
            else HBarList(rep.incomeBySource.map { (l.source(it.first)?.name ?: "Sem fonte") to it.second }, fin.chartIncome)
        }

        FinCard {
            Text("Evolução do patrimônio", style = MaterialTheme.typography.titleMedium)
            Label("Contas − cartão, fim de cada mês")
            Spacer(Modifier.height(10.dp))
            LineChart(c.netWorthByMonth(6).map { Dates.monthShortLabel(it.first) to it.second }, MaterialTheme.colorScheme.primary, description = "Patrimônio")
        }

        FinCard(onClick = { s.go(R.BOXES) }) {
            Text("Metas", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            c.activeBoxes.filter { it.kind != BoxKind.SPENDING && it.kind != BoxKind.NFC_STOCK }.forEach { b ->
                val g = c.goal(b)
                Column(Modifier.padding(vertical = 6.dp)) {
                    Row {
                        Text(b.name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text(Money.format(g.balanceCents) + (g.goalCents?.let { " de " + Money.short(it) } ?: ""), style = MaterialTheme.typography.bodySmall, color = fin.muted)
                    }
                    Spacer(Modifier.height(6.dp))
                    ProgressBar(g.progress, Color(b.color))
                }
            }
        }

        // Analyst teaser
        FinCard(onClick = { s.go(R.ANALYST) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(AppIcons.Spark, MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Analista financeiro", style = MaterialTheme.typography.titleMedium)
                    Text("${insights.size} observação(ões) sobre seu dinheiro", style = MaterialTheme.typography.bodySmall, color = fin.muted)
                }
            }
            insights.filter { it.level != Level.ALERT }.take(2).forEach {
                Spacer(Modifier.height(10.dp))
                Text(it.title, style = MaterialTheme.typography.titleSmall)
                Text(it.body, style = MaterialTheme.typography.bodySmall, color = fin.muted, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        }
    }

    confirmQuick?.let { text -> QuickEntryConfirm(s, text) { confirmQuick = null; quick = "" } }
}

@Composable
private fun BigAction(label: String, icon: ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.heightIn(min = 76.dp).clip(MaterialTheme.shapes.large).clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large, color = color.copy(alpha = 0.12f), border = BorderStroke(1.dp, color.copy(alpha = 0.30f)),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(10.dp))
            Text(label, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun SmallAction(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    val fin = LocalFin.current
    Surface(
        modifier = modifier.heightIn(min = 84.dp).clip(MaterialTheme.shapes.large).clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainer, border = BorderStroke(1.dp, fin.cardBorder),
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, maxLines = 2)
        }
    }
}
