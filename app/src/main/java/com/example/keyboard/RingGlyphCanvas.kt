package com.example.keyboard

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.data.RingGlyphType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RingGlyphIcon(
    ringType: RingGlyphType,
    strokeColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = minOf(size.width, size.height) * 0.36f
        val stroke = Stroke(width = 2.6f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        when (ringType) {
            RingGlyphType.BENZENE, RingGlyphType.AROMATIC_RING -> {
                drawPolygon(cx, cy, r, 6, strokeColor, stroke)
                drawCircle(
                    color = accentColor,
                    radius = r * 0.56f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.1f)
                )
            }
            RingGlyphType.BENZENE_KEKULE -> {
                drawKekuleHexagon(cx, cy, r, strokeColor, accentColor, stroke)
            }
            RingGlyphType.HEXAGON, RingGlyphType.CYCLOHEXANE -> {
                drawPolygon(cx, cy, r, 6, strokeColor, stroke)
            }
            RingGlyphType.CYCLOPENTANE -> {
                drawPolygon(cx, cy, r, 5, strokeColor, stroke)
            }
            RingGlyphType.CYCLOBUTANE -> {
                drawPolygon(cx, cy, r * 0.95f, 4, strokeColor, stroke, startAngle = -PI.toFloat() / 4f)
            }
            RingGlyphType.CYCLOPROPANE -> {
                drawPolygon(cx, cy, r, 3, strokeColor, stroke)
            }
            RingGlyphType.NAPHTHALENE -> {
                val sr = r * 0.72f
                val dx = sr * 0.866f
                drawPolygon(cx - dx, cy, sr, 6, strokeColor, stroke)
                drawPolygon(cx + dx, cy, sr, 6, strokeColor, stroke)
                drawCircle(accentColor, sr * 0.52f, Offset(cx - dx, cy), style = Stroke(1.8f))
                drawCircle(accentColor, sr * 0.52f, Offset(cx + dx, cy), style = Stroke(1.8f))
            }
            RingGlyphType.ANTHRACENE -> {
                val sr = r * 0.52f
                val dx = sr * 0.866f
                drawPolygon(cx - 2f * dx, cy, sr, 6, strokeColor, stroke)
                drawPolygon(cx, cy, sr, 6, strokeColor, stroke)
                drawPolygon(cx + 2f * dx, cy, sr, 6, strokeColor, stroke)
                drawCircle(accentColor, sr * 0.48f, Offset(cx - 2f * dx, cy), style = Stroke(1.6f))
                drawCircle(accentColor, sr * 0.48f, Offset(cx, cy), style = Stroke(1.6f))
                drawCircle(accentColor, sr * 0.48f, Offset(cx + 2f * dx, cy), style = Stroke(1.6f))
            }
            RingGlyphType.PHENANTHRENE -> {
                val sr = r * 0.52f
                val dx = sr * 0.866f
                drawPolygon(cx - dx, cy + sr * 0.45f, sr, 6, strokeColor, stroke)
                drawPolygon(cx + dx, cy + sr * 0.45f, sr, 6, strokeColor, stroke)
                drawPolygon(cx, cy - sr * 1.05f, sr, 6, strokeColor, stroke)
                drawCircle(accentColor, sr * 0.45f, Offset(cx - dx, cy + sr * 0.45f), style = Stroke(1.6f))
                drawCircle(accentColor, sr * 0.45f, Offset(cx + dx, cy + sr * 0.45f), style = Stroke(1.6f))
                drawCircle(accentColor, sr * 0.45f, Offset(cx, cy - sr * 1.05f), style = Stroke(1.6f))
            }
            RingGlyphType.PYRIDINE -> {
                drawPolygon(cx, cy, r, 6, strokeColor, stroke)
                drawCircle(accentColor, r * 0.52f, Offset(cx, cy), style = Stroke(1.8f))
                drawHeteroAtomBadge(textMeasurer, "N", cx, cy + r, accentColor)
            }
            RingGlyphType.FURAN -> {
                drawPolygon(cx, cy, r, 5, strokeColor, stroke)
                drawHeteroAtomBadge(textMeasurer, "O", cx, cy - r * 0.88f, accentColor)
            }
            RingGlyphType.THIOPHENE -> {
                drawPolygon(cx, cy, r, 5, strokeColor, stroke)
                drawHeteroAtomBadge(textMeasurer, "S", cx, cy - r * 0.88f, accentColor)
            }
            RingGlyphType.PYRROLE -> {
                drawPolygon(cx, cy, r, 5, strokeColor, stroke)
                drawHeteroAtomBadge(textMeasurer, "NH", cx, cy - r * 0.88f, accentColor)
            }
        }
    }
}

private fun DrawScope.drawPolygon(
    cx: Float,
    cy: Float,
    r: Float,
    sides: Int,
    color: Color,
    stroke: Stroke,
    startAngle: Float = -PI.toFloat() / 2f
) {
    val path = Path()
    for (i in 0 until sides) {
        val a = startAngle + (2f * PI.toFloat() * i / sides)
        val x = cx + r * cos(a)
        val y = cy + r * sin(a)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color = color, style = stroke)
}

private fun DrawScope.drawKekuleHexagon(
    cx: Float,
    cy: Float,
    r: Float,
    color: Color,
    accentColor: Color,
    stroke: Stroke
) {
    drawPolygon(cx, cy, r, 6, color, stroke)
    val startAngle = -PI.toFloat() / 2f
    val innerR = r * 0.78f
    for (i in listOf(0, 2, 4)) {
        val a1 = startAngle + (2f * PI.toFloat() * i / 6)
        val a2 = startAngle + (2f * PI.toFloat() * ((i + 1) % 6) / 6)
        drawLine(
            color = accentColor,
            start = Offset(cx + innerR * cos(a1), cy + innerR * sin(a1)),
            end = Offset(cx + innerR * cos(a2), cy + innerR * sin(a2)),
            strokeWidth = 2.0f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawHeteroAtomBadge(
    textMeasurer: TextMeasurer,
    atom: String,
    x: Float,
    y: Float,
    color: Color
) {
    val measured = textMeasurer.measure(
        text = atom,
        style = TextStyle(color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold)
    )
    drawCircle(
        color = Color(0xFF191C26),
        radius = maxOf(measured.size.width, measured.size.height) * 0.55f,
        center = Offset(x, y)
    )
    drawText(
        textLayoutResult = measured,
        topLeft = Offset(x - measured.size.width / 2f, y - measured.size.height / 2f)
    )
}
