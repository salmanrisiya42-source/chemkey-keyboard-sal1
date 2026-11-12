package com.example.structure

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import kotlin.math.hypot

object StructurePngExporter {

    fun renderStructureBitmap(
        doc: StructureDocument,
        darkBackground: Boolean = false,
        widthPx: Int = 1200,
        heightPx: Int = 900
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgColor = if (darkBackground) Color.parseColor("#0B0C10") else Color.WHITE
        val bondColor = if (darkBackground) Color.parseColor("#F8FAFC") else Color.parseColor("#111827")
        val accentColor = if (darkBackground) Color.parseColor("#FFD600") else Color.parseColor("#B45309")
        val labelBgColor = bgColor

        canvas.drawColor(bgColor)

        if (doc.nodes.isEmpty()) return bitmap

        val minX = doc.nodes.minOf { it.x }
        val maxX = doc.nodes.maxOf { it.x }
        val minY = doc.nodes.minOf { it.y }
        val maxY = doc.nodes.maxOf { it.y }

        val spanX = (maxX - minX).coerceAtLeast(140f)
        val spanY = (maxY - minY).coerceAtLeast(140f)
        val pad = 160f
        val scale = minOf((widthPx - pad * 2) / spanX, (heightPx - pad * 2) / spanY).coerceIn(0.8f, 2.4f)
        val centerDocX = (minX + maxX) / 2f
        val centerDocY = (minY + maxY) / 2f

        fun toScreenX(x: Float) = widthPx / 2f + (x - centerDocX) * scale
        fun toScreenY(y: Float) = heightPx / 2f + (y - centerDocY) * scale

        val bondPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bondColor
            strokeWidth = 6f
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }

        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accentColor
            strokeWidth = 4.5f
            style = Paint.Style.STROKE
        }

        val nodeLookup = doc.nodes.associateBy { it.id }

        doc.bonds.forEach { bond ->
            val from = nodeLookup[bond.fromId] ?: return@forEach
            val to = nodeLookup[bond.toId] ?: return@forEach
            val x1 = toScreenX(from.x)
            val y1 = toScreenY(from.y)
            val x2 = toScreenX(to.x)
            val y2 = toScreenY(to.y)
            val dx = x2 - x1
            val dy = y2 - y1
            val len = hypot(dx, dy).coerceAtLeast(1f)
            val nx = -dy / len
            val ny = dx / len

            when (bond.order) {
                BondOrder.SINGLE -> canvas.drawLine(x1, y1, x2, y2, bondPaint)
                BondOrder.DOUBLE -> {
                    val off = 9f
                    canvas.drawLine(x1 + nx * off, y1 + ny * off, x2 + nx * off, y2 + ny * off, bondPaint)
                    canvas.drawLine(x1 - nx * off, y1 - ny * off, x2 - nx * off, y2 - ny * off, bondPaint)
                }
                BondOrder.TRIPLE -> {
                    val off = 13f
                    canvas.drawLine(x1, y1, x2, y2, bondPaint)
                    canvas.drawLine(x1 + nx * off, y1 + ny * off, x2 + nx * off, y2 + ny * off, bondPaint)
                    canvas.drawLine(x1 - nx * off, y1 - ny * off, x2 - nx * off, y2 - ny * off, bondPaint)
                }
            }
        }

        doc.aromaticCircles.forEach { circle ->
            canvas.drawCircle(
                toScreenX(circle.centerX),
                toScreenY(circle.centerY),
                circle.radius * scale,
                ringPaint
            )
        }

        val bgCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = labelBgColor
            style = Paint.Style.FILL
        }
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bondColor
            textSize = 44f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val chargePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accentColor
            textSize = 32f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }

        doc.nodes.forEach { node ->
            if (!node.isImplicitCarbon || node.charge.isNotEmpty()) {
                val sx = toScreenX(node.x)
                val sy = toScreenY(node.y)
                val displayText = node.label.removePrefix("–")
                val textWidth = textPaint.measureText(displayText)
                canvas.drawCircle(sx, sy, (textWidth / 2f + 16f).coerceAtLeast(28f), bgCirclePaint)
                val baselineOffset = (textPaint.descent() + textPaint.ascent()) / 2f
                canvas.drawText(displayText, sx, sy - baselineOffset, textPaint)
                if (node.charge.isNotEmpty()) {
                    canvas.drawText(node.charge, sx + textWidth / 2f + 6f, sy - 14f, chargePaint)
                }
            }
        }

        // Watermark footer
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (darkBackground) Color.parseColor("#64748B") else Color.parseColor("#94A3B8")
            textSize = 24f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("Chemistry Keyboard – Pro", widthPx - 36f, heightPx - 28f, footerPaint)

        return bitmap
    }

    fun savePngToGallery(context: Context, doc: StructureDocument, darkBackground: Boolean): Result<Uri> {
        return runCatching {
            val bitmap = renderStructureBitmap(doc, darkBackground = darkBackground)
            val fileName = "chem_structure_${System.currentTimeMillis()}.png"
            val resolver = context.contentResolver

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/ChemistryKeyboardPro")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: error("Failed to create MediaStore entry")
                resolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                } ?: error("Failed to open output stream")
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                uri
            } else {
                val dir = File(context.cacheDir, "structures").apply { mkdirs() }
                val file = File(dir, fileName)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            }
        }
    }

    fun createShareIntent(context: Context, doc: StructureDocument, darkBackground: Boolean): Intent {
        val bitmap = renderStructureBitmap(doc, darkBackground = darkBackground)
        val dir = File(context.cacheDir, "structures").apply { mkdirs() }
        val file = File(dir, "chem_structure_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
