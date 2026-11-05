package br.com.daniel.meufinanceiro.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.daniel.meufinanceiro.domain.Money
import br.com.daniel.meufinanceiro.ui.theme.LocalFin

/*
 * Hand-drawn Compose charts (no chart library = fewer things that can break the install).
 * Rules followed (dataviz skill): thin marks, 4dp rounded data-end anchored at the baseline,
 * 2dp gap between bars, recessive grid, one axis, legend for 2 series, tap to inspect values,
 * text in text colors (never the series color), expense bars also carry a hatch texture so
 * income vs expense never depends on red/green alone.
 */

private fun DrawScope.topRoundedBar(left: Float, top: Float, width: Float, bottom: Float, color: Color, hatch: Boolean) {
    if (bottom - top <= 0.5f) return
    val r = minOf(4.dp.toPx(), width / 2, bottom - top)
    val path = Path().apply {
        addRoundRect(RoundRect(Rect(left, top, left + width, bottom), topLeft = CornerRadius(r), topRight = CornerRadius(r), bottomLeft = CornerRadius.Zero, bottomRight = CornerRadius.Zero))
    }
    if (!hatch) {
        drawPath(path, color)
    } else {
        drawPath(path, color.copy(alpha = 0.55f))
        clipPath(path) {
            val step = 5.dp.toPx()
            var x = left - (bottom - top)
            while (x < left + width) {
                drawLine(color, Offset(x, bottom), Offset(x + (bottom - top), top), strokeWidth = 1.5.dp.toPx())
                x += step
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, hatch: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.width(14.dp).height(10.dp)) { topRoundedBar(0f, 0f, size.width, size.height, color, hatch) }
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = LocalFin.current.muted)
    }
}

/** Grouped bars: entradas (solid) vs saídas (hatched), per bucket. Tap a group to read its values. */
@Composable
fun IncomeExpenseChart(data: List<Triple<String, Long, Long>>, modifier: Modifier = Modifier) {
    val fin = LocalFin.current
    var selected by remember(data) { mutableIntStateOf(-1) }
    val anim = remember { Animatable(0f) }
    LaunchedEffect(data) { anim.snapTo(0f); anim.animateTo(1f, tween(500)) }
    val max = (data.maxOfOrNull { maxOf(it.second, it.third) } ?: 0L).coerceAtLeast(1L)
    val totalIn = data.sumOf { it.second }
    val totalOut = data.sumOf { it.third }

    Column(modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            LegendItem(fin.chartIncome, "Entradas", false)
            LegendItem(fin.chartExpense, "Saídas", true)
        }
        Spacer(Modifier.height(8.dp))
        val sel = data.getOrNull(selected)
        Text(
            if (sel != null) "${sel.first}: entrou ${Money.format(sel.second)} · saiu ${Money.format(sel.third)}"
            else "No período: entrou ${Money.format(totalIn)} · saiu ${Money.format(totalOut)}",
            style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        Canvas(
            Modifier.fillMaxWidth().height(150.dp)
                .semantics { contentDescription = "Gráfico de entradas e saídas. Entrou ${Money.format(totalIn)}, saiu ${Money.format(totalOut)}." }
                .pointerInput(data) {
                    detectTapGestures { pos ->
                        if (data.isEmpty()) return@detectTapGestures
                        val i = (pos.x / (size.width / data.size)).toInt().coerceIn(0, data.size - 1)
                        selected = if (selected == i) -1 else i
                    }
                },
        ) {
            val n = data.size.coerceAtLeast(1)
            val groupW = size.width / n
            val gap = 2.dp.toPx()
            val barW = ((groupW * 0.62f) - gap) / 2
            val h = size.height
            // recessive grid: baseline + mid line
            drawLine(fin.chartGrid, Offset(0f, h), Offset(size.width, h), 1.dp.toPx())
            drawLine(fin.chartGrid, Offset(0f, h / 2), Offset(size.width, h / 2), 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
            data.forEachIndexed { i, (_, inc, exp) ->
                val cx = groupW * i + groupW / 2
                val left = cx - barW - gap / 2
                val dim = selected != -1 && selected != i
                val ci = if (dim) fin.chartIncome.copy(alpha = 0.35f) else fin.chartIncome
                val ce = if (dim) fin.chartExpense.copy(alpha = 0.35f) else fin.chartExpense
                topRoundedBar(left, h - h * (inc.toFloat() / max) * anim.value, barW, h, ci, false)
                topRoundedBar(cx + gap / 2, h - h * (exp.toFloat() / max) * anim.value, barW, h, ce, true)
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth()) {
            val step = if (data.size > 8) 2 else 1
            data.forEachIndexed { i, d ->
                Text(if (i % step == 0) d.first else "", style = MaterialTheme.typography.labelMedium, color = fin.faint,
                    modifier = Modifier.weight(1f), maxLines = 1, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    }
}

/** Horizontal magnitude bars with direct labels (one hue: label carries identity). */
@Composable
fun HBarList(items: List<Pair<String, Long>>, color: Color, modifier: Modifier = Modifier, total: Long? = null, maxItems: Int = 7, showShare: Boolean = true) {
    val fin = LocalFin.current
    val shown = if (items.size > maxItems) items.take(maxItems - 1) + ("Outros" to items.drop(maxItems - 1).sumOf { it.second }) else items
    val max = (shown.maxOfOrNull { it.second } ?: 0L).coerceAtLeast(1L)
    val sum = total ?: items.sumOf { it.second }
    val anim = remember { Animatable(0f) }
    LaunchedEffect(items) { anim.snapTo(0f); anim.animateTo(1f, tween(500)) }
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        shown.forEach { (label, v) ->
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (showShare && sum > 0) Text("${v * 100 / sum}%  ", style = MaterialTheme.typography.bodySmall, color = fin.muted)
                    Text(Money.format(v), style = MaterialTheme.typography.titleSmall)
                }
                Spacer(Modifier.height(4.dp))
                Canvas(Modifier.fillMaxWidth().height(8.dp)) {
                    val r = CornerRadius(4.dp.toPx())
                    drawRoundRect(fin.chartGrid, size = size, cornerRadius = r)
                    drawRoundRect(color, size = Size(size.width * (v.toFloat() / max) * anim.value, size.height), cornerRadius = r)
                }
            }
        }
    }
}

/** Single-series line with area; drag/tap to read a point. */
@Composable
fun LineChart(points: List<Pair<String, Long>>, color: Color, modifier: Modifier = Modifier, description: String = "Evolução") {
    val fin = LocalFin.current
    var selected by remember(points) { mutableIntStateOf(-1) }
    val anim = remember { Animatable(0f) }
    LaunchedEffect(points) { anim.snapTo(0f); anim.animateTo(1f, tween(700)) }
    if (points.isEmpty()) return
    val minV = minOf(0L, points.minOf { it.second })
    val maxV = points.maxOf { it.second }.coerceAtLeast(minV + 1)
    Column(modifier.fillMaxWidth()) {
        val sel = points.getOrNull(selected) ?: points.last()
        Row(verticalAlignment = Alignment.Bottom) {
            Text(Money.format(sel.second), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.width(8.dp))
            Text(sel.first, style = MaterialTheme.typography.bodySmall, color = fin.muted, modifier = Modifier.padding(bottom = 2.dp))
        }
        Spacer(Modifier.height(8.dp))
        Canvas(
            Modifier.fillMaxWidth().height(120.dp)
                .semantics { contentDescription = "$description: de ${Money.format(points.first().second)} para ${Money.format(points.last().second)}" }
                .pointerInput(points) {
                    fun pick(x: Float) { selected = ((x / size.width) * (points.size - 1) + 0.5f).toInt().coerceIn(0, points.size - 1) }
                    detectTapGestures { pick(it.x) }
                }
                .pointerInput(points) {
                    detectDragGestures(onDragEnd = { }) { change, _ ->
                        selected = ((change.position.x / size.width) * (points.size - 1) + 0.5f).toInt().coerceIn(0, points.size - 1)
                    }
                },
        ) {
            val w = size.width
            val h = size.height - 6.dp.toPx()
            fun x(i: Int) = if (points.size == 1) w / 2 else w * i / (points.size - 1)
            fun y(v: Long) = 3.dp.toPx() + h - h * ((v - minV).toFloat() / (maxV - minV))
            val zeroY = y(0)
            drawLine(fin.chartGrid, Offset(0f, zeroY), Offset(w, zeroY), 1.dp.toPx())
            val line = Path()
            points.forEachIndexed { i, p -> if (i == 0) line.moveTo(x(i), y(p.second)) else line.lineTo(x(i), y(p.second)) }
            val area = Path().apply { addPath(line); lineTo(x(points.size - 1), zeroY); lineTo(x(0), zeroY); close() }
            clipPath(Path().apply { addRect(Rect(0f, 0f, w * anim.value, size.height)) }) {
                drawPath(area, color.copy(alpha = 0.12f))
                drawPath(line, color, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            val si = if (selected >= 0) selected else points.size - 1
            val px = x(si); val py = y(points[si].second)
            if (selected >= 0) drawLine(fin.faint, Offset(px, 0f), Offset(px, size.height), 1.dp.toPx())
            drawCircle(fin.chartGrid.copy(alpha = 1f), 6.dp.toPx(), Offset(px, py))
            drawCircle(color, 4.dp.toPx(), Offset(px, py))
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth()) {
            Text(points.first().first, style = MaterialTheme.typography.labelMedium, color = fin.faint, modifier = Modifier.weight(1f))
            Text(points.last().first, style = MaterialTheme.typography.labelMedium, color = fin.faint)
        }
    }
}

/** Circular progress for goals. */
@Composable
fun ProgressRing(progress: Float, color: Color, modifier: Modifier = Modifier, stroke: Float = 6f) {
    val fin = LocalFin.current
    val anim = remember { Animatable(0f) }
    LaunchedEffect(progress) { anim.animateTo(progress.coerceIn(0f, 1f), tween(700)) }
    Canvas(modifier) {
        val s = stroke.dp.toPx()
        val d = minOf(size.width, size.height) - s
        val tl = Offset((size.width - d) / 2, (size.height - d) / 2)
        drawArc(fin.chartGrid, -90f, 360f, false, tl, Size(d, d), style = Stroke(s, cap = StrokeCap.Round))
        if (anim.value > 0f) drawArc(color, -90f, 360f * anim.value, false, tl, Size(d, d), style = Stroke(s, cap = StrokeCap.Round))
    }
}
