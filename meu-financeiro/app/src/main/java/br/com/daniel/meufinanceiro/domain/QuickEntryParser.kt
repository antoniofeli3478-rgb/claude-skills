package br.com.daniel.meufinanceiro.domain

import br.com.daniel.meufinanceiro.data.Category
import br.com.daniel.meufinanceiro.data.IncomeSource
import br.com.daniel.meufinanceiro.data.PayMethod
import br.com.daniel.meufinanceiro.data.SourceType
import java.text.Normalizer

/**
 * Offline interpreter for quick entries such as:
 *  "Almoço, R$ 25, Pix"  ·  "Gastei 18 reais no dinheiro com alimentação"
 *  "Recebi 80 do Tio Rex"  ·  "Tênis 300 no crédito em 3x"  ·  "uber 12,50 ontem"
 * The result is ALWAYS shown to Daniel for confirmation before saving.
 */
object QuickEntryParser {

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

    /** Keyword -> category name (matched against Daniel's categories, accent-insensitive). */
    private val categoryHints = mapOf(
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
        "Trabalho" to listOf("ferramenta", "material de trabalho", "impressao", "adobe", "software"),
        "Estoque NFC" to listOf("nfc", "plaquinha", "plaquinhas", "acrilico", "tag", "tags", "lote", "adesivo"),
    )

    private val sourceHints = mapOf(
        SourceType.TIO_REX to listOf("tio rex", "tiorex", "rex"),
        SourceType.RECREACAO to listOf("recreacao", "recreador", "festa infantil", "animacao", "evento de recreacao"),
        SourceType.PIPOCA to listOf("pipoca", "algodao", "algodao doce", "algodao-doce"),
        SourceType.NFC to listOf("nfc", "plaquinha", "plaquinhas"),
    )

    private val stopWords = setOf(
        "gastei", "paguei", "comprei", "recebi", "ganhei", "entrou", "com", "no", "na", "de", "do", "da", "em", "um", "uma",
        "reais", "real", "rs", "r", "pila", "conto", "contos", "pelo", "pela", "pra", "para", "o", "a", "e", "os", "as",
        "hoje", "ontem", "anteontem", "via", "x", "vezes", "parcelado", "parcelas", "meu", "minha", "foi", "por", "ao", "aos",
        "dinheiro", "pix", "debito", "credito", "cartao", "transferencia", "especie", "despesa", "gasto", "entrada",
    )

    fun normalize(s: String): String =
        Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "")

    fun parse(text: String, categories: List<Category>, sources: List<IncomeSource>, today: Long = Dates.today()): Result {
        val norm = normalize(text)
        val understood = mutableListOf<String>()

        // Installments: "3x", "em 3 vezes", "parcelado em 3"
        var installments = 1
        Regex("(\\d{1,2})\\s*(x|vezes|parcelas)\\b").find(norm)?.let {
            installments = it.groupValues[1].toInt().coerceIn(1, 48)
        }
        val textNoInst = norm.replace(Regex("(\\d{1,2})\\s*(x|vezes|parcelas)\\b"), " ")

        // Date
        var date = today
        when {
            Regex("\\banteontem\\b").containsMatchIn(norm) -> { date = today - 2; understood += "Data: anteontem" }
            Regex("\\bontem\\b").containsMatchIn(norm) -> { date = today - 1; understood += "Data: ontem" }
        }
        val dateMatch = Regex("\\b(\\d{1,2})/(\\d{1,2})(/(\\d{2,4}))?\\b").find(textNoInst)
        if (dateMatch != null) {
            Dates.parse(dateMatch.value, today)?.let { date = it; understood += "Data: ${Dates.format(it)}" }
        }
        val textNoDate = if (dateMatch != null) textNoInst.replace(dateMatch.value, " ") else textNoInst

        // Amount: first number (accepts 18 / 18,50 / 1.234,56 / r$ 18)
        val numbers = Regex("(r\\$\\s*)?(\\d{1,3}(\\.\\d{3})+(,\\d{1,2})?|\\d+([.,]\\d{1,2})?)(\\s*(reais|real|conto|contos|pila))?")
            .findAll(textNoDate).toList()
        // Prefer a number written as money ("r$ 18", "18 reais"); "99" alone is usually the ride app.
        val amountMatch = numbers.firstOrNull { it.value.startsWith("r$") || it.groupValues[6].isNotBlank() }
            ?: numbers.firstOrNull { it.value.trim() != "99" || numbers.size == 1 }
        val amount = amountMatch?.let { Money.parse(it.groupValues[2]) }

        // Method
        val method = methodWords.firstOrNull { (w, _) -> Regex("\\b$w\\b").containsMatchIn(norm) }?.second
            ?: if (installments > 1) PayMethod.CREDIT else null

        // Type
        val hasIncomeVerb = incomeVerbs.any { Regex("\\b$it\\b").containsMatchIn(norm) }
        val hasExpenseVerb = expenseVerbs.any { Regex("\\b$it\\b").containsMatchIn(norm) }
        val sourceMatch = sources.filter { !it.archived }.firstOrNull { src ->
            val hints = (sourceHints[src.type] ?: emptyList()) + normalize(src.name)
            hints.any { h -> h.isNotBlank() && norm.contains(h) }
        }
        val isIncome = when {
            hasIncomeVerb && !hasExpenseVerb -> true
            hasExpenseVerb -> false
            sourceMatch != null && sourceMatch.type != SourceType.NFC -> true
            else -> false
        }

        // Category (expenses): explicit category name first, then keyword hints.
        var categoryId: Long? = null
        if (!isIncome) {
            val active = categories.filter { !it.archived }
            categoryId = active.firstOrNull { norm.contains(normalize(it.name)) }?.id
            if (categoryId == null) {
                val words = Regex("[a-z0-9]+").findAll(textNoDate).map { it.value }.toSet()
                for ((catName, hints) in categoryHints) {
                    val hit = hints.any { h -> if (h.contains(' ')) norm.contains(h) else h in words }
                    if (hit) { categoryId = active.firstOrNull { normalize(it.name) == normalize(catName) }?.id; if (categoryId != null) break }
                }
            }
        }

        // Description = leftover meaningful words, or the category / source name.
        var leftover = textNoDate
        amountMatch?.let { leftover = leftover.replaceFirst(it.value, " ") }
        val originalWords = Regex("[\\p{L}0-9]+").findAll(text).map { it.value }.toList()
        val keep = originalWords.filter { w ->
            val n = normalize(w)
            n !in stopWords && n.toDoubleOrNull() == null && !Regex("^\\d").containsMatchIn(n) &&
                (categories.none { normalize(it.name) == n })
        }
        var description = keep.joinToString(" ").trim()
        if (isIncome && sourceMatch != null) description = sourceMatch.name
        if (description.isBlank()) {
            description = if (isIncome) sourceMatch?.name ?: "Entrada" else categories.firstOrNull { it.id == categoryId }?.name ?: "Despesa"
        }
        description = description.replaceFirstChar { it.uppercase() }

        understood.add(0, if (isIncome) "Tipo: entrada" else "Tipo: despesa")
        amount?.let { understood += "Valor: ${Money.format(it)}" } ?: understood.add("Valor: não encontrado")
        method?.let { understood += "Forma: ${PayMethod.label(it)}" }
        if (installments > 1) understood += "Parcelado em ${installments}x"
        categories.firstOrNull { it.id == categoryId }?.let { understood += "Categoria: ${it.name}" }
        if (isIncome) sourceMatch?.let { understood += "Fonte: ${it.name}" }

        return Result(isIncome, amount, description, method, categoryId, if (isIncome) sourceMatch?.id else null, installments, date, understood)
    }
}
