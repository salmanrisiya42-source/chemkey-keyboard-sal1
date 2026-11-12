package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.structure.BondOrder
import com.example.structure.EditorTool
import com.example.structure.StructureEngine
import com.example.structure.StructurePngExporter
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CarbonKeySurface
import com.example.ui.theme.CarbonOutline
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CarbonSurfaceElevated
import com.example.ui.theme.ChemYellow
import com.example.ui.theme.ChemYellowMuted
import com.example.ui.theme.LabCoral
import com.example.ui.theme.LabTeal
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ScientificSymbolFontFamily
import kotlin.math.hypot

@Composable
fun StructureEditorScreen(
    viewModel: MainViewModel,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.structureState.collectAsState()
    val savedStructures by viewModel.repository.savedStructures.collectAsState()
    val textMeasurer = rememberTextMeasurer()

    var paletteTab by remember { mutableStateOf("Build") }
    var canvasSize by remember { mutableStateOf(IntSize(1, 1)) }
    var draggedNodeId by remember { mutableStateOf<String?>(null) }

    val summaryText = remember(state.document) {
        StructureEngine.computeCopyableSummary(state.document).ifBlank { "Empty Canvas" }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonBlack)
    ) {
        // Top Action Bar: Title, Undo, Redo, Save, Copy, Export PNG, Share, Clear
        Surface(
            color = CarbonSurface,
            border = BorderStroke(1.dp, CarbonOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "STRUCTURE EDITOR",
                        color = ChemYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "$summaryText • ${state.document.nodes.size} atoms",
                        color = MutedSlate,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.undoStructure() },
                        enabled = state.canUndo,
                        modifier = Modifier.size(36.dp).testTag("struct_undo")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (state.canUndo) PureWhite else MutedSlate.copy(alpha = 0.35f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.redoStructure() },
                        enabled = state.canRedo,
                        modifier = Modifier.size(36.dp).testTag("struct_redo")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (state.canRedo) PureWhite else MutedSlate.copy(alpha = 0.35f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            val saved = viewModel.saveCurrentStructure()
                            onShowMessage("Saved '${saved.name}' to Keyboard Structures")
                        },
                        modifier = Modifier.size(36.dp).testTag("struct_save")
                    ) {
                        Icon(Icons.Filled.BookmarkAdd, contentDescription = "Save structure", tint = LabTeal, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = {
                            val text = StructureEngine.computeCopyableSummary(state.document)
                            if (text.isNotEmpty() && viewModel.repository.copyToClipboard(text, "Chemical Structure")) {
                                onShowMessage("Copied structure: $text")
                            }
                        },
                        modifier = Modifier.size(36.dp).testTag("struct_copy")
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy structure", tint = PureWhite, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = {
                            if (state.document.nodes.isEmpty()) {
                                onShowMessage("Draw a structure before exporting PNG")
                            } else {
                                val res = StructurePngExporter.savePngToGallery(
                                    context = context,
                                    doc = state.document,
                                    darkBackground = state.exportDarkBg
                                )
                                res.onSuccess {
                                    onShowMessage("Exported PNG to Pictures/ChemistryKeyboardPro")
                                }.onFailure { err ->
                                    onShowMessage("Export failed: ${err.message}")
                                }
                            }
                        },
                        modifier = Modifier.size(36.dp).testTag("struct_export_png")
                    ) {
                        Icon(Icons.Filled.Download, contentDescription = "Export PNG", tint = ChemYellow, modifier = Modifier.size(19.dp))
                    }
                    IconButton(
                        onClick = {
                            if (state.document.nodes.isNotEmpty()) {
                                val intent = StructurePngExporter.createShareIntent(
                                    context = context,
                                    doc = state.document,
                                    darkBackground = state.exportDarkBg
                                )
                                context.startActivity(Intent.createChooser(intent, "Share Structure PNG"))
                            }
                        },
                        modifier = Modifier.size(36.dp).testTag("struct_share_png")
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = "Share PNG", tint = PureWhite, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = { viewModel.clearStructure() },
                        modifier = Modifier.size(36.dp).testTag("struct_clear")
                    ) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Clear structure", tint = LabCoral, modifier = Modifier.size(19.dp))
                    }
                }
            }
        }

        // Interactive Molecular Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(CarbonBlack)
                .onSizeChanged { canvasSize = it }
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(state.activeTool, state.selectedNodeId, state.document) {
                        detectTapGestures { offset ->
                            val docX = offset.x - canvasSize.width / 2f
                            val docY = offset.y - canvasSize.height / 2f
                            viewModel.handleCanvasTap(docX, docY)
                        }
                    }
                    .pointerInput(state.activeTool) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val docX = offset.x - canvasSize.width / 2f
                                val docY = offset.y - canvasSize.height / 2f
                                val hit = viewModel.structureState.value.document.nodes
                                    .minByOrNull { hypot(it.x - docX, it.y - docY) }
                                    ?.takeIf { hypot(it.x - docX, it.y - docY) < 48f }
                                draggedNodeId = hit?.id
                                if (hit != null) viewModel.selectStructureNode(hit.id)
                            },
                            onDrag = { change, dragAmount ->
                                val id = draggedNodeId ?: return@detectDragGestures
                                change.consume()
                                val current = viewModel.structureState.value.document.nodes
                                    .firstOrNull { it.id == id } ?: return@detectDragGestures
                                viewModel.moveNodePosition(id, current.x + dragAmount.x, current.y + dragAmount.y)
                            },
                            onDragEnd = { draggedNodeId = null },
                            onDragCancel = { draggedNodeId = null }
                        )
                    }
                    .testTag("structure_canvas")
            ) {
                val centerX = size.width / 2f
                val centerY = size.height / 2f

                // Subtle lab grid
                val gridStep = 48f
                var gx = centerX % gridStep
                while (gx < size.width) {
                    drawLine(
                        color = Color(0xFF151822),
                        start = Offset(gx, 0f),
                        end = Offset(gx, size.height),
                        strokeWidth = 1f
                    )
                    gx += gridStep
                }
                var gy = centerY % gridStep
                while (gy < size.height) {
                    drawLine(
                        color = Color(0xFF151822),
                        start = Offset(0f, gy),
                        end = Offset(size.width, gy),
                        strokeWidth = 1f
                    )
                    gy += gridStep
                }

                val doc = state.document
                val nodeMap = doc.nodes.associateBy { it.id }

                // Draw Bonds
                doc.bonds.forEach { bond ->
                    val a = nodeMap[bond.fromId] ?: return@forEach
                    val b = nodeMap[bond.toId] ?: return@forEach
                    val p1 = Offset(centerX + a.x, centerY + a.y)
                    val p2 = Offset(centerX + b.x, centerY + b.y)
                    val dx = p2.x - p1.x
                    val dy = p2.y - p1.y
                    val len = hypot(dx, dy).coerceAtLeast(1f)
                    val nx = -dy / len
                    val ny = dx / len

                    when (bond.order) {
                        BondOrder.SINGLE -> {
                            drawLine(PureWhite, p1, p2, strokeWidth = 5f, cap = StrokeCap.Round)
                        }
                        BondOrder.DOUBLE -> {
                            val off = 7f
                            drawLine(
                                PureWhite,
                                Offset(p1.x + nx * off, p1.y + ny * off),
                                Offset(p2.x + nx * off, p2.y + ny * off),
                                strokeWidth = 4.2f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                ChemYellow,
                                Offset(p1.x - nx * off, p1.y - ny * off),
                                Offset(p2.x - nx * off, p2.y - ny * off),
                                strokeWidth = 4.2f,
                                cap = StrokeCap.Round
                            )
                        }
                        BondOrder.TRIPLE -> {
                            val off = 10f
                            drawLine(PureWhite, p1, p2, strokeWidth = 4f, cap = StrokeCap.Round)
                            drawLine(
                                ChemYellow,
                                Offset(p1.x + nx * off, p1.y + ny * off),
                                Offset(p2.x + nx * off, p2.y + ny * off),
                                strokeWidth = 3.8f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                ChemYellow,
                                Offset(p1.x - nx * off, p1.y - ny * off),
                                Offset(p2.x - nx * off, p2.y - ny * off),
                                strokeWidth = 3.8f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

                // Draw Aromatic Benzene Circles
                doc.aromaticCircles.forEach { circle ->
                    drawCircle(
                        color = ChemYellow,
                        radius = circle.radius,
                        center = Offset(centerX + circle.centerX, centerY + circle.centerY),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                    )
                }

                // Draw Selection Halo & Atom Labels
                doc.nodes.forEach { node ->
                    val sx = centerX + node.x
                    val sy = centerY + node.y
                    val isSelected = node.id == state.selectedNodeId

                    if (isSelected) {
                        drawCircle(
                            color = ChemYellow.copy(alpha = 0.24f),
                            radius = 28f,
                            center = Offset(sx, sy)
                        )
                        drawCircle(
                            color = ChemYellow,
                            radius = 22f,
                            center = Offset(sx, sy),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
                        )
                    }

                    if (!node.isImplicitCarbon || node.charge.isNotEmpty()) {
                        val display = node.label.removePrefix("–")
                        val measured = textMeasurer.measure(
                            text = display,
                            style = TextStyle(
                                color = if (isSelected) ChemYellow else PureWhite,
                                fontFamily = ScientificSymbolFontFamily,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        drawCircle(
                            color = CarbonBlack,
                            radius = (measured.size.width / 2f + 10f).coerceAtLeast(18f),
                            center = Offset(sx, sy)
                        )
                        drawText(
                            textLayoutResult = measured,
                            topLeft = Offset(sx - measured.size.width / 2f, sy - measured.size.height / 2f)
                        )
                        if (node.charge.isNotEmpty()) {
                            val chMeasured = textMeasurer.measure(
                                text = node.charge,
                                style = TextStyle(
                                    color = ChemYellow,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            drawText(
                                textLayoutResult = chMeasured,
                                topLeft = Offset(sx + measured.size.width / 2f + 2f, sy - 18f)
                            )
                        }
                    } else {
                        drawCircle(
                            color = if (isSelected) ChemYellow else PureWhite.copy(alpha = 0.7f),
                            radius = if (isSelected) 6f else 4f,
                            center = Offset(sx, sy)
                        )
                    }
                }
            }

            // Helpful Overlay Pill (top-left of canvas)
            Surface(
                color = CarbonSurfaceElevated.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, CarbonOutline),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = "Tool: ${state.activeTool.label} • Tap vertex to select, drag to move",
                    color = MutedSlate,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            // PNG Theme Switch Pill (top-right of canvas)
            Surface(
                onClick = { viewModel.toggleExportTheme() },
                color = CarbonSurfaceElevated.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, CarbonOutline),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Text(
                    text = if (state.exportDarkBg) "PNG: Dark" else "PNG: White",
                    color = ChemYellow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        // Bottom Structure Tool Palette
        Surface(
            color = CarbonSurface,
            border = BorderStroke(1.dp, CarbonOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Palette Sub-tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Build", "Rings", "Groups", "Substituents", "Charges", "Saved (${savedStructures.size})").forEach { tab ->
                        val active = paletteTab == tab || (tab.startsWith("Saved") && paletteTab.startsWith("Saved"))
                        Surface(
                            onClick = { paletteTab = tab },
                            color = if (active) ChemYellow else CarbonSurfaceElevated,
                            border = BorderStroke(1.dp, if (active) ChemYellow else CarbonOutline),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab,
                                    color = if (active) CarbonBlack else PureWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Active Palette Actions Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when {
                        paletteTab == "Build" -> {
                            // Clearly visible & selectable Benzene Ring button first!
                            EditorActionChip(
                                label = "⌬ Benzene Ring",
                                highlighted = true,
                                testTag = "editor_insert_benzene",
                                onClick = {
                                    viewModel.insertRingInEditor(sides = 6, isAromatic = true, kekule = true)
                                }
                            )
                            EditorActionChip(
                                label = "+ C₃ Chain",
                                onClick = { viewModel.addCarbonChainInEditor(steps = 3) }
                            )
                            EditorActionChip(
                                label = "+ Branch",
                                onClick = { viewModel.attachGroupInEditor("C", isImplicitCarbon = true) }
                            )
                            EditorTool.entries.forEach { tool ->
                                EditorActionChip(
                                    label = tool.label,
                                    selected = state.activeTool == tool,
                                    onClick = { viewModel.setEditorTool(tool) }
                                )
                            }
                        }

                        paletteTab == "Rings" -> {
                            EditorActionChip("⌬ Benzene", highlighted = true, onClick = { viewModel.insertRingInEditor(6, isAromatic = true, kekule = true) })
                            EditorActionChip("Benzene (Circle)", onClick = { viewModel.insertRingInEditor(6, isAromatic = true, kekule = false) })
                            EditorActionChip("Cyclohexane (6)", onClick = { viewModel.insertRingInEditor(6) })
                            EditorActionChip("Cyclopentane (5)", onClick = { viewModel.insertRingInEditor(5) })
                            EditorActionChip("Cyclobutane (4)", onClick = { viewModel.insertRingInEditor(4) })
                            EditorActionChip("Cyclopropane (3)", onClick = { viewModel.insertRingInEditor(3) })
                            EditorActionChip("Pyridine (N)", onClick = { viewModel.insertRingInEditor(6, isAromatic = true, kekule = true, heteroAtom = "N") })
                            EditorActionChip("Furan (O)", onClick = { viewModel.insertRingInEditor(5, isAromatic = false, kekule = false, heteroAtom = "O") })
                            EditorActionChip("Thiophene (S)", onClick = { viewModel.insertRingInEditor(5, isAromatic = false, kekule = false, heteroAtom = "S") })
                            EditorActionChip("Pyrrole (NH)", onClick = { viewModel.insertRingInEditor(5, isAromatic = false, kekule = false, heteroAtom = "NH") })
                        }

                        paletteTab == "Groups" -> {
                            listOf("OH", "COOH", "CHO", "COOR", "NH₂", "NO₂", "CN", "CONH₂", "SH", "SO₃H").forEach { fg ->
                                EditorActionChip(
                                    label = "–$fg",
                                    highlighted = fg in listOf("OH", "COOH", "NH₂"),
                                    onClick = { viewModel.attachGroupInEditor(fg) }
                                )
                            }
                            EditorActionChip(
                                label = "=O (Carbonyl)",
                                onClick = { viewModel.attachGroupInEditor("O", bondOrder = BondOrder.DOUBLE) }
                            )
                        }

                        paletteTab == "Substituents" -> {
                            listOf("CH₃", "C₂H₅", "Ph", "Cl", "Br", "F", "I", "OCH₃", "X", "R").forEach { sub ->
                                EditorActionChip(
                                    label = "–$sub",
                                    onClick = { viewModel.attachGroupInEditor(sub) }
                                )
                            }
                        }

                        paletteTab == "Charges" -> {
                            listOf("⁺", "⁻", "²⁺", "²⁻", "³⁺", "δ⁺", "δ⁻", "•").forEach { ch ->
                                EditorActionChip(
                                    label = "Charge $ch",
                                    highlighted = ch == "⁺" || ch == "⁻",
                                    onClick = { viewModel.applyChargeInEditor(ch) }
                                )
                            }
                        }

                        else -> {
                            if (savedStructures.isEmpty()) {
                                Text("No saved structures yet. Tap Bookmark icon above to save.", color = MutedSlate, fontSize = 12.sp)
                            } else {
                                savedStructures.forEach { item ->
                                    EditorActionChip(
                                        label = item.name,
                                        onClick = {
                                            viewModel.loadSavedStructure(item)
                                            onShowMessage("Loaded ${item.name}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorActionChip(
    label: String,
    selected: Boolean = false,
    highlighted: Boolean = false,
    testTag: String? = null,
    onClick: () -> Unit
) {
    val bg = when {
        selected -> ChemYellow
        highlighted -> ChemYellowMuted
        else -> CarbonKeySurface
    }
    val fg = when {
        selected -> CarbonBlack
        highlighted -> ChemYellow
        else -> PureWhite
    }
    val border = when {
        selected || highlighted -> ChemYellow.copy(alpha = 0.8f)
        else -> CarbonOutline
    }

    Surface(
        onClick = onClick,
        color = bg,
        border = BorderStroke(1.dp, border),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .height(42.dp)
            .let { if (testTag != null) it.testTag(testTag) else it }
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = fg,
                fontFamily = ScientificSymbolFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
