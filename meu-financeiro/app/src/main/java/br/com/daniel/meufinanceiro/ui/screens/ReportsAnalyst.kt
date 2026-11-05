package br.com.daniel.meufinanceiro.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import br.com.daniel.meufinanceiro.ai.ClaudeAnalyst
import br.com.daniel.meufinanceiro.data.BoxKind
import br.com.daniel.meufinanceiro.domain.Analyst
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.Keys
import br.com.daniel.meufinanceiro.domain.Level
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.ui.AppState
import br.com.daniel.meufinanceiro.ui.R
import br.com.daniel.meufinanceiro.ui.components.AppIcons
import br.com.daniel.meufinanceiro.ui.components.AppTextField
import br.com.daniel.meufinanceiro.ui.components.Divider
import br.com.daniel.meufinanceiro.ui.components.FinCard
import br.com.daniel.meufinanceiro.ui.components.HBarList
import br.com.daniel.meufinanceiro.ui.components.IconBadge
import br.com.daniel.meufinanceiro.ui.components.IncomeExpenseChart
import br.com.daniel.meufinanceiro.ui.components.Label
import br.com.daniel.meufinanceiro.ui.components.LineChart
import br.com.daniel.meufinanceiro.ui.components.ListRow
import br.com.daniel.meufinanceiro.ui.components.MoneyText
import br.com.daniel.meufinanceiro.ui.components.PrimaryButton
import br.com.daniel.meufinanceiro.ui.components.ProgressBar
import br.com.daniel.meufinanceiro.ui.components.ScreenScaffold
import br.com.daniel.meufinanceiro.ui.components.ScrollColumn
import br.com.daniel.meufinanceiro.ui.components.SectionTitle
import br.com.daniel.meufinanceiro.ui.components.StatTile
import br.com.daniel.meufinanceiro.ui.theme.LocalFin
import kotlinx.coroutines.launch

/* ======================= Mais ======================= */

@Composable
fun MoreScreen(s: AppState) {
    val c = s.calc
    val fin = LocalFin.current
    ScreenScaffold("Mais") { pad ->
        ScrollColumn(pad) {
            @Composable
            fun item(title: String, sub: String, icon: androidx.compose.ui.graphics.vector.ImageVector, route: String) =
                FinCard(onClick = { s.go(route) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBadge(icon, MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(title, style = MaterialTheme.typography.titleMedium)
                            Text(sub, style = MaterialTheme.typography.bodySmall, color = fin.muted)
                        }
                    }
                }
            item("Assistente financeira", "Pergunte, registre e altere dados conversando", AppIcons.Spark, R.ASSISTANT)
            item("Minhas Caixinhas", "Reservado: ${Money.format(c.reserved())}", AppIcons.Box, R.BOXES)
            item("Contas a pagar", "Falta pagar no mês: ${Money.format(c.billsUnpaid())}", AppIcons.Bills, R.BILLS)
            item("Relatórios", "Para onde seu dinheiro está indo", AppIcons.Chart, R.REPORTS)
            item("Alertas", "${br.com.daniel.meufinanceiro.domain.Alerts.compute(c).size} aviso(s) agora", AppIcons.Bolt, R.ALERTS)
            item("Análises e sugestões", "Onde gastar menos e lucrar mais", AppIcons.Chart, R.ANALYST)
            item("Empréstimos", if (c.loansOutstanding() > 0) "A devolver: ${Money.format(c.loansOutstanding())}" else "Dinheiro emprestado e devoluções", AppIcons.Pay, R.LOANS)
            item("Plaquinhas NFC", "Estoque: ${c.nfcStock()} un. · lucro e vendas", AppIcons.Tag, R.NFC)
            item("Transferir entre contas", "Não conta como receita nem despesa", AppIcons.Swap, R.TRANSFER)
            item("Configurações", "Contas, cartão, categorias, percentuais, tema, PIN, backup", AppIcons.Settings, R.SETTINGS)
        }
    }
}

/* ======================= Relatórios ======================= */

@Composable
fun ReportsScreen(s: AppState) {
    val l = s.ledger
    val c = s.calc
    val fin = LocalFin.current
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val period by s.vm.period.collectAsState()
    val r = remember(l, period) { c.report(period) }
    val prev = remember(l, period) { c.report(period.previous()) }
    val csvLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri != null) scope.launch { runCatching { s.vm.backup.writeTo(uri, s.vm.backup.csv(l)) }.onSuccess { s.vm.toast("CSV exportado") }.onFailure { s.vm.toast("Falha ao exportar: ${it.message}") } }
    }
    val htmlLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/html")) { uri ->
        if (uri != null) scope.launch { runCatching { s.vm.backup.writeTo(uri, s.vm.backup.htmlReport(l, period)) }.onSuccess { s.vm.toast("Relatório exportado") }.onFailure { s.vm.toast("Falha ao exportar: ${it.message}") } }
    }

    ScreenScaffold("Relatórios", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            PeriodSelector(period) { s.vm.period.value = it }
            HealthCard(s)
            FinCard {
                Text("Resumo do período", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                val pos = c.position()
                listOf(
                    "Receitas brutas (trabalho e vendas)" to Money.format(r.incomeCents) + (delta(r.incomeCents, prev.incomeCents)?.let { "  ($it)" } ?: ""),
                    "Faturamento dos trabalhos" to Money.format(r.grossRevenueCents),
                    "Custos dos trabalhos" to Money.format(r.profitCostsCents),
                    "Lucro líquido" to Money.format(r.netProfitCents),
                    "Despesas pessoais" to Money.format(r.ownExpenseCents) + (delta(r.ownExpenseCents, prev.ownExpenseCents)?.let { "  ($it)" } ?: ""),
                    "Gastos com transporte" to Money.format(r.transportCents),
                    "Reinvestido em estoque NFC" to Money.format(r.nfcStockCents),
                    "Entradas que não são renda" to Money.format(r.otherInCents),
                    "Saldo livre (hoje)" to Money.format(pos.freeCents),
                    "Dinheiro reservado (hoje)" to Money.format(pos.reservedCents),
                    "Patrimônio total (hoje)" to Money.format(pos.patrimonyCents),
                    "Fatura em aberto (hoje)" to Money.format(pos.cardDebtCents),
                ).forEach { (a, b) -> Row(Modifier.padding(vertical = 3.dp)) { Text(a, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium); Text(b, style = MaterialTheme.typography.titleSmall) } }
                Spacer(Modifier.height(4.dp))
                Label("Transferências, caixinhas, pagamento de fatura, empréstimos e ajustes não contam como receita nem despesa. Comparação com ${period.previous().label}.")
            }
            FinCard {
                Text("Entradas × saídas", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                IncomeExpenseChart(c.incomeVsExpense(period))
            }
            FinCard {
                Text("Para onde foi o dinheiro", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                if (r.expenseByCategory.isEmpty()) Label("Sem gastos no período.")
                else HBarList(r.expenseByCategory.map { (l.category(it.first)?.name ?: "Sem categoria") to it.second }, fin.chartExpense, maxItems = 14)
            }
            FinCard {
                Text("De onde veio o dinheiro", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                if (r.incomeBySource.isEmpty()) Label("Sem entradas no período.")
                else HBarList(r.incomeBySource.map { (l.source(it.first)?.name ?: "Sem fonte") to it.second }, fin.chartIncome)
                if (r.hoursBySource.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    r.hoursBySource.forEach { (sid, h) ->
                        val v = r.workIncomeBySource[sid] ?: 0
                        Label("${l.source(sid)?.name}: ${fmtHours(h)} trabalhadas · ${if (h > 0) Money.format((v / h).toLong()) else "—"}/h")
                    }
                }
            }
            FinCard {
                Text("Evolução do saldo (patrimônio)", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LineChart(c.netWorthSeries(period).map { Dates.formatShort(it.first) to it.second }, MaterialTheme.colorScheme.primary, description = "Patrimônio no período")
            }
            FinCard {
                Text("Evolução das metas", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                c.activeBoxes.filter { it.kind != BoxKind.SPENDING }.forEach { b ->
                    val g = c.goal(b)
                    val hist = c.boxBalanceByMonth(b.id, 6)
                    Column(Modifier.padding(vertical = 6.dp)) {
                        Row {
                            Text(b.name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Text(Money.format(g.balanceCents) + (g.goalCents?.let { " / ${Money.short(it)}" } ?: ""), style = MaterialTheme.typography.bodySmall, color = fin.muted)
                        }
                        Spacer(Modifier.height(4.dp))
                        ProgressBar(g.progress, Color(b.color))
                        Label("6 meses: " + hist.joinToString("  ") { "${Dates.monthShortLabel(it.first)} ${Money.short(it.second)}" })
                    }
                }
            }
            FinCard {
                Text("NFC no período", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                listOf(
                    "Unidades vendidas" to "${r.nfc.unitsSold}", "Faturamento" to Money.format(r.nfc.revenueCents), "Custo das plaquinhas" to Money.format(r.nfc.costCents),
                    "Lucro bruto" to Money.format(r.nfc.grossProfitCents), "Lucro líquido" to Money.format(r.nfc.netProfitCents), "Recebido" to Money.format(r.nfc.receivedCents),
                    "A receber" to Money.format(r.nfc.pendingCents),
                ).forEach { (a, b) -> Row(Modifier.padding(vertical = 2.dp)) { Text(a, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium); Text(b, style = MaterialTheme.typography.titleSmall) } }
            }
            SectionTitle("Maiores gastos do período")
            FinCard(padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                val top = r.expenseEvents.sortedByDescending { it.amountCents }.take(8)
                if (top.isEmpty()) Label("Nenhum gasto.")
                top.forEachIndexed { i, e ->
                    if (i > 0) Divider()
                    ListRow(e.description, Dates.relative(e.date) + " · " + (l.category(e.categoryId)?.name ?: "") + if (e.viaCard) " · crédito" else "",
                        trailing = { MoneyText(e.amountCents, style = MaterialTheme.typography.titleSmall) })
                }
            }
            SectionTitle("Exportar")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { csvLauncher.launch("meu-financeiro-${Dates.format(c.today).replace('/', '-')}.csv") }, modifier = Modifier.weight(1f)) { Text("CSV (planilha)") }
                OutlinedButton(onClick = { htmlLauncher.launch("relatorio-${period.label.replace(' ', '-').replace('/', '-')}.html") }, modifier = Modifier.weight(1f)) { Text("Relatório") }
            }
            TextButton(onClick = {
                val text = s.vm.backup.htmlReport(l, period)
                val send = Intent(Intent.ACTION_SEND).apply { type = "text/html"; putExtra(Intent.EXTRA_TEXT, text); putExtra(Intent.EXTRA_SUBJECT, "Relatório ${period.label}") }
                runCatching { ctx.startActivity(Intent.createChooser(send, "Compartilhar relatório")) }
            }) { Text("Compartilhar relatório…") }
            Label("O relatório abre no navegador e pode ser salvo em PDF pelo menu Imprimir.")
        }
    }
}

private fun delta(now: Long, before: Long): String? {
    if (before <= 0) return null
    val pct = (now - before) * 100 / before
    return (if (pct >= 0) "+" else "") + "$pct% vs. anterior"
}

/* ======================= Analista ======================= */

@Composable
fun AnalystScreen(s: AppState) {
    val l = s.ledger
    val c = s.calc
    val fin = LocalFin.current
    val scope = rememberCoroutineScope()
    val insights = remember(l) { Analyst.analyze(c) }
    val aiEnabled = l.settingBool(Keys.AI_ENABLED)
    val hasKey = remember(aiEnabled) { s.vm.secrets.has("anthropic_key") }
    var question by rememberSaveable { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var answer by rememberSaveable { mutableStateOf<String?>(null) }
    var showSummary by remember { mutableStateOf(false) }

    ScreenScaffold("Analista financeiro", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            Label("Análise feita no próprio celular, sem internet, com base nos seus lançamentos.")
            insights.forEach { ins ->
                val color = when (ins.level) { Level.ALERT -> fin.expense; Level.WARN -> fin.warn; Level.GOOD -> fin.income; Level.INFO -> fin.info }
                FinCard {
                    Row(verticalAlignment = Alignment.Top) {
                        IconBadge(AppIcons.Spark, color, 34)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(ins.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(2.dp))
                            Text(ins.body, style = MaterialTheme.typography.bodyMedium)
                            ins.tip?.let { Spacer(Modifier.height(6.dp)); Text("Sugestão: $it", style = MaterialTheme.typography.bodyMedium, color = fin.muted) }
                        }
                    }
                }
            }

            SectionTitle("Análise profunda com Claude (opcional)")
            if (!aiEnabled || !hasKey) {
                FinCard {
                    Text("Converse com uma IA sobre o seu dinheiro", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text("Ative em Configurações → Inteligência artificial e cole a sua chave da API da Anthropic. Só um resumo com totais é enviado (sem nomes de clientes, descrições ou observações), e só quando você tocar em Analisar.",
                        style = MaterialTheme.typography.bodyMedium, color = fin.muted)
                    Spacer(Modifier.height(10.dp))
                    PrimaryButton("Configurar IA", onClick = { s.go(R.settings("ai")) })
                }
            } else {
                AppTextField(question, { question = it }, "Pergunte algo (opcional)", singleLine = false,
                    supporting = "Ex.: como chego mais rápido no computador? Vale baixar o preço da plaquinha?")
                PrimaryButton(if (loading) "Analisando…" else "Analisar com Claude", enabled = !loading, onClick = {
                    loading = true
                    val key = s.vm.secrets.get("anthropic_key")
                    if (key == null) { s.vm.toast("Chave não encontrada. Cole de novo em Configurações."); loading = false; return@PrimaryButton }
                    scope.launch {
                        when (val res = ClaudeAnalyst.analyze(key, ClaudeAnalyst.buildSummary(c), question, l.userName)) {
                            is ClaudeAnalyst.Result.Ok -> answer = res.text
                            is ClaudeAnalyst.Result.Error -> s.vm.toast(res.message)
                        }
                        loading = false
                    }
                })
                if (loading) Row(verticalAlignment = Alignment.CenterVertically) { CircularProgressIndicator(Modifier.padding(4.dp)); Spacer(Modifier.width(8.dp)); Label("Isso pode levar alguns segundos.") }
                TextButton(onClick = { showSummary = !showSummary }) { Text(if (showSummary) "Esconder o que é enviado" else "Ver exatamente o que é enviado") }
                if (showSummary) FinCard { Text(ClaudeAnalyst.buildSummary(c), style = MaterialTheme.typography.bodySmall) }
                answer?.let { FinCard { MarkdownText(it) } }
            }
        }
    }
}

/** Minimal markdown: ## headings, - bullets, **bold**. */
@Composable
fun MarkdownText(md: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        md.lines().forEach { raw ->
            val line = raw.trimEnd()
            when {
                line.isBlank() -> Spacer(Modifier.height(4.dp))
                line.startsWith("#") -> Text(line.trimStart('#', ' '), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 6.dp))
                line.trimStart().startsWith("- ") || line.trimStart().startsWith("* ") -> Row {
                    Text("•  ", style = MaterialTheme.typography.bodyMedium)
                    Text(bold(line.trimStart().drop(2)), style = MaterialTheme.typography.bodyMedium)
                }
                else -> Text(bold(line), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

private fun bold(text: String) = buildAnnotatedString {
    val parts = text.split("**")
    parts.forEachIndexed { i, p -> if (i % 2 == 1) withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(p) } else append(p) }
}

/** "Como estou financeiramente?" — objective summary from real data, plus optional deeper analysis with Claude. */
@Composable
fun HealthCard(s: AppState) {
    val fin = LocalFin.current
    val sections = remember(s.ledger) { br.com.daniel.meufinanceiro.domain.Health.summary(s.calc) }
    FinCard {
        Text("Como estou financeiramente?", style = MaterialTheme.typography.titleMedium)
        sections.forEach { sec ->
            Spacer(Modifier.height(8.dp))
            Label(sec.title)
            sec.lines.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { s.vm.ask("Como estou financeiramente? Faça uma análise com sugestões."); s.go(R.ASSISTANT) }) {
            Text(if (s.ledger.settingBool(Keys.AI_ENABLED)) "Pedir uma análise mais profunda à assistente" else "Perguntar à assistente")
        }
        Text("Baseado só nos seus lançamentos. Sem dados suficientes, o app diz que faltam dados.", style = MaterialTheme.typography.bodySmall, color = fin.muted)
    }
}
