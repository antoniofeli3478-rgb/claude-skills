package br.com.daniel.meufinanceiro.domain

import br.com.daniel.meufinanceiro.data.Category
import br.com.daniel.meufinanceiro.data.IncomeSource
import br.com.daniel.meufinanceiro.data.PayMethod
import br.com.daniel.meufinanceiro.data.SourceType
import java.text.Normalizer

/**
 * Offline interpreter for quick entries such as:
 *  "Almoço, R$ 25, Pix" · "Gastei 18 reais no dinheiro com alimentação" · "Tio Rex ganhei 80"
 *  "Uber 15 para trabalhar no Tio Rex" · "Ônibus 5 para voltar do trabalho" · "Vendi 2 plaquinhas por 89,90 cada"
 *  "Comprei 10 acrílicos por 37 reais" · "Minha mãe me emprestou 30 reais" · "Minha mãe me deu 20 pro Uber"
 * The result is ALWAYS shown for confirmation before saving; missing pieces are listed in [missing].
 */
object QuickEntryParser {

    enum class Kind(val label: String) {
        EXPENSE("Despesa"), INCOME("Entrada"), WORK_INCOME("Pagamento de trabalho"), LOAN("Empréstimo recebido"),
        FAMILY("Dinheiro da família / ajuda"), NFC_SALE("Venda de plaquinhas NFC"), NFC_MATERIAL("Compra de material NFC"),
    }

    data class Result(
        val isIncome: Boolean,
        val amountCents: Long?,
        val description: String,
        val method: String?,
        val categoryId: Long?,
        val sourceId: Long?,
        val installments: Int,
        val date: Long,
        /** What was understood, in plain Portuguese, for the confirmation screen. */
        val understood: List<String>,
        val kind: Kind = if (isIncome) Kind.INCOME else Kind.EXPENSE,
        /** Expense that is a cost of a job (Uber para trabalhar...). */
        val workCost: Boolean = false,
        /** Source the work cost belongs to (null when it must be asked). */
        val workSourceId: Long? = null,
        val quantity: Int? = null,
        val unitPriceCents: Long? = null,
        val person: String? = null,
        /** Things that must be answered before saving. */
        val missing: List<String> = emptyList(),
    )

    private val incomeVerbs = listOf("recebi", "ganhei", "entrou", "entrada", "receber", "pagaram", "me pagou", "vendi", "faturei", "caiu")
    private val expenseVerbs = listOf("gastei", "paguei", "comprei", "despesa", "gasto", "saiu", "pagar")

    private val methodWords = listOf(
        "pix" to PayMethod.PIX,
        "dinheiro" to PayMethod.CASH, "especie" to PayMethod.CASH, "em maos" to PayMethod.CASH, "cash" to PayMethod.CASH,
        "debito" to PayMethod.DEBIT,
        "credito" to PayMethod.CREDIT, "cartao" to PayMethod.CREDIT, "parcelado" to PayMethod.CREDIT,
        "transferencia" to PayMethod.TRANSFER, "ted" to PayMethod.TRANSFER, "doc" to PayMethod.TRANSFER,
    )

    /** Keyword -> category name (matched against the categories, accent-insensitive). Order matters (first hit wins). */
    private val categoryHints = linkedMapOf(
        "NFC: acrílicos" to listOf("acrilico", "acrilicos"),
        "NFC: etiquetas NFC" to listOf("etiqueta", "etiquetas", "tag", "tags", "chip", "chips"),
        "NFC: impressão" to listOf("impressao", "impressoes", "arte", "artes", "adesivo", "adesivos"),
        "NFC: embalagens" to listOf("embalagem", "embalagens", "saquinho", "saquinhos"),
        "Estoque NFC" to listOf("nfc", "plaquinha", "plaquinhas", "lote"),
        "Alimentação" to listOf("almoco", "janta", "jantar", "lanche", "comida", "mercado", "ifood", "restaurante", "cafe", "padaria", "pizza", "acai", "hamburguer", "salgado", "agua", "refrigerante", "supermercado", "feira", "marmita", "sorvete", "doce"),
        "Transporte" to listOf("uber", "99", "onibus", "gasolina", "combustivel", "metro", "passagem", "estacionamento", "taxi", "moto", "brt", "trem", "pedagio"),
        "Academia" to listOf("academia", "gym", "suplemento", "whey", "creatina"),
        "Muay Thai" to listOf("muay", "thai", "luta", "luva", "bandagem"),
        "Assinaturas" to listOf("spotify", "netflix", "claude", "assinatura", "youtube", "prime", "disney", "icloud", "chatgpt", "hbo", "globoplay", "deezer"),
        "Saúde" to listOf("farmacia", "remedio", "medico", "dentista", "consulta", "exame", "hospital"),
        "Lazer" to listOf("cinema", "show", "bar", "jogo", "steam", "festa", "role", "balada", "praia", "passeio", "ingresso", "psn", "xbox"),
        "Família" to listOf("pai", "mae", "irmao", "irma", "familia", "vo", "avo"),
        "Educação" to listOf("curso", "livro", "escola", "faculdade", "ingles", "aula", "apostila"),
        "Compras pessoais" to listOf("roupa", "tenis", "camisa", "calca", "loja", "shopping", "perfume", "fone", "celular", "relogio", "bone"),
        "Trabalho" to listOf("ferramenta", "material de trabalho", "software"),
    )

    private val sourceHints = mapOf(
        SourceType.TIO_REX to listOf("tio rex", "tiorex", "rex"),
        SourceType.RECREACAO to listOf("recreacao", "recreador", "festa infantil", "animacao", "evento de recreacao"),
        SourceType.PIPOCA to listOf("pipoca", "algodao", "algodao doce", "algodao-doce"),
        SourceType.NFC to listOf("nfc", "plaquinha", "plaquinhas"),
    )

    private val familyWords = listOf("mae", "pai", "vo", "avo", "vovo", "tia", "tio", "irmao", "irma", "madrinha", "padrinho", "familia", "namorada", "namorado")
    private val workCostPhrases = listOf("para trabalhar", "pra trabalhar", "pro trabalho", "para o trabalho", "do trabalho", "ao trabalho", "no trabalho", "ir trabalhar", "voltar do trabalho", "volta do trabalho", "ida do trabalho", "do servico", "pro servico", "para o servico", "do evento", "pro evento", "para o evento")

    private val stopWords = setOf(
        "gastei", "paguei", "comprei", "recebi", "ganhei", "entrou", "com", "no", "na", "de", "do", "da", "em", "um", "uma",
        "reais", "real", "rs", "r", "pila", "conto", "contos", "pelo", "pela", "pra", "para", "pro", "o", "a", "e", "os", "as",
        "hoje", "ontem", "anteontem", "via", "x", "vezes", "parcelado", "parcelas", "meu", "minha", "foi", "por", "ao", "aos",
        "dinheiro", "pix", "debito", "credito", "cartao", "transferencia", "especie", "despesa", "gasto", "entrada", "cada",
        "trabalhar", "trabalho", "voltar", "volta", "ir", "ida", "me", "servico",
    )

    fun normalize(s: String): String =
        Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "")

    private fun hasWord(norm: String, w: String) = Regex("\\b${Regex.escape(w)}\\b").containsMatchIn(norm)

    fun parse(text: String, categories: List<Category>, sources: List<IncomeSource>, today: Long = Dates.today()): Result {
        val norm = normalize(text)
        val understood = mutableListOf<String>()
        val missing = mutableListOf<String>()

        // Installments: "3x", "em 3 vezes", "parcelado em 3"
        var installments = 1
        Regex("(\\d{1,2})\\s*(x|vezes|parcelas)\\b").find(norm)?.let { installments = it.groupValues[1].toInt().coerceIn(1, 48) }
        val textNoInst = norm.replace(Regex("(\\d{1,2})\\s*(x|vezes|parcelas)\\b"), " ")

        // Date
        var date = today
        when {
            hasWord(norm, "anteontem") -> { date = today - 2; understood += "Data: anteontem" }
            hasWord(norm, "ontem") -> { date = today - 1; understood += "Data: ontem" }
        }
        val dateMatch = Regex("\\b(\\d{1,2})/(\\d{1,2})(/(\\d{2,4}))?\\b").find(textNoInst)
        if (dateMatch != null) Dates.parse(dateMatch.value, today)?.let { date = it; understood += "Data: ${Dates.format(it)}" }
        val textNoDate = if (dateMatch != null) textNoInst.replace(dateMatch.value, " ") else textNoInst

        // Numbers. Priority: "por <n>" -> written as money (r$ / reais) -> first that isn't the ride app "99".
        val numRe = Regex("(r\\$\\s*)?(\\d{1,3}(\\.\\d{3})+(,\\d{1,2})?|\\d+([.,]\\d{1,2})?)(\\s*(reais|real|conto|contos|pila))?")
        val numbers = numRe.findAll(textNoDate).toList()
        val porMatch = Regex("\\bpor\\s+(r\\$\\s*)?(\\d{1,3}(\\.\\d{3})+(,\\d{1,2})?|\\d+([.,]\\d{1,2})?)").find(textNoDate)
        val amountMatch = numbers.firstOrNull { it.value.startsWith("r$") || it.groupValues[6].isNotBlank() }
            ?: numbers.firstOrNull { it.value.trim() != "99" || numbers.size == 1 }
        var amount = (porMatch?.groupValues?.get(2) ?: amountMatch?.groupValues?.get(2))?.let { Money.parse(it) }

        val method = methodWords.firstOrNull { (w, _) -> hasWord(norm, w) }?.second ?: if (installments > 1) PayMethod.CREDIT else null

        val activeSources = sources.filter { !it.archived }
        val sourceMatch = activeSources.firstOrNull { src ->
            val hints = (sourceHints[src.type] ?: emptyList()) + normalize(src.name)
            hints.any { h -> h.isNotBlank() && norm.contains(h) }
        }
        val hasIncomeVerb = incomeVerbs.any { hasWord(norm, it) }
        val hasExpenseVerb = expenseVerbs.any { hasWord(norm, it) }
        val familyPerson = familyWords.firstOrNull { hasWord(norm, it) }

        // ---------- Kind ----------
        var quantity: Int? = null
        var unitPrice: Long? = null
        val kind: Kind = when {
            Regex("\\bemprest").containsMatchIn(norm) && (hasWord(norm, "me") || hasIncomeVerb || familyPerson != null || hasWord(norm, "peguei")) -> Kind.LOAN
            familyPerson != null && Regex("\\b(me deu|me deram|me mandou|me passou|me enviou|me ajudou|ganhei d[aoe])\\b").containsMatchIn(norm) -> Kind.FAMILY
            hasWord(norm, "vendi") && (norm.contains("plaquinha") || norm.contains("nfc")) -> Kind.NFC_SALE
            (hasWord(norm, "comprei") || hasExpenseVerb) && listOf("acrilico", "etiqueta", "tag nfc", "tags nfc", "impressao", "embalage", "chip").any { norm.contains(it) } -> Kind.NFC_MATERIAL
            sourceMatch != null && sourceMatch.type != SourceType.NFC && (hasIncomeVerb || (!hasExpenseVerb && numbers.isNotEmpty() && workCostPhrases.none { norm.contains(it) } && categoryHintFor(norm, textNoDate) == null)) -> Kind.WORK_INCOME
            hasIncomeVerb && !hasExpenseVerb -> Kind.INCOME
            else -> Kind.EXPENSE
        }

        if (kind == Kind.NFC_SALE) {
            // "vendi 2 plaquinhas por 89,90 cada" / "vendi 3 plaquinhas por 240"
            quantity = Regex("\\bvendi\\s+(\\d{1,3})\\b").find(norm)?.groupValues?.get(1)?.toIntOrNull()
                ?: Regex("(\\d{1,3})\\s+(plaquinha|plaquinhas|un|unidades)").find(norm)?.groupValues?.get(1)?.toIntOrNull() ?: 1
            val price = porMatch?.groupValues?.get(2)?.let { Money.parse(it) }
                ?: numbers.map { Money.parse(it.groupValues[2]) }.firstOrNull { it != null && it != quantity.toLong() * 100 }
            unitPrice = if (price != null && Regex("\\bcada\\b|\\bunidade\\b|/un").containsMatchIn(norm)) price
            else price?.let { it / quantity!! }
            amount = unitPrice?.let { it * quantity!! }
        }

        // ---------- Category / work cost ----------
        var categoryId: Long? = null
        val isMoneyOut = kind == Kind.EXPENSE || kind == Kind.NFC_MATERIAL
        if (isMoneyOut) {
            val active = categories.filter { !it.archived }
            categoryId = active.firstOrNull { norm.contains(normalize(it.name)) && !normalize(it.name).startsWith("nfc:") }?.id
            if (categoryId == null) categoryHintFor(norm, textNoDate)?.let { name -> categoryId = active.firstOrNull { normalize(it.name) == normalize(name) }?.id }
            if (kind == Kind.NFC_MATERIAL && categories.firstOrNull { it.id == categoryId }?.isNfcStock != true)
                categoryId = active.firstOrNull { it.isNfcStock }?.id
        }
        val isWorkCost = kind == Kind.EXPENSE && (workCostPhrases.any { norm.contains(it) } ||
            (sourceMatch != null && sourceMatch.type != SourceType.NFC && Regex("\\b(para|pra|pro|no|ate o|ate a)\\b").containsMatchIn(norm)))
        val workSourceId = if (isWorkCost) sourceMatch?.takeIf { it.type != SourceType.NFC }?.id else null

        // ---------- Description ----------
        val originalWords = Regex("[\\p{L}0-9]+").findAll(text).map { it.value }.toList()
        val sourceWords = sourceMatch?.let { s -> (sourceHints[s.type] ?: emptyList()).flatMap { it.split(' ') }.toSet() + normalize(s.name).split(' ') } ?: emptySet()
        val keep = originalWords.filter { w ->
            val n = normalize(w)
            n !in stopWords && n.toDoubleOrNull() == null && !Regex("^\\d").containsMatchIn(n) &&
                categories.none { normalize(it.name) == n } && n !in sourceWords && n !in familyWords &&
                !n.startsWith("emprest") && n != "vendi" && n != "deu"
        }
        var description = keep.joinToString(" ").trim()
        when (kind) {
            Kind.WORK_INCOME -> description = sourceMatch?.name ?: "Trabalho"
            Kind.INCOME -> if (sourceMatch != null) description = sourceMatch.name
            Kind.LOAN -> description = "Empréstimo de ${familyPerson ?: description.ifBlank { "alguém" }}"
            Kind.FAMILY -> description = "Dinheiro de ${familyPerson ?: "família"}" + (if (description.isNotBlank()) " ($description)" else "")
            Kind.NFC_SALE -> description = "Venda de ${quantity ?: 1} plaquinha(s) NFC"
            else -> Unit
        }
        if (isWorkCost && sourceMatch != null) description = (description.ifBlank { categories.firstOrNull { it.id == categoryId }?.name ?: "Custo" }) + " (trabalho: ${sourceMatch.name})"
        if (description.isBlank()) description = if (isMoneyOut) categories.firstOrNull { it.id == categoryId }?.name ?: "Despesa" else "Entrada"
        description = description.replaceFirstChar { it.uppercase() }

        // ---------- Understood / missing ----------
        understood.add(0, "Tipo: ${kind.label.lowercase()}")
        if (amount != null) understood += "Valor: ${Money.format(amount)}" else missing += "o valor"
        if (kind == Kind.NFC_SALE) understood += "${quantity ?: 1} un. a ${unitPrice?.let { Money.format(it) } ?: "?"}"
        method?.let { understood += "Forma: ${PayMethod.label(it)}" }
        if (installments > 1) understood += "Parcelado em ${installments}x"
        categories.firstOrNull { it.id == categoryId }?.let { understood += "Categoria: ${it.name}" }
        if (kind == Kind.WORK_INCOME || kind == Kind.INCOME) sourceMatch?.let { understood += "Fonte: ${it.name}" }
        if (familyPerson != null && (kind == Kind.LOAN || kind == Kind.FAMILY)) understood += "De: $familyPerson"
        if (isWorkCost) {
            if (workSourceId != null) understood += "Custo do trabalho: ${sourceMatch!!.name}"
            else missing += "qual trabalho esse custo pertence"
        }
        if (isMoneyOut && categoryId == null) missing += "a categoria"
        if (kind == Kind.LOAN && familyPerson == null && description.contains("alguém")) missing += "quem emprestou"

        val isIncome = kind in setOf(Kind.INCOME, Kind.WORK_INCOME, Kind.LOAN, Kind.FAMILY, Kind.NFC_SALE)
        return Result(
            isIncome = isIncome, amountCents = amount, description = description, method = method, categoryId = categoryId,
            sourceId = if (kind == Kind.INCOME || kind == Kind.WORK_INCOME) sourceMatch?.id else null,
            installments = installments, date = date, understood = understood, kind = kind,
            workCost = isWorkCost, workSourceId = workSourceId, quantity = quantity, unitPriceCents = unitPrice,
            person = familyPerson, missing = missing,
        )
    }

    private fun categoryHintFor(norm: String, textNoDate: String): String? {
        val words = Regex("[a-z0-9]+").findAll(textNoDate).map { it.value }.toSet()
        for ((catName, hints) in categoryHints) {
            if (hints.any { h -> if (h.contains(' ')) norm.contains(h) else h in words }) return catName
        }
        return null
    }
}
