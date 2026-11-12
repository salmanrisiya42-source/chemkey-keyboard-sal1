package com.example

import android.app.Application
import android.content.Context
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.AndroidViewModel
import com.example.data.KeyboardTab
import com.example.data.SavedStructureSummary
import com.example.keyboard.KeyboardTarget
import com.example.storage.ChemStorageRepository
import com.example.structure.BondOrder
import com.example.structure.EditorTool
import com.example.structure.StructureDocument
import com.example.structure.StructureEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.hypot

data class ImeStatus(
    val isEnabled: Boolean = false,
    val isSelected: Boolean = false
)

data class StructureEditorUiState(
    val document: StructureDocument = StructureEngine.createInitialBenzene(),
    val selectedNodeId: String? = null,
    val activeTool: EditorTool = EditorTool.SINGLE_BOND,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val exportDarkBg: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository: ChemStorageRepository = ChemStorageRepository.getInstance(application)

    private val _scratchpadText = MutableStateFlow("CH₃COOH + NaOH ⇌ CH₃COONa + H₂O\n⌬ + HNO₃ —(H₂SO₄, Δ)→ ⌬–NO₂ + H₂O")
    val scratchpadText: StateFlow<String> = _scratchpadText.asStateFlow()

    private val scratchUndo = ArrayDeque<String>()
    private val scratchRedo = ArrayDeque<String>()

    private val _imeStatus = MutableStateFlow(ImeStatus())
    val imeStatus: StateFlow<ImeStatus> = _imeStatus.asStateFlow()

    private val _structureState = MutableStateFlow(StructureEditorUiState())
    val structureState: StateFlow<StructureEditorUiState> = _structureState.asStateFlow()

    private val structUndo = ArrayDeque<StructureDocument>()
    private val structRedo = ArrayDeque<StructureDocument>()

    private val _requestedNavIndex = MutableStateFlow(0)
    val requestedNavIndex: StateFlow<Int> = _requestedNavIndex.asStateFlow()

    init {
        refreshImeStatus()
    }

    fun selectNavTab(index: Int) {
        _requestedNavIndex.value = index
    }

    fun refreshImeStatus() {
        val ctx = getApplication<Application>()
        val imm = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val pkg = ctx.packageName
        val enabled = imm?.enabledInputMethodList?.any { it.packageName == pkg } == true
        val currentDefault = try {
            Settings.Secure.getString(ctx.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        } catch (_: Exception) {
            null
        }
        val selected = currentDefault?.startsWith("$pkg/") == true
        _imeStatus.value = ImeStatus(isEnabled = enabled, isSelected = selected)
    }

    fun setScratchpadText(newText: String) {
        if (newText == _scratchpadText.value) return
        pushScratchUndo(_scratchpadText.value)
        _scratchpadText.value = newText
    }

    fun clearScratchpad() {
        if (_scratchpadText.value.isEmpty()) return
        pushScratchUndo(_scratchpadText.value)
        _scratchpadText.value = ""
    }

    fun insertIntoScratchpad(text: String) {
        val current = _scratchpadText.value
        pushScratchUndo(current)
        if (text == " ") {
            val lastToken = current.takeLastWhile { !it.isWhitespace() }
            if (lastToken.isNotEmpty()) {
                val shortcut = repository.findShortcutExpansion(lastToken)
                if (shortcut != null) {
                    _scratchpadText.value = current.dropLast(lastToken.length) + shortcut.expansion + " "
                    repository.recordRecentSymbol(shortcut.expansion)
                    return
                }
            }
        }
        _scratchpadText.value = current + text
    }

    fun createScratchpadTarget(onOpenEditor: () -> Unit): KeyboardTarget = object : KeyboardTarget {
        override fun commitText(text: String) {
            insertIntoScratchpad(text)
        }

        override fun deleteBack() {
            val current = _scratchpadText.value
            if (current.isEmpty()) return
            pushScratchUndo(current)
            val cp = Character.codePointBefore(current, current.length)
            _scratchpadText.value = current.dropLast(Character.charCount(cp))
        }

        override fun performEnter() {
            insertIntoScratchpad("\n")
        }

        override fun undo() {
            val prev = scratchUndo.removeLastOrNull() ?: return
            scratchRedo.addLast(_scratchpadText.value)
            _scratchpadText.value = prev
        }

        override fun redo() {
            val next = scratchRedo.removeLastOrNull() ?: return
            scratchUndo.addLast(_scratchpadText.value)
            _scratchpadText.value = next
        }

        override fun copySelectionOrAll(): String? {
            return _scratchpadText.value.takeIf { it.isNotEmpty() }
        }

        override fun pasteText(text: String) {
            insertIntoScratchpad(text)
        }

        override fun switchKeyboard() {
            val imm = getApplication<Application>().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showInputMethodPicker()
        }

        override fun openStructureEditor() {
            onOpenEditor()
        }
    }

    private fun pushScratchUndo(snapshot: String) {
        if (scratchUndo.size >= 60) scratchUndo.removeFirst()
        scratchUndo.addLast(snapshot)
        scratchRedo.clear()
    }

    // --- Visual Structure Editor Actions ---

    fun setEditorTool(tool: EditorTool) {
        _structureState.update { it.copy(activeTool = tool) }
    }

    fun toggleExportTheme() {
        _structureState.update { it.copy(exportDarkBg = !it.exportDarkBg) }
    }

    fun selectStructureNode(nodeId: String?) {
        _structureState.update { it.copy(selectedNodeId = nodeId) }
    }

    fun insertRingInEditor(
        sides: Int,
        isAromatic: Boolean = false,
        kekule: Boolean = false,
        heteroAtom: String? = null
    ) {
        mutateStructure { doc, selectedId ->
            StructureEngine.insertRing(
                doc = doc,
                sides = sides,
                isAromatic = isAromatic,
                kekuleAlternating = kekule,
                heteroAtomAtZero = heteroAtom,
                selectedNodeId = selectedId
            )
        }
    }

    fun addCarbonChainInEditor(steps: Int = 3) {
        val bondOrder = activeBondOrder()
        mutateStructure { doc, selectedId ->
            StructureEngine.addCarbonChain(doc, selectedId, steps = steps, bondOrder = bondOrder)
        }
    }

    fun attachGroupInEditor(label: String, isImplicitCarbon: Boolean = false, bondOrder: BondOrder = BondOrder.SINGLE) {
        mutateStructure { doc, selectedId ->
            StructureEngine.attachSubstituentOrBranch(
                doc = doc,
                selectedNodeId = selectedId,
                label = label,
                bondOrder = bondOrder,
                isImplicitCarbon = isImplicitCarbon
            )
        }
    }

    fun applyChargeInEditor(charge: String) {
        mutateStructure { doc, selectedId ->
            StructureEngine.applyChargeToNode(doc, selectedId, charge) to selectedId
        }
    }

    fun handleCanvasTap(docX: Float, docY: Float) {
        val state = _structureState.value
        val doc = state.document

        // 1. Check if user tapped a node
        val hitNode = doc.nodes.minByOrNull { hypot(it.x - docX, it.y - docY) }
            ?.takeIf { hypot(it.x - docX, it.y - docY) < 42f }

        if (hitNode != null) {
            when (state.activeTool) {
                EditorTool.ERASE -> {
                    mutateStructure { d, _ ->
                        StructureEngine.eraseNode(d, hitNode.id) to null
                    }
                }
                EditorTool.SINGLE_BOND, EditorTool.DOUBLE_BOND, EditorTool.TRIPLE_BOND -> {
                    val prevId = state.selectedNodeId
                    if (prevId != null && prevId != hitNode.id) {
                        val order = activeBondOrder()
                        mutateStructure { d, _ ->
                            StructureEngine.addBondToPosition(d, prevId, hitNode.x, hitNode.y, order)
                        }
                    } else {
                        _structureState.update { it.copy(selectedNodeId = hitNode.id) }
                    }
                }
                EditorTool.SELECT -> {
                    _structureState.update { it.copy(selectedNodeId = hitNode.id) }
                }
            }
            return
        }

        // 2. Check if user tapped near a bond midpoint
        val nodeMap = doc.nodes.associateBy { it.id }
        val hitBond = doc.bonds.firstOrNull { bond ->
            val a = nodeMap[bond.fromId] ?: return@firstOrNull false
            val b = nodeMap[bond.toId] ?: return@firstOrNull false
            distanceToSegment(docX, docY, a.x, a.y, b.x, b.y) < 26f
        }
        if (hitBond != null) {
            when (state.activeTool) {
                EditorTool.ERASE -> {
                    mutateStructure { d, sel ->
                        StructureEngine.eraseBond(d, hitBond.id) to sel
                    }
                }
                EditorTool.SINGLE_BOND -> mutateStructure { d, sel ->
                    StructureEngine.cycleBondOrder(d, hitBond.id, BondOrder.SINGLE) to sel
                }
                EditorTool.DOUBLE_BOND -> mutateStructure { d, sel ->
                    StructureEngine.cycleBondOrder(d, hitBond.id, BondOrder.DOUBLE) to sel
                }
                EditorTool.TRIPLE_BOND -> mutateStructure { d, sel ->
                    StructureEngine.cycleBondOrder(d, hitBond.id, BondOrder.TRIPLE) to sel
                }
                EditorTool.SELECT -> mutateStructure { d, sel ->
                    StructureEngine.cycleBondOrder(d, hitBond.id, null) to sel
                }
            }
            return
        }

        // 3. Empty canvas tap
        if (state.activeTool == EditorTool.SINGLE_BOND ||
            state.activeTool == EditorTool.DOUBLE_BOND ||
            state.activeTool == EditorTool.TRIPLE_BOND
        ) {
            val anchorId = state.selectedNodeId ?: doc.nodes.lastOrNull()?.id
            if (anchorId == null) {
                mutateStructure { d, _ ->
                    val first = com.example.structure.ChemNode(
                        id = "n_${System.currentTimeMillis()}",
                        x = docX,
                        y = docY,
                        label = "C",
                        isImplicitCarbon = true
                    )
                    d.copy(nodes = listOf(first)) to first.id
                }
            } else {
                val order = activeBondOrder()
                mutateStructure { d, _ ->
                    StructureEngine.addBondToPosition(d, anchorId, docX, docY, order)
                }
            }
        } else {
            _structureState.update { it.copy(selectedNodeId = null) }
        }
    }

    fun moveNodePosition(nodeId: String, newX: Float, newY: Float) {
        _structureState.update { st ->
            val updatedNodes = st.document.nodes.map { n ->
                if (n.id == nodeId) n.copy(x = newX, y = newY) else n
            }
            st.copy(document = st.document.copy(nodes = updatedNodes))
        }
    }

    fun undoStructure() {
        val prev = structUndo.removeLastOrNull() ?: return
        structRedo.addLast(_structureState.value.document)
        _structureState.update {
            it.copy(
                document = prev,
                selectedNodeId = prev.nodes.lastOrNull()?.id,
                canUndo = structUndo.isNotEmpty(),
                canRedo = structRedo.isNotEmpty()
            )
        }
    }

    fun redoStructure() {
        val next = structRedo.removeLastOrNull() ?: return
        structUndo.addLast(_structureState.value.document)
        _structureState.update {
            it.copy(
                document = next,
                selectedNodeId = next.nodes.lastOrNull()?.id,
                canUndo = structUndo.isNotEmpty(),
                canRedo = structRedo.isNotEmpty()
            )
        }
    }

    fun clearStructure() {
        mutateStructure { _, _ ->
            StructureDocument(title = "New Structure") to null
        }
    }

    fun loadSavedStructure(summary: SavedStructureSummary) {
        val parsed = StructureEngine.deserialize(summary.serializedJson) ?: return
        mutateStructure { _, _ ->
            parsed to parsed.nodes.lastOrNull()?.id
        }
    }

    fun saveCurrentStructure(customName: String? = null): SavedStructureSummary {
        val doc = _structureState.value.document
        val formula = StructureEngine.computeCopyableSummary(doc).ifBlank { "Structure" }
        val summary = SavedStructureSummary(
            id = "st_${System.currentTimeMillis()}",
            name = customName?.takeIf { it.isNotBlank() } ?: formula,
            formula = formula,
            serializedJson = StructureEngine.serialize(doc),
            timestamp = System.currentTimeMillis()
        )
        repository.saveStructure(summary)
        return summary
    }

    private fun activeBondOrder(): BondOrder = when (_structureState.value.activeTool) {
        EditorTool.DOUBLE_BOND -> BondOrder.DOUBLE
        EditorTool.TRIPLE_BOND -> BondOrder.TRIPLE
        else -> BondOrder.SINGLE
    }

    private fun mutateStructure(block: (StructureDocument, String?) -> Pair<StructureDocument, String?>) {
        val current = _structureState.value
        if (structUndo.size >= 40) structUndo.removeFirst()
        structUndo.addLast(current.document)
        structRedo.clear()
        val (nextDoc, nextSelected) = block(current.document, current.selectedNodeId)
        _structureState.value = current.copy(
            document = nextDoc,
            selectedNodeId = nextSelected,
            canUndo = structUndo.isNotEmpty(),
            canRedo = false
        )
    }

    private fun distanceToSegment(px: Float, py: Float, x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val l2 = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)
        if (l2 == 0f) return hypot(px - x1, py - y1)
        val t = (((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / l2).coerceIn(0f, 1f)
        val projX = x1 + t * (x2 - x1)
        val projY = y1 + t * (y2 - y1)
        return hypot(px - projX, py - projY)
    }
}
