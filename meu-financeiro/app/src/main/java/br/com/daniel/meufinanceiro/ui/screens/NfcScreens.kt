package br.com.daniel.meufinanceiro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.daniel.meufinanceiro.data.BoxKind
import br.com.daniel.meufinanceiro.data.DistMode
import br.com.daniel.meufinanceiro.data.NfcBatch
import br.com.daniel.meufinanceiro.data.NfcCustomer
import br.com.daniel.meufinanceiro.data.NfcSale
import br.com.daniel.meufinanceiro.data.PayMethod
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.Keys
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.domain.Period
import br.com.daniel.meufinanceiro.domain.PeriodKind
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
import br.com.daniel.meufinanceiro.ui.components.ListRow
import br.com.daniel.meufinanceiro.ui.components.MoneyField
import br.com.daniel.meufinanceiro.ui.components.MoneyText
import br.com.daniel.meufinanceiro.ui.components.PrimaryButton
import br.com.daniel.meufinanceiro.ui.components.ScreenScaffold
import br.com.daniel.meufinanceiro.ui.components.ScrollColumn
import br.com.daniel.meufinanceiro.ui.components.SectionTitle
import br.com.daniel.meufinanceiro.ui.components.StatTile
import br.com.daniel.meufinanceiro.ui.theme.LocalFin

@Composable
fun NfcScreen(s: AppState) {
    val l = s.ledger
    val c = s.calc
    val fin = LocalFin.current
    var period by remember { mutableStateOf(Period.of(PeriodKind.MONTH)) }
    val sum = remember(l, period) { c.nfcSummary(period) }
    val all = remember(l) { c.nfcSummary(null) }
    val stockBox = l.boxOfKind(BoxKind.NFC_STOCK)
    var editCustomer by remember { mutableStateOf<NfcCustomer?>(null) }
    var deleteBatch by remember { mutableStateOf<NfcBatch?>(null) }

    ScreenScaffold("Plaquinhas NFC", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            FinCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Label("Estoque disponível")
                        Text("${c.nfcStock()} unidades", style = MaterialTheme.typography.headlineMedium)
                        Label("Custo médio ${Money.format(c.nfcAvgUnitCost())}/un.")
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Label("Caixinha Estoque NFC")
                        MoneyText(stockBox?.let { c.boxBalance(it.id) } ?: 0)
                        Label("para repor estoque")
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryButton("Nova venda", onClick = { s.go(R.sale()) }, modifier = Modifier.weight(1f))
                PrimaryButton("Novo lote", onClick = { s.go(R.BATCH) }, modifier = Modifier.weight(1f))
            }
            PeriodSelector(period) { period = it }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("Faturamento", sum.revenueCents, Modifier.weight(1f), sub = "${sum.unitsSold} un. vendidas")
                StatTile("Custo das plaquinhas", sum.costCents, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("Lucro bruto", sum.grossProfitCents, Modifier.weight(1f), fin.income, sub = "faturamento − custo")
                StatTile("Lucro líquido", sum.netProfitCents, Modifier.weight(1f), fin.income, sub = "− outros custos ${Money.format(sum.extraCostsCents)}")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("Recebido", sum.receivedCents, Modifier.weight(1f))
                StatTile("A receber", sum.pendingCents, Modifier.weight(1f), if (sum.pendingCents > 0) fin.warn else androidx.compose.ui.graphics.Color.Unspecified) { s.go(R.RECEIVE) }
            }
            StatTile("Reinvestido em lotes no período", sum.stockSpentCents, sub = "Desde o início: faturou ${Money.format(all.revenueCents)}, lucro líquido ${Money.format(all.netProfitCents)}")
            Label("Faturamento não é dinheiro pessoal: quando uma venda é paga, o custo da plaquinha volta para a caixinha Estoque NFC e só o lucro entra na divisão 50/25/25.")

            SectionTitle("Vendas")
            if (l.sales.isEmpty()) EmptyState("Nenhuma venda ainda", "Registre cada venda: cliente, quantidade, preço e se já recebeu.")
            else FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                l.sales.take(30).forEachIndexed { i, sale ->
                    val pending = c.saleRevenue(sale) - c.saleReceived(sale.id)
                    val cust = l.customer(sale.customerId)
                    if (i > 0) Divider()
                    ListRow("${sale.quantity} un. · " + (cust?.let { it.business.ifBlank { it.name } } ?: "Sem cliente"),
                        Dates.relative(sale.date) + " · ${Money.format(sale.unitPriceCents)}/un." + if (pending > 0) " · falta ${Money.format(pending)}" else " · pago",
                        leading = { Dot(if (pending > 0) fin.warn else fin.income) }, trailing = { MoneyText(c.saleRevenue(sale), style = MaterialTheme.typography.titleSmall) },
                        onClick = { s.go(R.sale(sale.id)) })
                }
            }

            SectionTitle("Lotes comprados")
            if (l.batches.isEmpty()) EmptyState("Nenhum lote cadastrado", "Cadastre o estoque que você tem hoje como um lote (ex.: 10 un. por R$ 140) para o app calcular o custo e o lucro.")
            else FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                l.batches.forEachIndexed { i, b ->
                    if (i > 0) Divider()
                    ListRow("${b.quantity} un. · ${Money.format(if (b.quantity > 0) b.totalCostCents / b.quantity else 0)}/un.", Dates.relative(b.date) + (b.supplier.takeIf { it.isNotBlank() }?.let { " · $it" } ?: ""),
                        trailing = { MoneyText(b.totalCostCents, style = MaterialTheme.typography.titleSmall) }, onClick = { deleteBatch = b })
                }
            }

            SectionTitle("Clientes", action = "Novo", onAction = { editCustomer = NfcCustomer(name = "") })
            if (l.customers.isEmpty()) Label("Os clientes cadastrados nas vendas aparecem aqui, com o histórico de compras.")
            else FinCard(padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                l.customers.forEachIndexed { i, cu ->
                    val sales = l.sales.filter { it.customerId == cu.id }
                    if (i > 0) Divider()
                    ListRow(cu.business.ifBlank { cu.name }, listOfNotNull(cu.name.takeIf { cu.business.isNotBlank() }, cu.phone.takeIf { it.isNotBlank() }, "${sales.sumOf { it.quantity }} un. compradas").joinToString(" · "),
                        trailing = { MoneyText(sales.sumOf { c.saleRevenue(it) }, style = MaterialTheme.typography.titleSmall) }, onClick = { editCustomer = cu })
                }
            }
        }
    }

    editCustomer?.let { cu -> CustomerDialog(s, cu) { editCustomer = null } }
    deleteBatch?.let { b ->
        ConfirmDialog("Excluir lote?", "${b.quantity} un. por ${Money.format(b.totalCostCents)}. O pagamento do lote (se lançado) também será excluído.", "Excluir", danger = true,
            onConfirm = { s.vm.run("Lote excluído") { deleteBatch(b.id) } }, onDismiss = { deleteBatch = null })
    }
}

@Composable
fun CustomerDialog(s: AppState, cu: NfcCustomer, onSaved: (Long) -> Unit = {}, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(cu.name) }
    var business by remember { mutableStateOf(cu.business) }
    var phone by remember { mutableStateOf(cu.phone) }
    var notes by remember { mutableStateOf(cu.notes) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (cu.id == 0L) "Novo cliente" else "Cliente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(business, { business = it }, "Estabelecimento")
                AppTextField(name, { name = it }, "Nome do contato")
                AppTextField(phone, { phone = it }, "Telefone / WhatsApp", keyboardType = KeyboardType.Phone)
                AppTextField(notes, { notes = it }, "Observações", singleLine = false)
            }
        },
        confirmButton = {
            TextButton(enabled = name.isNotBlank() || business.isNotBlank(), onClick = {
                s.vm.run("Cliente salvo") { val id = saveCustomer(cu.copy(name = name.trim().ifBlank { business.trim() }, business = business.trim(), phone = phone.trim(), notes = notes)); onSaved(id) }
                onDismiss()
            }) { Text("Salvar") }
        },
        dismissButton = {
            Row {
                if (cu.id != 0L) TextButton(onClick = { s.vm.run("Cliente excluído") { deleteCustomer(cu.id) }; onDismiss() }) { Text("Excluir", color = MaterialTheme.colorScheme.error) }
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        },
    )
}

@Composable
fun NfcSaleForm(s: AppState, saleId: Long) {
    val l = s.ledger
    val c = s.calc
    val fin = LocalFin.current
    val old = remember { l.sale(saleId.takeIf { it != 0L }) }
    val editing = old != null
    val price = l.settingLong(Keys.NFC_PRICE, 8990)
    val promo = l.settingLong(Keys.NFC_PROMO_PRICE, 7990)
    val promoMin = l.settingInt(Keys.NFC_PROMO_MIN_QTY, 2)
    var customer by remember { mutableStateOf(old?.customerId) }
    var qty by remember { mutableStateOf(old?.quantity ?: 1) }
    var unit by remember { mutableStateOf(Money.toInput(old?.unitPriceCents ?: price)) }
    var priceTouched by remember { mutableStateOf(editing) }
    var unitCost by remember { mutableStateOf(Money.toInput(old?.unitCostCents ?: c.nfcAvgUnitCost())) }
    var extra by remember { mutableStateOf(old?.extraCostsCents?.takeIf { it > 0 }?.let { Money.toInput(it) } ?: "") }
    var date by remember { mutableStateOf(old?.date ?: c.today) }
    var received by remember { mutableStateOf(if (editing) "none" else "all") }
    var partial by remember { mutableStateOf("") }
    var account by remember { mutableStateOf(l.dailyAccount?.id) }
    var mode by remember { mutableStateOf(DistMode.DEFAULT) }
    val custom = remember { mutableStateMapOf<Long, String>() }
    var notes by remember { mutableStateOf(old?.notes ?: "") }
    var newCustomer by remember { mutableStateOf(false) }
    var askDelete by remember { mutableStateOf(false) }

    val unitV = Money.parse(unit) ?: 0
    val costV = Money.parse(unitCost) ?: 0
    val extraV = Money.parse(extra) ?: 0
    val revenue = unitV * qty
    val gross = revenue - costV * qty
    val receivedNow = when (received) { "all" -> revenue; "part" -> Money.parse(partial) ?: 0; else -> 0 }
    val stockAfter = c.nfcStock() + (old?.quantity ?: 0) - qty

    ScreenScaffold(if (editing) "Editar venda NFC" else "Nova venda NFC", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Dropdown("Cliente", listOf(0L to "Sem cliente") + l.customers.map { it.id to it.business.ifBlank { it.name } }, customer ?: 0L, { customer = it.takeIf { v -> v != 0L } }, Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { newCustomer = true }) { Text("+ Novo") }
            }
            ChoiceChips("Quantidade", (1..10).map { it to "$it" }, qty, {
                qty = it
                if (!priceTouched) unit = Money.toInput(if (it >= promoMin) promo else price)
            })
            MoneyField(unit, { unit = it; priceTouched = true }, "Preço por unidade", supporting = "Padrão ${Money.format(price)} · a partir de $promoMin un.: ${Money.format(promo)}")
            MoneyField(unitCost, { unitCost = it }, "Custo por unidade", supporting = "Custo médio dos seus lotes")
            MoneyField(extra, { extra = it }, "Outros custos da venda (opcional)", supporting = "Frete, embalagem, impressão...")
            DateField(date, { date = it })
            FinCard {
                Row { Text("Faturamento", Modifier.weight(1f)); Text(Money.format(revenue), style = MaterialTheme.typography.titleSmall) }
                Row { Text("Lucro bruto", Modifier.weight(1f)); Text(Money.format(gross), style = MaterialTheme.typography.titleSmall, color = fin.income) }
                Row { Text("Lucro líquido", Modifier.weight(1f)); Text(Money.format(gross - extraV), style = MaterialTheme.typography.titleSmall, color = fin.income) }
                Spacer(Modifier.height(4.dp))
                Label("Ao receber, ${Money.format(costV * qty)} voltam para a caixinha Estoque NFC.")
            }
            if (stockAfter < 0) Banner("O estoque registrado ficaria negativo ($stockAfter). Cadastre o lote que você tem em \"Novo lote\".", fin.warn)
            if (!editing) {
                ChoiceChips("Já recebeu?", listOf("all" to "Sim, tudo", "part" to "Uma parte", "none" to "Ainda não"), received, { received = it })
                if (received == "part") MoneyField(partial, { partial = it }, "Quanto já recebeu")
                if (received != "none") {
                    Dropdown("Conta que recebeu", accountOptions(l), account, { account = it })
                    SplitEditor(s, (receivedNow - (if (revenue > 0) receivedNow * costV * qty / revenue else 0)), mode, { mode = it }, custom)
                }
            } else Label("Recebido: ${Money.format(c.saleReceived(old!!.id))}. Novos pagamentos: \"Registrar pagamento\".")
            AppTextField(notes, { notes = it }, "Observações", singleLine = false)
            PrimaryButton("Salvar venda", enabled = unitV > 0 && qty > 0, onClick = {
                val sale = NfcSale(id = old?.id ?: 0, date = date, customerId = customer, quantity = qty, unitPriceCents = unitV, unitCostCents = costV,
                    extraCostsCents = extraV, notes = notes, createdAt = old?.createdAt ?: System.currentTimeMillis())
                s.vm.run(if (editing) "Venda atualizada" else "Venda registrada", onDone = s::back) { saveSale(sale, if (editing) 0 else receivedNow, account, buildSplit(mode, custom)) }
            })
            if (editing) OutlinedButton(onClick = { askDelete = true }) { Text("Excluir venda", color = MaterialTheme.colorScheme.error) }
        }
    }
    if (newCustomer) CustomerDialog(s, NfcCustomer(name = ""), onSaved = { customer = it }) { newCustomer = false }
    if (askDelete) ConfirmDialog("Excluir venda?", "Os pagamentos desta venda também serão excluídos.", "Excluir", danger = true,
        onConfirm = { s.vm.run("Venda excluída", onDone = s::back) { deleteSale(old!!.id) } }, onDismiss = { askDelete = false })
}

@Composable
fun NfcBatchForm(s: AppState) {
    val l = s.ledger
    val fin = LocalFin.current
    var qty by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(s.calc.today) }
    var supplier by remember { mutableStateOf("") }
    var pay by remember { mutableStateOf("now") }
    var method by remember { mutableStateOf(PayMethod.PIX) }
    var account by remember { mutableStateOf(l.accounts.firstOrNull { it.name.contains("Nubank") }?.id ?: l.dailyAccount?.id) }
    var installments by remember { mutableStateOf(1) }
    val q = qty.toIntOrNull() ?: 0
    val t = Money.parse(total)
    val stockBox = l.boxOfKind(BoxKind.NFC_STOCK)
    val boxBal = stockBox?.let { s.calc.boxBalance(it.id) } ?: 0

    ScreenScaffold("Novo lote NFC", onBack = s::back) { pad ->
        ScrollColumn(pad) {
            AppTextField(qty, { qty = it.filter(Char::isDigit).take(5) }, "Quantidade de plaquinhas", keyboardType = KeyboardType.Number, autoFocus = true)
            MoneyField(total, { total = it }, "Custo total do lote", supporting = "Referência: lote de ${Money.format(l.settingLong(Keys.NFC_REF_BATCH_COST, 35000))}")
            if (q > 0 && t != null) Label("Custo por unidade: ${Money.format(t / q)}")
            DateField(date, { date = it })
            AppTextField(supplier, { supplier = it }, "Fornecedor (opcional)")
            ChoiceChips("Pagamento", listOf("now" to "Lançar o pagamento", "skip" to "Só registrar o estoque"), pay, { pay = it })
            if (pay == "now") {
                ChoiceChips("Forma", methodOptions, method, { method = it })
                if (method == PayMethod.CREDIT) ChoiceChips("Parcelas", listOf(1, 2, 3, 4, 5, 6).map { it to if (it == 1) "À vista" else "${it}x" }, installments, { installments = it })
                else Dropdown("Conta", accountOptions(l), account, { account = it })
                Label("O pagamento sai da caixinha Estoque NFC (hoje com ${Money.format(boxBal)}), não do seu dinheiro pessoal.")
                if (t != null && t > boxBal) Banner("A caixinha Estoque NFC não cobre este lote: faltam ${Money.format(t - boxBal)}. Ela vai ficar negativa até as próximas vendas.", fin.warn)
            } else Label("Use para cadastrar o estoque que você já tinha (ex.: o primeiro lote de 10 un. por R$ 140), sem lançar uma nova despesa.")
            PrimaryButton("Salvar lote", enabled = q > 0 && t != null, onClick = {
                s.vm.run("Lote registrado", onDone = s::back) {
                    saveBatch(NfcBatch(date = date, quantity = q, totalCostCents = t!!, supplier = supplier.trim()),
                        if (pay == "now") method else null, if (method == PayMethod.CREDIT) null else account, l.mainCard?.id, installments)
                }
            })
        }
    }
}
