package br.com.daniel.meufinanceiro.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.daniel.meufinanceiro.BuildConfig
import br.com.daniel.meufinanceiro.data.Account
import br.com.daniel.meufinanceiro.data.AccountKind
import br.com.daniel.meufinanceiro.data.Category
import br.com.daniel.meufinanceiro.data.CycleRule
import br.com.daniel.meufinanceiro.data.DistributionRule
import br.com.daniel.meufinanceiro.data.IncomeSource
import br.com.daniel.meufinanceiro.data.SourceType
import br.com.daniel.meufinanceiro.domain.CardBilling
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.Keys
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.security.Pin
import br.com.daniel.meufinanceiro.ui.AppState
import br.com.daniel.meufinanceiro.ui.R
import br.com.daniel.meufinanceiro.ui.components.AppTextField
import br.com.daniel.meufinanceiro.ui.components.Banner
import br.com.daniel.meufinanceiro.ui.components.ChoiceChips
import br.com.daniel.meufinanceiro.ui.components.ConfirmDialog
import br.com.daniel.meufinanceiro.ui.components.Divider
import br.com.daniel.meufinanceiro.ui.components.Dot
import br.com.daniel.meufinanceiro.ui.components.Dropdown
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
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(s: AppState) {
    val l = s.ledger
    ScreenScaffold("Configurações", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            SectionTitle("Você")
            FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                ListRow("Nome", l.userName, onClick = { s.go(R.settings("name")) })
            }
            SectionTitle("Aparência")
            ChoiceChips("Tema", listOf("system" to "Igual ao celular", "light" to "Claro", "dark" to "Escuro"), l.setting(Keys.THEME) ?: "system", { s.vm.setSetting(Keys.THEME, it) })
            SectionTitle("Dinheiro")
            FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                ListRow("Contas e saldos", "Nubank, Inter, dinheiro · saldo mínimo ${Money.format(s.calc.minBalance())}", onClick = { s.go(R.settings("accounts")) }); Divider()
                ListRow("Cartão de crédito", l.mainCard?.let { "${it.name} · fecha dia ${it.closingDay} · vence ${it.dueDay ?: "a configurar"}" }, onClick = { s.go(R.settings("card")) }); Divider()
                ListRow("Divisão automática", l.rules.sortedBy { l.box(it.boxId)?.sortOrder ?: 0 }.joinToString(" / ") { "${l.box(it.boxId)?.name} ${Money.formatPercent(it.basisPoints)}" }, onClick = { s.go(R.settings("split")) }); Divider()
                ListRow("Categorias de despesa", "${l.categories.count { !it.archived }} categorias", onClick = { s.go(R.settings("categories")) }); Divider()
                ListRow("Fontes de renda", "${l.sources.count { !it.archived }} fontes", onClick = { s.go(R.settings("sources")) }); Divider()
                ListRow("Plaquinhas NFC", "Preços e custos de referência", onClick = { s.go(R.settings("nfc")) }); Divider()
                ListRow("Contas a pagar", "Ativar, valores e vencimentos", onClick = { s.go(R.BILLS) })
            }
            SectionTitle("Proteção")
            FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                ListRow("PIN e biometria", if (l.setting(Keys.PIN_HASH) != null) "Ativado" else "Desativado", onClick = { s.go(R.settings("security")) }); Divider()
                ListRow("Backup e exportação", "Copiar, restaurar, CSV", onClick = { s.go(R.settings("backup")) }); Divider()
                ListRow("Assistente e inteligência artificial", if (l.settingBool(Keys.AI_ENABLED)) "Claude ativado" else "Modo offline (sem internet)", onClick = { s.go(R.settings("ai")) }); Divider()
                ListRow("Alertas", "Quais avisos receber e notificações", onClick = { s.go(R.ALERTS) })
            }
            SectionTitle("Sobre")
            Label("Meu Financeiro ${BuildConfig.VERSION_NAME} · app privado de ${l.userName} · dados salvos só neste celular (banco local). Identificador: ${BuildConfig.APPLICATION_ID}")
        }
    }
}

@Composable
fun SettingsSection(s: AppState, section: String) {
    when (section) {
        "accounts" -> AccountsSettings(s)
        "card" -> CardSettings(s)
        "split" -> SplitSettings(s)
        "categories" -> CategoriesSettings(s)
        "sources" -> SourcesSettings(s)
        "nfc" -> NfcSettings(s)
        "security" -> SecuritySettings(s)
        "backup" -> BackupSettings(s)
        "ai" -> AiSettings(s)
        "name" -> NameSettings(s)
        else -> SettingsScreen(s)
    }
}

/* ---------- Accounts ---------- */

@Composable
private fun AccountsSettings(s: AppState) {
    val l = s.ledger
    val c = s.calc
    var edit by remember { mutableStateOf<Account?>(null) }
    var adjust by remember { mutableStateOf<Account?>(null) }
    var minDialog by remember { mutableStateOf(false) }
    ScreenScaffold("Contas e saldos", onBack = s::back, actions = { TextButton(onClick = { edit = Account(name = "", sortOrder = l.accounts.size) }) { Text("Nova") } }) { pad ->
        ScrollColumn(pad) {
            Label("Os saldos não são sincronizados com o banco. O saldo de cada conta é o TOTAL no banco, incluindo o dinheiro das caixinhas guardadas nele. Se algo divergir, use \"Atualizar saldo\".")
            l.accounts.filter { !it.archived }.forEach { a ->
                FinCard {
                    Row {
                        Column(Modifier.weight(1f)) {
                            Text(a.name, style = MaterialTheme.typography.titleMedium)
                            Label(listOfNotNull(if (a.kind == AccountKind.CASH) "Dinheiro físico" else "Conta bancária", "Dia a dia".takeIf { a.isDaily }, a.note.takeIf { it.isNotBlank() }).joinToString(" · "))
                            val inBoxes = c.boxesInAccount(a.id)
                            if (inBoxes > 0) Label("Em caixinhas: ${Money.format(inBoxes)} · fora delas: ${Money.format(c.accountOutsideBoxes(a.id))}",
                                color = if (c.accountOutsideBoxes(a.id) < 0) LocalFin.current.expense else LocalFin.current.muted)
                        }
                        MoneyText(c.accountBalance(a.id))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { adjust = a }) { Text("Atualizar saldo") }
                        TextButton(onClick = { edit = a }) { Text("Editar") }
                    }
                }
            }
            SectionTitle("Saldo mínimo")
            FinCard(onClick = { minDialog = true }) {
                Text("Manter pelo menos ${Money.format(c.minBalance())} livres", style = MaterialTheme.typography.titleMedium)
                Label("O app avisa quando uma movimentação deixaria o seu saldo livre (sem contar caixinhas) abaixo disso.")
            }
        }
    }
    edit?.let { a -> AccountDialog(s, a) { edit = null } }
    adjust?.let { a ->
        val cur = c.accountBalance(a.id)
        val inBoxes = c.boxesInAccount(a.id)
        var outsideMode by remember(a.id) { mutableStateOf(inBoxes > 0) }
        androidx.compose.runtime.key(outsideMode) { MoneyDialog("Atualizar saldo de ${a.name}", if (outsideMode) "Saldo em conta (fora das caixinhas)" else "Total no banco (com caixinhas)",
            if (outsideMode) c.accountOutsideBoxes(a.id) else cur,
            "Hoje o app calcula ${Money.format(cur)} no total" + (if (inBoxes > 0) " (${Money.format(inBoxes)} em caixinhas deste banco)" else "") +
                ". A diferença vira um ajuste: não é receita nem despesa.", "Atualizar",
            onConfirm = { v -> val newTotal = if (outsideMode) v + inBoxes else v; s.vm.run("Saldo atualizado") { adjustBalance(a.id, cur, newTotal, c.today) } }, onDismiss = { adjust = null }) {
            if (inBoxes > 0) ChoiceChips(null, listOf(true to "Informar saldo em conta", false to "Informar total com caixinhas"), outsideMode, { outsideMode = it })
        } }
    }
    if (minDialog) MoneyDialog("Saldo mínimo", "Valor", c.minBalance(), null, "Salvar",
        onConfirm = { v -> s.vm.setSetting(Keys.MIN_BALANCE, v.toString()) }, onDismiss = { minDialog = false })
}

@Composable
private fun AccountDialog(s: AppState, a: Account, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(a.name) }
    var initial by remember { mutableStateOf(Money.toInput(a.initialBalanceCents)) }
    var daily by remember { mutableStateOf(a.isDaily) }
    var cash by remember { mutableStateOf(a.kind == AccountKind.CASH) }
    var note by remember { mutableStateOf(a.note) }
    var archive by remember { mutableStateOf(a.archived) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (a.id == 0L) "Nova conta" else "Editar conta") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(name, { name = it }, "Nome")
                MoneyField(initial, { initial = it }, "Saldo inicial", supporting = "Quanto tinha quando começou a usar o app")
                SwitchRow("Conta do dia a dia", daily, { daily = it }, "Recebe o alerta de saldo mínimo")
                SwitchRow("Dinheiro físico (carteira)", cash, { cash = it })
                AppTextField(note, { note = it }, "Anotação")
                if (a.id != 0L) SwitchRow("Arquivar", archive, { archive = it }, "Some das listas; o histórico continua")
            }
        },
        confirmButton = {
            TextButton(enabled = name.isNotBlank() && Money.parse(initial) != null, onClick = {
                s.vm.run("Conta salva") { saveAccount(a.copy(name = name.trim(), initialBalanceCents = Money.parse(initial) ?: 0, isDaily = daily, kind = if (cash) AccountKind.CASH else AccountKind.BANK, note = note, archived = archive)) }
                onDismiss()
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

/* ---------- Card ---------- */

@Composable
private fun CardSettings(s: AppState) {
    val l = s.ledger
    val card = l.mainCard ?: br.com.daniel.meufinanceiro.data.CreditCard(name = "Cartão Inter", bank = "Inter", closingDay = 20, bestPurchaseDay = 14)
    var name by remember { mutableStateOf(card.name) }
    var bank by remember { mutableStateOf(card.bank) }
    var closing by remember { mutableStateOf(card.closingDay.toString()) }
    var best by remember { mutableStateOf(card.bestPurchaseDay.toString()) }
    var due by remember { mutableStateOf(card.dueDay?.toString() ?: "") }
    var rule by remember { mutableStateOf(card.cycleRule) }
    var limit by remember { mutableStateOf(card.limitCents?.let { Money.toInput(it) } ?: "") }
    var account by remember { mutableStateOf(card.paymentAccountId ?: l.dailyAccount?.id) }
    val preview = card.copy(closingDay = closing.toIntOrNull() ?: card.closingDay, bestPurchaseDay = best.toIntOrNull() ?: card.bestPurchaseDay, cycleRule = rule)
    ScreenScaffold("Cartão de crédito", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            AppTextField(name, { name = it }, "Nome do cartão")
            AppTextField(bank, { bank = it }, "Banco")
            AppTextField(best, { best = it.filter(Char::isDigit).take(2) }, "Melhor dia de compra", keyboardType = KeyboardType.Number)
            AppTextField(closing, { closing = it.filter(Char::isDigit).take(2) }, "Dia de fechamento", keyboardType = KeyboardType.Number)
            AppTextField(due, { due = it.filter(Char::isDigit).take(2) }, "Dia de vencimento", keyboardType = KeyboardType.Number, supporting = "Confira no app do Inter")
            ChoiceChips("Qual dia separa uma fatura da outra?", listOf(CycleRule.BEST_DAY to "Melhor dia de compra", CycleRule.CLOSING to "Dia de fechamento"), rule, { rule = it })
            Label(if (rule == CycleRule.BEST_DAY) "Compras a partir do dia ${preview.bestPurchaseDay} entram na fatura do mês seguinte." else "Compras depois do dia ${preview.closingDay} entram na fatura do mês seguinte.")
            CardBilling.configWarning(preview)?.let { Banner(it, LocalFin.current.info) }
            MoneyField(limit, { limit = it }, "Limite (opcional)")
            Dropdown("Conta que paga a fatura", accountOptions(l), account, { account = it })
            PrimaryButton("Salvar", enabled = (closing.toIntOrNull() ?: 0) in 1..31 && (best.toIntOrNull() ?: 0) in 1..31, onClick = {
                s.vm.run("Cartão salvo", onDone = s::back) {
                    saveCard(card.copy(name = name.trim(), bank = bank.trim(), closingDay = closing.toInt(), bestPurchaseDay = best.toInt(), dueDay = due.toIntOrNull()?.coerceIn(1, 31),
                        cycleRule = rule, limitCents = Money.parse(limit)?.takeIf { it > 0 }, paymentAccountId = account))
                }
            })
            Label("Mudar as datas não move as compras já lançadas: elas continuam na fatura em que foram registradas.")
        }
    }
}

/* ---------- Split ---------- */

@Composable
private fun SplitSettings(s: AppState) {
    val l = s.ledger
    val boxes = l.boxes.filter { !it.archived }.sortedBy { it.sortOrder }
    val values = remember { mutableStateMapOf<Long, String>().apply { boxes.forEach { b -> put(b.id, (l.rules.firstOrNull { it.boxId == b.id }?.basisPoints ?: 0).let { bp -> if (bp % 100 == 0) (bp / 100).toString() else (bp / 100.0).toString().replace('.', ',') }) } } }
    fun bp(v: String?) = v?.replace(',', '.')?.toDoubleOrNull()?.let { Math.round(it * 100).toInt() } ?: 0
    val total = values.values.sumOf { bp(it) }
    ScreenScaffold("Divisão automática", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            Label("Toda nova entrada é dividida assim (a não ser que você escolha \"Não dividir\" ou personalize na hora). Vendas NFC: primeiro o custo volta para o Estoque NFC, depois o lucro é dividido.")
            boxes.forEach { b ->
                AppTextField(values[b.id] ?: "", { values[b.id] = it.filter { ch -> ch.isDigit() || ch == ',' }.take(5) }, "${b.name} (%)", keyboardType = KeyboardType.Decimal)
            }
            Text("Total: ${Money.formatPercent(total)}", style = MaterialTheme.typography.titleMedium, color = if (total == 10000) LocalFin.current.income else LocalFin.current.expense)
            PrimaryButton("Salvar percentuais", enabled = total == 10000, onClick = {
                s.vm.run("Percentuais salvos", onDone = s::back) { saveRules(boxes.map { DistributionRule(it.id, bp(values[it.id])) }) }
            })
            TextButton(onClick = {
                boxes.forEach { values[it.id] = "0" }
                l.boxOfKind(br.com.daniel.meufinanceiro.data.BoxKind.SPENDING)?.let { values[it.id] = "50" }
                l.boxOfKind(br.com.daniel.meufinanceiro.data.BoxKind.EMERGENCY)?.let { values[it.id] = "25" }
                l.boxOfKind(br.com.daniel.meufinanceiro.data.BoxKind.ABROAD)?.let { values[it.id] = "25" }
            }) { Text("Voltar para 50 / 25 / 25") }
        }
    }
}

/* ---------- Categories / sources ---------- */

@Composable
private fun CategoriesSettings(s: AppState) {
    val l = s.ledger
    var edit by remember { mutableStateOf<Category?>(null) }
    val palette = listOf(0xFF2A78D6, 0xFFEB6834, 0xFF1BAF7A, 0xFFEDA100, 0xFFE87BA4, 0xFF008300, 0xFF6250D6, 0xFFE34948)
    ScreenScaffold("Categorias", onBack = s::back, actions = { TextButton(onClick = { edit = Category(name = "", color = palette[l.categories.size % palette.size], sortOrder = l.categories.size) }) { Text("Nova") } }) { pad ->
        ScrollColumn(pad) {
            FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                l.categories.forEachIndexed { i, cat ->
                    if (i > 0) Divider()
                    ListRow(cat.name, listOfNotNull("Sai da caixinha Estoque NFC".takeIf { cat.isNfcStock }, "arquivada".takeIf { cat.archived }).joinToString(" · "),
                        leading = { Dot(Color(cat.color)) }, onClick = { edit = cat })
                }
            }
        }
    }
    edit?.let { cat ->
        var name by remember(cat) { mutableStateOf(cat.name) }
        var nfc by remember(cat) { mutableStateOf(cat.isNfcStock) }
        var arch by remember(cat) { mutableStateOf(cat.archived) }
        AlertDialog(onDismissRequest = { edit = null }, title = { Text(if (cat.id == 0L) "Nova categoria" else "Editar categoria") },
            text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(name, { name = it }, "Nome")
                SwitchRow("É estoque NFC", nfc, { nfc = it }, "Gastos desta categoria saem da caixinha Estoque NFC")
                if (cat.id != 0L) SwitchRow("Arquivar", arch, { arch = it })
            } },
            confirmButton = { TextButton(enabled = name.isNotBlank(), onClick = { s.vm.run("Categoria salva") { saveCategory(cat.copy(name = name.trim(), isNfcStock = nfc, archived = arch)) }; edit = null }) { Text("Salvar") } },
            dismissButton = { TextButton(onClick = { edit = null }) { Text("Cancelar") } })
    }
}

@Composable
private fun SourcesSettings(s: AppState) {
    val l = s.ledger
    var edit by remember { mutableStateOf<IncomeSource?>(null) }
    ScreenScaffold("Fontes de renda", onBack = s::back, actions = { TextButton(onClick = { edit = IncomeSource(name = "", color = 0xFF6250D6, sortOrder = l.sources.size) }) { Text("Nova") } }) { pad ->
        ScrollColumn(pad) {
            Label("Crie fontes novas a qualquer momento (trabalhos temporários, serviços digitais, vendas, bicos...). Não precisa mudar nada no código.")
            FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                l.sources.forEachIndexed { i, src ->
                    if (i > 0) Divider()
                    ListRow(src.name, listOfNotNull(src.defaultAmountCents.takeIf { it > 0 }?.let { "padrão ${Money.format(it)}" }, src.defaultHours.takeIf { it > 0 }?.let { fmtHours(it) }, "arquivada".takeIf { src.archived }).joinToString(" · "),
                        leading = { Dot(Color(src.color)) }, onClick = { edit = src })
                }
            }
        }
    }
    edit?.let { src ->
        var name by remember(src) { mutableStateOf(src.name) }
        var amount by remember(src) { mutableStateOf(if (src.defaultAmountCents > 0) Money.toInput(src.defaultAmountCents) else "") }
        var hours by remember(src) { mutableStateOf(if (src.defaultHours > 0) fmtHours(src.defaultHours).removeSuffix("h") else "") }
        var arch by remember(src) { mutableStateOf(src.archived) }
        AlertDialog(onDismissRequest = { edit = null }, title = { Text(if (src.id == 0L) "Nova fonte de renda" else "Editar fonte") },
            text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(name, { name = it }, "Nome")
                MoneyField(amount, { amount = it }, "Valor padrão por trabalho (opcional)", supporting = "Só sugestão: você sempre pode mudar")
                AppTextField(hours, { hours = it.filter { ch -> ch.isDigit() || ch == ',' } }, "Horas padrão (opcional)", keyboardType = KeyboardType.Decimal)
                if (src.id != 0L && src.type != SourceType.NFC) SwitchRow("Arquivar", arch, { arch = it })
            } },
            confirmButton = { TextButton(enabled = name.isNotBlank(), onClick = {
                s.vm.run("Fonte salva") { saveSource(src.copy(name = name.trim(), defaultAmountCents = Money.parse(amount) ?: 0, defaultHours = hours.replace(',', '.').toDoubleOrNull() ?: 0.0, archived = arch)) }
                edit = null
            }) { Text("Salvar") } },
            dismissButton = { TextButton(onClick = { edit = null }) { Text("Cancelar") } })
    }
}

/* ---------- NFC ---------- */

@Composable
private fun NfcSettings(s: AppState) {
    val l = s.ledger
    var price by remember { mutableStateOf(Money.toInput(l.settingLong(Keys.NFC_PRICE, 8990))) }
    var promo by remember { mutableStateOf(Money.toInput(l.settingLong(Keys.NFC_PROMO_PRICE, 7990))) }
    var promoMin by remember { mutableStateOf(l.settingInt(Keys.NFC_PROMO_MIN_QTY, 2).toString()) }
    var unitCost by remember { mutableStateOf(Money.toInput(l.settingLong(Keys.NFC_REF_UNIT_COST, 1167))) }
    var batch by remember { mutableStateOf(Money.toInput(l.settingLong(Keys.NFC_REF_BATCH_COST, 35000))) }
    ScreenScaffold("Plaquinhas NFC", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            Label("Valores de referência (não são fixos). O custo real vem dos lotes que você cadastrar.")
            MoneyField(price, { price = it }, "Preço de venda padrão")
            MoneyField(promo, { promo = it }, "Preço promocional")
            AppTextField(promoMin, { promoMin = it.filter(Char::isDigit).take(3) }, "Promoção a partir de quantas unidades", keyboardType = KeyboardType.Number)
            MoneyField(unitCost, { unitCost = it }, "Custo por unidade de referência", supporting = "Usado enquanto não houver lote cadastrado")
            MoneyField(batch, { batch = it }, "Valor de referência de um novo lote")
            PrimaryButton("Salvar", onClick = {
                s.vm.run("Valores salvos", onDone = s::back) {
                    Money.parse(price)?.let { setSetting(Keys.NFC_PRICE, it.toString()) }
                    Money.parse(promo)?.let { setSetting(Keys.NFC_PROMO_PRICE, it.toString()) }
                    promoMin.toIntOrNull()?.let { setSetting(Keys.NFC_PROMO_MIN_QTY, it.toString()) }
                    Money.parse(unitCost)?.let { setSetting(Keys.NFC_REF_UNIT_COST, it.toString()) }
                    Money.parse(batch)?.let { setSetting(Keys.NFC_REF_BATCH_COST, it.toString()) }
                }
            })
        }
    }
}

/* ---------- Security ---------- */

@Composable
private fun SecuritySettings(s: AppState) {
    val l = s.ledger
    val hasPin = l.setting(Keys.PIN_HASH) != null
    var pin1 by remember { mutableStateOf("") }
    var pin2 by remember { mutableStateOf("") }
    var askRemove by remember { mutableStateOf(false) }
    ScreenScaffold("PIN e biometria", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            if (!hasPin) {
                Label("Crie um PIN de 4 a 8 números. Ele é pedido ao abrir o app e depois de 1 minuto fora dele.")
                AppTextField(pin1, { pin1 = it.filter(Char::isDigit).take(8) }, "Novo PIN", keyboardType = KeyboardType.NumberPassword)
                AppTextField(pin2, { pin2 = it.filter(Char::isDigit).take(8) }, "Repita o PIN", keyboardType = KeyboardType.NumberPassword)
                PrimaryButton("Ativar PIN", enabled = Pin.isValidFormat(pin1) && pin1 == pin2, onClick = { s.vm.setPin(pin1); pin1 = ""; pin2 = "" })
                Label("Se esquecer o PIN, não há como recuperar sem apagar os dados do app. Mantenha um backup.")
            } else {
                SwitchRow("Desbloquear com biometria", l.settingBool(Keys.BIOMETRIC), { s.vm.setSetting(Keys.BIOMETRIC, if (it) "true" else null) },
                    if (s.canUseBiometric) "Digital ou rosto cadastrados no celular" else "Biometria não disponível neste celular")
                OutlinedButton(onClick = { askRemove = true }) { Text("Remover PIN") }
            }
            SwitchRow("Ocultar na tela de apps recentes", l.settingBool(Keys.HIDE_IN_RECENTS), { s.vm.setSetting(Keys.HIDE_IN_RECENTS, if (it) "true" else null) },
                "Também bloqueia capturas de tela do app")
        }
    }
    if (askRemove) ConfirmDialog("Remover PIN?", "O app vai abrir sem pedir senha.", "Remover", danger = true, onConfirm = { s.vm.setPin(null) }, onDismiss = { askRemove = false })
}

/* ---------- Backup ---------- */

@Composable
private fun BackupSettings(s: AppState) {
    val scope = rememberCoroutineScope()
    var pendingRestore by remember { mutableStateOf<String?>(null) }
    var snaps by remember { mutableStateOf(s.vm.backup.snapshots()) }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) scope.launch {
            runCatching { s.vm.backup.writeTo(uri, s.vm.backup.exportJson()) }
                .onSuccess { s.vm.toast("Backup salvo. Guarde uma cópia fora do celular (Drive, e-mail ou computador).") }
                .onFailure { s.vm.toast("Falha no backup: ${it.message}") }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) runCatching { s.vm.backup.readFrom(uri) }.onSuccess { text ->
            runCatching { s.vm.backup.parse(text) }.onSuccess { pendingRestore = text }.onFailure { s.vm.toast("Arquivo inválido: não é um backup do Meu Financeiro") }
        }.onFailure { s.vm.toast("Não consegui ler o arquivo") }
    }
    val csvLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri != null) scope.launch { runCatching { s.vm.backup.writeTo(uri, s.vm.backup.csv(s.ledger)) }.onSuccess { s.vm.toast("CSV exportado") }.onFailure { s.vm.toast("Falha: ${it.message}") } }
    }
    ScreenScaffold("Backup e exportação", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            Banner("Seus dados ficam só neste celular. Se desinstalar o app ou perder o celular, só o arquivo de backup salva seus dados. Faça backup toda semana e guarde fora do celular.", LocalFin.current.info)
            PrimaryButton("Fazer backup agora", onClick = { exportLauncher.launch("meu-financeiro-backup-${Dates.format(Dates.today()).replace('/', '-')}.json") })
            OutlinedButton(onClick = { importLauncher.launch(arrayOf("application/json", "application/octet-stream", "text/plain", "*/*")) }) { Text("Restaurar de um arquivo") }
            OutlinedButton(onClick = { csvLauncher.launch("meu-financeiro-${Dates.format(Dates.today()).replace('/', '-')}.csv") }) { Text("Exportar tudo em CSV (planilha)") }
            SectionTitle("Cópias automáticas")
            Label("O app guarda sozinho uma cópia por dia (as 10 mais recentes) dentro dele, e outra antes de qualquer restauração. Elas protegem contra erros, mas somem se o app for desinstalado.")
            OutlinedButton(onClick = { scope.launch { runCatching { s.vm.backup.autoSnapshot(force = true, tag = "manual") }; snaps = s.vm.backup.snapshots(); s.vm.toast("Cópia criada") } }) { Text("Criar cópia agora") }
            if (snaps.isNotEmpty()) FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                snaps.forEachIndexed { i, f ->
                    if (i > 0) Divider()
                    ListRow(f.nameWithoutExtension, "${f.length() / 1024} KB · toque para restaurar", onClick = { pendingRestore = f.readText() })
                }
            }
        }
    }
    pendingRestore?.let { text ->
        val info = runCatching { s.vm.backup.parse(text) }.getOrNull()
        ConfirmDialog("Restaurar backup?",
            "Todos os dados atuais serão substituídos pelos do backup" + (info?.let { " de ${Dates.format(java.time.Instant.ofEpochMilli(it.exportedAt).atZone(java.time.ZoneId.systemDefault()).toLocalDate().toEpochDay())} (${it.txns.size} lançamentos, ${it.purchases.size} compras no cartão)" } ?: "") +
                ". Antes disso o app guarda uma cópia do estado atual.", "Restaurar", danger = true,
            onConfirm = { scope.launch { runCatching { s.vm.backup.restore(text) }.onSuccess { s.vm.toast("Backup restaurado"); snaps = s.vm.backup.snapshots() }.onFailure { s.vm.toast("Falha ao restaurar: ${it.message}. Nada foi alterado.") } } },
            onDismiss = { pendingRestore = null })
    }
}

/* ---------- AI ---------- */

@Composable
private fun AiSettings(s: AppState) {
    val l = s.ledger
    var key by remember { mutableStateOf("") }
    val hasKey = remember(l) { s.vm.secrets.has("anthropic_key") }
    ScreenScaffold("Inteligência artificial", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            FinCard {
                Text("Analista local (sempre ligado)", style = MaterialTheme.typography.titleMedium)
                Label("Roda no celular, sem internet. Analisa gastos, cartão, metas, NFC e renda por hora, e dá sugestões na tela Analista.")
            }
            FinCard {
                Text("Claude na assistente (opcional, precisa de internet)", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Label("Sem a chave, a assistente funciona no modo offline (perguntas e pedidos comuns). Com a chave, você conversa livremente: o Claude consulta seus dados pelas ferramentas do app e pede confirmação antes de mudar qualquer coisa. Para responder, o app envia à Anthropic o que você escreve e os dados que o Claude consultar (movimentações, totais, caixinhas). O uso da API é cobrado na sua conta da Anthropic (console.anthropic.com).")
            }
            SwitchRow("Ativar o Claude na assistente", l.settingBool(Keys.AI_ENABLED), { s.vm.setSetting(Keys.AI_ENABLED, if (it) "true" else null) })
            SwitchRow("Registros simples sem confirmação", l.settingBool(Keys.ASSISTANT_AUTO_SIMPLE), { s.vm.setSetting(Keys.ASSISTANT_AUTO_SIMPLE, if (it) "true" else null) },
                "Só para registrar despesas, entradas e trabalhos com dados claros. Edições, exclusões, metas e limites SEMPRE pedem confirmação.")
            AppTextField(key, { key = it.trim() }, if (hasKey) "Chave salva (cole outra para trocar)" else "Chave da API (sk-ant-...)", keyboardType = KeyboardType.Password,
                supporting = "Fica criptografada no celular (Android Keystore). Não vai para backups.")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton("Salvar chave", enabled = key.startsWith("sk-"), modifier = Modifier.weight(1f), onClick = {
                    s.vm.secrets.put("anthropic_key", key); key = ""; s.vm.setSetting(Keys.AI_ENABLED, "true"); s.vm.toast("Chave salva")
                })
                if (hasKey) OutlinedButton(onClick = { s.vm.secrets.put("anthropic_key", null); s.vm.setSetting(Keys.AI_ENABLED, null); s.vm.toast("Chave removida") }, modifier = Modifier.weight(1f).height(54.dp)) { Text("Remover chave") }
            }
            Label("Modelo: ${br.com.daniel.meufinanceiro.ai.ClaudeAnalyst.MODEL}")
        }
    }
}

@Composable
private fun NameSettings(s: AppState) {
    var name by remember { mutableStateOf(s.ledger.userName) }
    ScreenScaffold("Seu nome", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            AppTextField(name, { name = it }, "Como o app deve te chamar", autoFocus = true)
            PrimaryButton("Salvar", enabled = name.isNotBlank(), onClick = { s.vm.run("Nome salvo", onDone = s::back) { setSetting(Keys.USER_NAME, name.trim()) } })
        }
    }
}
