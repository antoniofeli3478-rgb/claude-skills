package br.com.daniel.meufinanceiro.domain

import kotlin.math.abs

/**
 * All money is stored as Long cents. Never use floating point for balances.
 */
object Money {

    /** Formats cents as "R$ 1.234,56" (or "-R$ 12,00"). */
    fun format(cents: Long, withSymbol: Boolean = true, showPlus: Boolean = false): String {
        val negative = cents < 0
        val absCents = abs(cents)
        val reais = absCents / 100
        val centavos = (absCents % 100).toInt()
        val reaisStr = groupThousands(reais)
        val body = "$reaisStr,${centavos.toString().padStart(2, '0')}"
        val sign = when {
            negative -> "-"
            showPlus && cents > 0 -> "+"
            else -> ""
        }
        return if (withSymbol) "${sign}R$ $body" else "$sign$body"
    }

    /** Short form for chart labels: "R$ 1,2 mil", "R$ 850". */
    fun short(cents: Long): String {
        val reais = cents / 100.0
        val a = abs(reais)
        val sign = if (reais < 0) "-" else ""
        return when {
            a >= 1_000_000 -> sign + "R$ " + oneDecimal(a / 1_000_000) + " mi"
            a >= 1_000 -> sign + "R$ " + oneDecimal(a / 1_000) + " mil"
            else -> sign + "R$ " + a.toLong().toString()
        }
    }

    private fun oneDecimal(v: Double): String {
        val tenths = Math.round(v * 10)
        val whole = tenths / 10
        val dec = tenths % 10
        return if (dec == 0L) whole.toString() else "$whole,$dec"
    }

    private fun groupThousands(value: Long): String {
        val s = value.toString()
        val sb = StringBuilder()
        var count = 0
        for (i in s.length - 1 downTo 0) {
            sb.append(s[i])
            count++
            if (count % 3 == 0 && i != 0) sb.append('.')
        }
        return sb.reverse().toString()
    }

    /**
     * Parses user input like "25", "25,5", "25,50", "1.234,56", "R$ 18", "18.90".
     * Returns null for invalid input. Never throws.
     */
    fun parse(input: String): Long? {
        var s = input.trim().replace("R$", "", ignoreCase = true).replace(" ", "").replace(" ", "")
        if (s.isEmpty()) return null
        val negative = s.startsWith("-")
        if (negative) s = s.substring(1)
        if (s.isEmpty() || !s.all { it.isDigit() || it == ',' || it == '.' }) return null
        val lastComma = s.lastIndexOf(',')
        val lastDot = s.lastIndexOf('.')
        val decimalSep: Char? = when {
            lastComma >= 0 && lastDot >= 0 -> if (lastComma > lastDot) ',' else '.'
            lastComma >= 0 -> ','
            // "18.90" -> decimal; "1.234" -> thousands (3 digits after the dot)
            lastDot >= 0 -> {
                val digitsAfter = s.length - lastDot - 1
                if (s.count { it == '.' } == 1 && digitsAfter in 1..2) '.' else '#'
            }
            else -> null
        }
        val intPart: String
        val fracPart: String
        if (decimalSep == null || decimalSep == '#') {
            intPart = s.replace(".", "").replace(",", "")
            fracPart = ""
        } else {
            val idx = s.lastIndexOf(decimalSep)
            intPart = s.substring(0, idx).replace(".", "").replace(",", "")
            fracPart = s.substring(idx + 1)
            if (fracPart.length > 2 || fracPart.any { !it.isDigit() }) return null
        }
        if (intPart.isEmpty() && fracPart.isEmpty()) return null
        if (intPart.length > 13) return null
        val reais = if (intPart.isEmpty()) 0L else intPart.toLongOrNull() ?: return null
        val cents = when (fracPart.length) {
            0 -> 0L
            1 -> fracPart.toLong() * 10
            else -> fracPart.toLong()
        }
        val total = reais * 100 + cents
        return if (negative) -total else total
    }

    /** Value for editing in a text field: 2550 -> "25,50"; 2500 -> "25". */
    fun toInput(cents: Long): String {
        val a = abs(cents)
        val r = a / 100
        val c = a % 100
        val sign = if (cents < 0) "-" else ""
        return if (c == 0L) "$sign$r" else "$sign$r,${c.toString().padStart(2, '0')}"
    }

    /** cents * basisPoints / 10000, floored (basis points: 5000 = 50%). */
    fun percentOf(cents: Long, basisPoints: Int): Long = cents * basisPoints / 10_000

    fun formatPercent(basisPoints: Int): String {
        val whole = basisPoints / 100
        val frac = basisPoints % 100
        return if (frac == 0) "$whole%" else "$whole,${frac.toString().padStart(2, '0').trimEnd('0')}%"
    }
}
