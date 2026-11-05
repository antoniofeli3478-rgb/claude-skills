package br.com.daniel.meufinanceiro.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

/** Dates are stored as epochDay (LocalDate.toEpochDay()). Months are stored as yyyymm Int (e.g. 202611). */
object Dates {
    private val monthNames = listOf(
        "janeiro", "fevereiro", "março", "abril", "maio", "junho",
        "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
    )
    private val monthShort = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")
    private val weekShort = listOf("seg", "ter", "qua", "qui", "sex", "sáb", "dom")

    fun day(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)
    fun today(): Long = LocalDate.now().toEpochDay()

    fun ym(date: LocalDate): Int = date.year * 100 + date.monthValue
    fun ym(epochDay: Long): Int = ym(day(epochDay))
    fun ymToYearMonth(ym: Int): YearMonth = YearMonth.of(ym / 100, ym % 100)
    fun ymPlus(ym: Int, months: Long): Int {
        val m = ymToYearMonth(ym).plusMonths(months)
        return m.year * 100 + m.monthValue
    }
    fun ymMinus(a: Int, b: Int): Int {
        val ya = ymToYearMonth(a)
        val yb = ymToYearMonth(b)
        return (ya.year - yb.year) * 12 + (ya.monthValue - yb.monthValue)
    }

    /** Day `d` inside month `ym`, clamped to the month length (day 31 in February -> 28/29). */
    fun dayInMonth(ym: Int, d: Int): LocalDate {
        val y = ymToYearMonth(ym)
        return y.atDay(d.coerceIn(1, y.lengthOfMonth()))
    }

    fun format(epochDay: Long): String {
        val d = day(epochDay)
        return "%02d/%02d/%04d".format(d.dayOfMonth, d.monthValue, d.year)
    }
    fun formatShort(epochDay: Long): String {
        val d = day(epochDay)
        return "%02d/%02d".format(d.dayOfMonth, d.monthValue)
    }
    fun relative(epochDay: Long, today: Long = today()): String = when (epochDay) {
        today -> "Hoje"
        today - 1 -> "Ontem"
        today + 1 -> "Amanhã"
        else -> {
            val d = day(epochDay)
            "${weekShort[d.dayOfWeek.value - 1]}, ${d.dayOfMonth} de ${monthShort[d.monthValue - 1]}" +
                if (d.year != day(today).year) " de ${d.year}" else ""
        }
    }
    fun monthName(ym: Int): String = monthNames[(ym % 100) - 1]
    fun monthLabel(ym: Int): String = monthName(ym).replaceFirstChar { it.uppercase() } + " " + (ym / 100)
    fun monthShortLabel(ym: Int): String = monthShort[(ym % 100) - 1]
    fun weekdayShort(epochDay: Long): String = weekShort[day(epochDay).dayOfWeek.value - 1]

    /** Parses "dd/mm" or "dd/mm/yyyy". */
    fun parse(text: String, today: Long = today()): Long? {
        val parts = text.trim().split('/', '-', '.')
        return try {
            when (parts.size) {
                2 -> LocalDate.of(day(today).year, parts[1].toInt(), parts[0].toInt()).toEpochDay()
                3 -> {
                    var y = parts[2].toInt()
                    if (y < 100) y += 2000
                    LocalDate.of(y, parts[1].toInt(), parts[0].toInt()).toEpochDay()
                }
                else -> null
            }
        } catch (_: Exception) { null }
    }
}

enum class PeriodKind(val label: String) {
    TODAY("Hoje"), WEEK("Esta semana"), MONTH("Este mês"), LAST_MONTH("Mês anterior"), CUSTOM("Personalizado")
}

/** Inclusive date range in epochDays. */
data class Period(val kind: PeriodKind, val start: Long, val end: Long) {
    operator fun contains(epochDay: Long) = epochDay in start..end
    val days: Int get() = (end - start + 1).toInt()

    val label: String
        get() = when (kind) {
            PeriodKind.TODAY -> "Hoje"
            PeriodKind.WEEK -> "Semana ${Dates.formatShort(start)} – ${Dates.formatShort(end)}"
            PeriodKind.MONTH, PeriodKind.LAST_MONTH -> Dates.monthLabel(Dates.ym(start))
            PeriodKind.CUSTOM -> "${Dates.format(start)} – ${Dates.format(end)}"
        }

    /** The equally long period immediately before this one (used for comparisons). */
    fun previous(): Period = when (kind) {
        PeriodKind.MONTH, PeriodKind.LAST_MONTH -> {
            val s = Dates.day(start).minusMonths(1).withDayOfMonth(1)
            Period(PeriodKind.CUSTOM, s.toEpochDay(), s.with(TemporalAdjusters.lastDayOfMonth()).toEpochDay())
        }
        else -> Period(PeriodKind.CUSTOM, start - days, start - 1)
    }

    companion object {
        /** Weeks run Monday to Sunday. */
        fun of(kind: PeriodKind, today: Long = Dates.today(), customStart: Long? = null, customEnd: Long? = null): Period {
            val t = Dates.day(today)
            return when (kind) {
                PeriodKind.TODAY -> Period(kind, today, today)
                PeriodKind.WEEK -> {
                    val s = t.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    Period(kind, s.toEpochDay(), s.plusDays(6).toEpochDay())
                }
                PeriodKind.MONTH -> Period(kind, t.withDayOfMonth(1).toEpochDay(), t.with(TemporalAdjusters.lastDayOfMonth()).toEpochDay())
                PeriodKind.LAST_MONTH -> {
                    val s = t.minusMonths(1).withDayOfMonth(1)
                    Period(kind, s.toEpochDay(), s.with(TemporalAdjusters.lastDayOfMonth()).toEpochDay())
                }
                PeriodKind.CUSTOM -> {
                    val s = customStart ?: today
                    val e = customEnd ?: today
                    Period(kind, minOf(s, e), maxOf(s, e))
                }
            }
        }
        fun month(ym: Int): Period {
            val y = Dates.ymToYearMonth(ym)
            return Period(PeriodKind.CUSTOM, y.atDay(1).toEpochDay(), y.atEndOfMonth().toEpochDay())
        }
    }
}
