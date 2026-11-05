package br.com.daniel.meufinanceiro.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Our own line-icon set (24dp grid, 1.8 stroke), drawn for this app. */
object AppIcons {
    private fun icon(name: String, block: PathBuilder.() -> Unit): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round, pathBuilder = block)
        }.build()

    val Home = icon("home") { moveTo(4f, 10.5f); lineTo(12f, 4f); lineTo(20f, 10.5f); moveTo(6f, 9f); verticalLineTo(20f); horizontalLineTo(18f); verticalLineTo(9f); moveTo(10f, 20f); verticalLineTo(14f); horizontalLineTo(14f); verticalLineTo(20f) }
    val List = icon("list") { moveTo(8f, 6f); horizontalLineTo(20f); moveTo(8f, 12f); horizontalLineTo(20f); moveTo(8f, 18f); horizontalLineTo(20f); moveTo(4f, 6f); horizontalLineTo(4.01f); moveTo(4f, 12f); horizontalLineTo(4.01f); moveTo(4f, 18f); horizontalLineTo(4.01f) }
    val Income = icon("income") { moveTo(12f, 20f); verticalLineTo(5f); moveTo(6f, 11f); lineTo(12f, 5f); lineTo(18f, 11f) }
    val Expense = icon("expense") { moveTo(12f, 4f); verticalLineTo(19f); moveTo(6f, 13f); lineTo(12f, 19f); lineTo(18f, 13f) }
    val Card = icon("card") { moveTo(3f, 7f); arcToRelative(2f, 2f, 0f, false, true, 2f, -2f); horizontalLineTo(19f); arcToRelative(2f, 2f, 0f, false, true, 2f, 2f); verticalLineTo(17f); arcToRelative(2f, 2f, 0f, false, true, -2f, 2f); horizontalLineTo(5f); arcToRelative(2f, 2f, 0f, false, true, -2f, -2f); close(); moveTo(3f, 10f); horizontalLineTo(21f); moveTo(7f, 15f); horizontalLineTo(10f) }
    val Box = icon("box") { moveTo(4f, 8f); lineTo(12f, 4f); lineTo(20f, 8f); verticalLineTo(16f); lineTo(12f, 20f); lineTo(4f, 16f); close(); moveTo(4f, 8f); lineTo(12f, 12f); lineTo(20f, 8f); moveTo(12f, 12f); verticalLineTo(20f) }
    val More = icon("more") { moveTo(5f, 5f); horizontalLineTo(10f); verticalLineTo(10f); horizontalLineTo(5f); close(); moveTo(14f, 5f); horizontalLineTo(19f); verticalLineTo(10f); horizontalLineTo(14f); close(); moveTo(5f, 14f); horizontalLineTo(10f); verticalLineTo(19f); horizontalLineTo(5f); close(); moveTo(14f, 14f); horizontalLineTo(19f); verticalLineTo(19f); horizontalLineTo(14f); close() }
    val Work = icon("work") { moveTo(4f, 8f); horizontalLineTo(20f); verticalLineTo(19f); horizontalLineTo(4f); close(); moveTo(9f, 8f); verticalLineTo(5f); horizontalLineTo(15f); verticalLineTo(8f); moveTo(4f, 13f); horizontalLineTo(20f) }
    val Tag = icon("tag") { moveTo(7f, 3f); horizontalLineTo(17f); arcToRelative(1f, 1f, 0f, false, true, 1f, 1f); verticalLineTo(20f); arcToRelative(1f, 1f, 0f, false, true, -1f, 1f); horizontalLineTo(7f); arcToRelative(1f, 1f, 0f, false, true, -1f, -1f); verticalLineTo(4f); arcToRelative(1f, 1f, 0f, false, true, 1f, -1f); close(); moveTo(9.5f, 9f); arcToRelative(3.5f, 3.5f, 0f, false, true, 5f, 0f); moveTo(8f, 7.2f); arcToRelative(6f, 6f, 0f, false, true, 8f, 0f); moveTo(12f, 11.5f); verticalLineTo(11.51f); moveTo(9f, 17f); horizontalLineTo(15f) }
    val Pay = icon("pay") { moveTo(4f, 7f); horizontalLineTo(20f); verticalLineTo(17f); horizontalLineTo(4f); close(); moveTo(12f, 9.5f); arcToRelative(2.5f, 2.5f, 0f, true, true, 0f, 5f); arcToRelative(2.5f, 2.5f, 0f, true, true, 0f, -5f); moveTo(7f, 12f); horizontalLineTo(7.01f); moveTo(17f, 12f); horizontalLineTo(17.01f) }
    val Swap = icon("swap") { moveTo(4f, 8f); horizontalLineTo(18f); moveTo(14f, 4f); lineTo(18f, 8f); lineTo(14f, 12f); moveTo(20f, 16f); horizontalLineTo(6f); moveTo(10f, 12f); lineTo(6f, 16f); lineTo(10f, 20f) }
    val Chart = icon("chart") { moveTo(4f, 20f); horizontalLineTo(20f); moveTo(7f, 16f); verticalLineTo(11f); moveTo(12f, 16f); verticalLineTo(6f); moveTo(17f, 16f); verticalLineTo(9f) }
    val Spark = icon("spark") { moveTo(12f, 3f); lineTo(13.8f, 10.2f); lineTo(21f, 12f); lineTo(13.8f, 13.8f); lineTo(12f, 21f); lineTo(10.2f, 13.8f); lineTo(3f, 12f); lineTo(10.2f, 10.2f); close(); moveTo(19f, 3f); verticalLineTo(6f); moveTo(17.5f, 4.5f); horizontalLineTo(20.5f) }
    val Calendar = icon("calendar") { moveTo(4f, 6f); horizontalLineTo(20f); verticalLineTo(20f); horizontalLineTo(4f); close(); moveTo(4f, 10f); horizontalLineTo(20f); moveTo(8f, 3f); verticalLineTo(7f); moveTo(16f, 3f); verticalLineTo(7f) }
    val Bank = icon("bank") { moveTo(3f, 9f); lineTo(12f, 4f); lineTo(21f, 9f); moveTo(4f, 20f); horizontalLineTo(20f); moveTo(6f, 10f); verticalLineTo(17f); moveTo(10f, 10f); verticalLineTo(17f); moveTo(14f, 10f); verticalLineTo(17f); moveTo(18f, 10f); verticalLineTo(17f) }
    val Settings = icon("settings") { moveTo(12f, 9f); arcToRelative(3f, 3f, 0f, true, true, 0f, 6f); arcToRelative(3f, 3f, 0f, true, true, 0f, -6f); moveTo(12f, 2.5f); verticalLineTo(5f); moveTo(12f, 19f); verticalLineTo(21.5f); moveTo(2.5f, 12f); horizontalLineTo(5f); moveTo(19f, 12f); horizontalLineTo(21.5f); moveTo(5.3f, 5.3f); lineTo(7.1f, 7.1f); moveTo(16.9f, 16.9f); lineTo(18.7f, 18.7f); moveTo(5.3f, 18.7f); lineTo(7.1f, 16.9f); moveTo(16.9f, 7.1f); lineTo(18.7f, 5.3f) }
    val Sun = icon("sun") { moveTo(12f, 8f); arcToRelative(4f, 4f, 0f, true, true, 0f, 8f); arcToRelative(4f, 4f, 0f, true, true, 0f, -8f); moveTo(12f, 2f); verticalLineTo(4f); moveTo(12f, 20f); verticalLineTo(22f); moveTo(2f, 12f); horizontalLineTo(4f); moveTo(20f, 12f); horizontalLineTo(22f); moveTo(4.9f, 4.9f); lineTo(6.3f, 6.3f); moveTo(17.7f, 17.7f); lineTo(19.1f, 19.1f); moveTo(4.9f, 19.1f); lineTo(6.3f, 17.7f); moveTo(17.7f, 6.3f); lineTo(19.1f, 4.9f) }
    val Moon = icon("moon") { moveTo(20f, 14.5f); arcTo(8f, 8f, 0f, true, true, 9.5f, 4f); arcTo(6.5f, 6.5f, 0f, false, false, 20f, 14.5f); close() }
    val Bills = icon("bills") { moveTo(6f, 3f); horizontalLineTo(18f); verticalLineTo(21f); lineTo(15f, 19f); lineTo(12f, 21f); lineTo(9f, 19f); lineTo(6f, 21f); close(); moveTo(9f, 8f); horizontalLineTo(15f); moveTo(9f, 12f); horizontalLineTo(15f); moveTo(9f, 16f); horizontalLineTo(12f) }
    val Shield = icon("shield") { moveTo(12f, 3f); lineTo(19f, 6f); verticalLineTo(11f); curveTo(19f, 15.5f, 16f, 19f, 12f, 21f); curveTo(8f, 19f, 5f, 15.5f, 5f, 11f); verticalLineTo(6f); close(); moveTo(9f, 12f); lineTo(11f, 14f); lineTo(15f, 10f) }
    val Download = icon("download") { moveTo(12f, 4f); verticalLineTo(15f); moveTo(7f, 10f); lineTo(12f, 15f); lineTo(17f, 10f); moveTo(5f, 20f); horizontalLineTo(19f) }
    val Bolt = icon("bolt") { moveTo(13f, 3f); lineTo(5f, 14f); horizontalLineTo(12f); lineTo(11f, 21f); lineTo(19f, 10f); horizontalLineTo(12f); close() }
    val Target = icon("target") { moveTo(12f, 3f); arcToRelative(9f, 9f, 0f, true, true, 0f, 18f); arcToRelative(9f, 9f, 0f, true, true, 0f, -18f); moveTo(12f, 7f); arcToRelative(5f, 5f, 0f, true, true, 0f, 10f); arcToRelative(5f, 5f, 0f, true, true, 0f, -10f); moveTo(12f, 11f); arcToRelative(1f, 1f, 0f, true, true, 0f, 2f); arcToRelative(1f, 1f, 0f, true, true, 0f, -2f) }
}
