package br.com.daniel.meufinanceiro.data

import br.com.daniel.meufinanceiro.domain.Keys

/** Initial data, created once on the first launch. Everything here is editable in the app. */
object Seed {

    suspend fun run(dao: FinanceDao) {
        if (dao.accountCount() > 0) return

        val nubank = dao.upsertAccount(Account(name = "Nubank", note = "Caixinhas e organização do dinheiro", color = 0xFF8A5CF6, sortOrder = 0))
        val inter = dao.upsertAccount(Account(name = "Banco Inter", note = "Movimentações do dia a dia e cartão", isDaily = true, color = 0xFFFF7A00, sortOrder = 1))
        dao.upsertAccount(Account(name = "Dinheiro (carteira)", kind = AccountKind.CASH, color = 0xFF3DDC97, sortOrder = 2))
        check(nubank > 0 && inter > 0)

        // Best day 14 and closing day 20 as informed by Daniel. Due day intentionally NOT set.
        dao.upsertCard(CreditCard(name = "Cartão Inter", bank = "Inter", closingDay = 20, bestPurchaseDay = 14, dueDay = null, paymentAccountId = inter))

        val catColors = listOf(
            "Alimentação" to 0xFFF59E0B, "Transporte" to 0xFF3B82F6, "Academia" to 0xFF10B981, "Muay Thai" to 0xFFEF4444,
            "Assinaturas" to 0xFF8B5CF6, "Compras pessoais" to 0xFFEC4899, "Trabalho" to 0xFF64748B, "Estoque NFC" to 0xFF06B6D4,
            "Saúde" to 0xFF22C55E, "Lazer" to 0xFFF97316, "Família" to 0xFFA855F7, "Educação" to 0xFF0EA5E9, "Outros" to 0xFF94A3B8,
        )
        val catIds = mutableMapOf<String, Long>()
        catColors.forEachIndexed { i, (name, color) ->
            catIds[name] = dao.upsertCategory(Category(name = name, color = color, isNfcStock = name == "Estoque NFC", sortOrder = i))
        }

        dao.upsertSource(IncomeSource(name = "Plaquinhas NFC", type = SourceType.NFC, defaultAmountCents = 8990, color = 0xFF06B6D4, sortOrder = 0))
        dao.upsertSource(IncomeSource(name = "Tio Rex Restaurante", type = SourceType.TIO_REX, defaultAmountCents = 8000, defaultHours = 4.0, color = 0xFF22C55E, sortOrder = 1))
        dao.upsertSource(IncomeSource(name = "Recreação (festas e eventos)", type = SourceType.RECREACAO, defaultAmountCents = 12000, defaultHours = 4.0, color = 0xFFF59E0B, sortOrder = 2))
        dao.upsertSource(IncomeSource(name = "Pipoca e algodão-doce", type = SourceType.PIPOCA, defaultAmountCents = 0, color = 0xFFEC4899, sortOrder = 3))
        dao.upsertSource(IncomeSource(name = "Outras rendas", type = SourceType.OTHER, color = 0xFF94A3B8, sortOrder = 4))

        val gastos = dao.upsertBox(Box(name = "Gastos pessoais", kind = BoxKind.SPENDING, color = 0xFF3DDC97, sortOrder = 0,
            note = "Dinheiro livre para o dia a dia. Despesas saem daqui."))
        val reserva = dao.upsertBox(Box(name = "Reserva de emergência", kind = BoxKind.EMERGENCY, emergencyMonths = 6, color = 0xFF3B82F6, sortOrder = 1,
            note = "Meta calculada pelos seus gastos médios quando houver dados (ou defina um valor)."))
        val sair = dao.upsertBox(Box(name = "Sair do Brasil", kind = BoxKind.ABROAD, color = 0xFF8B5CF6, sortOrder = 2,
            note = "Cidadania italiana · horizonte de 3 a 5 anos. Defina a meta quando tiver o valor."))
        dao.upsertBox(Box(name = "Estoque NFC", kind = BoxKind.NFC_STOCK, color = 0xFF06B6D4, sortOrder = 3,
            note = "Dinheiro do negócio para repor estoque. Não é lucro pessoal."))
        dao.upsertBox(Box(name = "Computador", kind = BoxKind.COMPUTER, goalCents = 400_000, goalMaxCents = 600_000, color = 0xFFF59E0B, sortOrder = 4,
            note = "Notebook/PC para Premiere, After Effects, DaVinci, Blender, jogos e Meta Quest. Orçamento de R$ 4.000 a R$ 6.000."))
        dao.upsertBox(Box(name = "Outras metas", kind = BoxKind.OTHER, color = 0xFF94A3B8, sortOrder = 5))

        dao.upsertRule(DistributionRule(gastos, 5000))
        dao.upsertRule(DistributionRule(reserva, 2500))
        dao.upsertRule(DistributionRule(sair, 2500))

        // Commitments: all start INACTIVE and without due date, so nothing is assumed as due or paid.
        val bills = listOf(
            Bill(name = "Ajuda em casa (pai)", amountCents = 30000, categoryId = catIds["Família"], sortOrder = 0),
            Bill(name = "Academia", amountCents = 16000, categoryId = catIds["Academia"], sortOrder = 1),
            Bill(name = "Spotify", amountCents = 1300, categoryId = catIds["Assinaturas"], sortOrder = 2),
            Bill(name = "Muay Thai", amountCents = 13000, categoryId = catIds["Muay Thai"], notes = "Valor aproximado", sortOrder = 3),
            Bill(name = "Claude AI", amountCents = 0, variable = true, categoryId = catIds["Assinaturas"], notes = "Valor a configurar", sortOrder = 4),
            Bill(name = "Estoque NFC", amountCents = 0, variable = true, categoryId = catIds["Estoque NFC"], notes = "Investimento variável", sortOrder = 5),
        )
        bills.forEach { dao.upsertBill(it) }

        listOf(
            Keys.MIN_BALANCE to "3000",
            Keys.NFC_PRICE to "8990",
            Keys.NFC_PROMO_PRICE to "7990",
            Keys.NFC_PROMO_MIN_QTY to "2",
            Keys.NFC_REF_UNIT_COST to "1167",
            Keys.NFC_REF_BATCH_COST to "35000",
        ).forEach { (k, v) -> dao.upsertSetting(Setting(k, v)) }
    }
}
