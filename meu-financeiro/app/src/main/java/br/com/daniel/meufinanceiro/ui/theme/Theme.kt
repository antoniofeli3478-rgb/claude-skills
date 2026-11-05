package br.com.daniel.meufinanceiro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Semantic money colors, separate from Material roles. */
@Immutable
data class FinColors(
    val income: Color,
    val expense: Color,
    val warn: Color,
    val info: Color,
    val muted: Color,
    val faint: Color,
    val cardBorder: Color,
    val chartGrid: Color,
    /** Chart marks: validated for color-blind separation together with the hatch texture on expense bars. */
    val chartIncome: Color,
    val chartExpense: Color,
    val isDark: Boolean,
)

val LocalFin = staticCompositionLocalOf {
    FinColors(Color.Green, Color.Red, Color.Yellow, Color.Blue, Color.Gray, Color.LightGray, Color.LightGray, Color.LightGray, Color.Green, Color.Red, false)
}

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF3DDC97),
    onPrimary = Color(0xFF00321F),
    primaryContainer = Color(0xFF15402D),
    onPrimaryContainer = Color(0xFFB8F5D8),
    secondary = Color(0xFF9FB7A9),
    onSecondary = Color(0xFF0E1210),
    secondaryContainer = Color(0xFF243029),
    onSecondaryContainer = Color(0xFFD9E7DE),
    background = Color(0xFF0C100E),
    onBackground = Color(0xFFE8EEEA),
    surface = Color(0xFF0C100E),
    onSurface = Color(0xFFE8EEEA),
    surfaceVariant = Color(0xFF1A211D),
    onSurfaceVariant = Color(0xFF9AA69F),
    surfaceContainerLowest = Color(0xFF090C0B),
    surfaceContainerLow = Color(0xFF121815),
    surfaceContainer = Color(0xFF151C18),
    surfaceContainerHigh = Color(0xFF1B231F),
    surfaceContainerHighest = Color(0xFF222B26),
    outline = Color(0xFF34403A),
    outlineVariant = Color(0xFF242D28),
    error = Color(0xFFFF8A7A),
    onError = Color(0xFF3B0A04),
    errorContainer = Color(0xFF4A1A14),
    onErrorContainer = Color(0xFFFFDAD4),
)

private val LightScheme = lightColorScheme(
    primary = Color(0xFF0B8A57),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCFF3E1),
    onPrimaryContainer = Color(0xFF002114),
    secondary = Color(0xFF4F6358),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2ECE6),
    onSecondaryContainer = Color(0xFF0C1F15),
    background = Color(0xFFF5F7F5),
    onBackground = Color(0xFF111714),
    surface = Color(0xFFF5F7F5),
    onSurface = Color(0xFF111714),
    surfaceVariant = Color(0xFFE9EEEA),
    onSurfaceVariant = Color(0xFF56635B),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFBFCFB),
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color(0xFFF0F3F1),
    surfaceContainerHighest = Color(0xFFE8ECE9),
    outline = Color(0xFFC5CEC8),
    outlineVariant = Color(0xFFE1E7E3),
    error = Color(0xFFC6443A),
    onError = Color.White,
    errorContainer = Color(0xFFFFE2DD),
    onErrorContainer = Color(0xFF410002),
)

private val DarkFin = FinColors(
    income = Color(0xFF3DDC97), expense = Color(0xFFFF8A7A), warn = Color(0xFFF5B84B), info = Color(0xFF8AB4FF),
    muted = Color(0xFF9AA69F), faint = Color(0xFF66726B), cardBorder = Color(0xFF212A25), chartGrid = Color(0xFF242D28),
    chartIncome = Color(0xFF1E9C6C), chartExpense = Color(0xFFDB6655), isDark = true,
)
private val LightFin = FinColors(
    income = Color(0xFF0B8A57), expense = Color(0xFFC9483E), warn = Color(0xFFB7791F), info = Color(0xFF2F64D6),
    muted = Color(0xFF56635B), faint = Color(0xFF8A968F), cardBorder = Color(0xFFE3E9E5), chartGrid = Color(0xFFE6EBE8),
    chartIncome = Color(0xFF0B8A57), chartExpense = Color(0xFFC9483E), isDark = false,
)

private val AppTypography = Typography().let { t ->
    t.copy(
        displaySmall = t.displaySmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 34.sp, letterSpacing = (-0.5).sp, fontFeatureSettings = "tnum"),
        headlineMedium = t.headlineMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 26.sp, letterSpacing = (-0.3).sp, fontFeatureSettings = "tnum"),
        headlineSmall = t.headlineSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, letterSpacing = (-0.2).sp),
        titleLarge = t.titleLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
        titleMedium = t.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, fontFeatureSettings = "tnum"),
        titleSmall = t.titleSmall.copy(fontWeight = FontWeight.Medium, fontSize = 14.sp, fontFeatureSettings = "tnum"),
        bodyLarge = t.bodyLarge.copy(fontSize = 16.sp, lineHeight = 23.sp),
        bodyMedium = t.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp, fontFeatureSettings = "tnum"),
        bodySmall = t.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp, fontFeatureSettings = "tnum"),
        labelLarge = t.labelLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
        labelMedium = t.labelMedium.copy(fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.2.sp),
    )
}

val MoneyLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 34.sp, letterSpacing = (-0.6).sp, fontFeatureSettings = "tnum")

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

/** theme: "system" | "light" | "dark" */
@Composable
fun MeuFinanceiroTheme(theme: String?, content: @Composable () -> Unit) {
    val dark = when (theme) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val scheme: ColorScheme = if (dark) DarkScheme else LightScheme
    CompositionLocalProvider(LocalFin provides if (dark) DarkFin else LightFin) {
        MaterialTheme(colorScheme = scheme, typography = AppTypography, shapes = AppShapes, content = content)
    }
}
