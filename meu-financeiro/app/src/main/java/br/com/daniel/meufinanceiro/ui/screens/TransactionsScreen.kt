package br.com.daniel.meufinanceiro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.daniel.meufinanceiro.data.Txn
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.domain.QuickEntryParser
import br.com.daniel.meufinanceiro.ui.AppState
import br.com.daniel.meufinanceiro.ui.R
import br.com.daniel.meufinanceiro.ui.components.ChoiceChips
import br.com.daniel.meufinanceiro.ui.components.Dot
import br.com.daniel.meufinanceiro.ui.components.EmptyState
import br.com.daniel.meufinanceiro.ui.components.Label
import br.com.daniel.meufinanceiro.ui.components.ListRow
import br.com.daniel.meufinanceiro.ui.components.ScreenScaffold
import br.com.daniel.meufinanceiro.ui.theme.LocalFin

@Composable
fun TransactionsScreen(s: AppState) {
    val l = s.ledger
    val fin = LocalFin.current
    var filter by rememberSaveable { mutableStateOf("Tudo") }
    var query by rememberSaveable { mutableStateOf("") }
    var removing by remember { mutableStateOf<Txn?>(null) }
    val all = remember(l) { movements(l, s.calc) }
    val q = QuickEntryParser.normalize(query.trim())
    val items = all.filter { (filter == "Tudo" || it.kind == filter) && (q.isEmpty() || QuickEntryParser.normalize(it.title + " " + it.subtitle).contains(q)) }
    val grouped = items.groupBy { it.date }

    ScreenScaffold("Movimentações") { pad ->
        LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            item {
                OutlinedTextField(query, { query = it }, label = { Text("Buscar") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large)
            }
            item { ChoiceChips(null, listOf("Tudo", "Entradas", "Despesas", "Transferências").map { it to it }, filter, { filter = it }, Modifier.padding(vertical = 6.dp)) }
            if (items.isEmpty()) item { EmptyState("Nada por aqui", "Registre uma entrada ou despesa na tela Início.") }
            grouped.forEach { (date, list) ->
                item(key = "h$date") {
                    val net = list.filter { it.tone != MoveTone.NEUTRAL }.sumOf { it.amountCents }
                    androidx.compose.foundation.layout.Row(Modifier.padding(top = 14.dp, bottom = 4.dp)) {
                        Label(Dates.relative(date), Modifier.weight(1f))
                        Label(Money.format(net, showPlus = true))
                    }
                }
                items(list, key = { it.key }) { m ->
                    ListRow(
                        m.title, m.subtitle,
                        leading = { Dot(m.tone.color(fin)) },
                        trailing = { Text(Money.format(m.amountCents, showPlus = m.tone == MoveTone.IN), style = MaterialTheme.typography.titleSmall, color = if (m.tone == MoveTone.NEUTRAL) fin.muted else m.tone.color(fin)) },
                        onClick = {
                            if (m.route != null) s.go(m.route)
                            else removing = l.txns.firstOrNull { "t${it.id}" == m.key }
                        },
                    )
                }
            }
        }
    }
    removing?.let { SimpleTxnDialog(s, it) { removing = null } }
}

