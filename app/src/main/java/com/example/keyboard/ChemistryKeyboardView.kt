package com.example.keyboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChemKey
import com.example.data.GreekAndTemplatesData
import com.example.data.InorganicChemData
import com.example.data.KeyboardTab
import com.example.data.OrganicChemData
import com.example.data.PeriodicElement
import com.example.data.PeriodicTableData
import com.example.data.PhysicalChemData
import com.example.storage.ChemStorageRepository
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CarbonKeySurface
import com.example.ui.theme.CarbonOutline
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CarbonSurfaceElevated
import com.example.ui.theme.ChemYellow
import com.example.ui.theme.ChemYellowMuted
import com.example.ui.theme.LabTeal
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ScientificSymbolFontFamily

@Composable
fun ChemistryKeyboardView(
    repository: ChemStorageRepository,
    target: KeyboardTarget,
    initialTab: KeyboardTab? = null,
    heightDp: Int = 320,
    onFeedbackMessage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val settings by repository.settings.collectAsState()
    val recents by repository.recentSymbols.collectAsState()
    val favorites by repository.favoriteSymbols.collectAsState()
    val clipboardHistory by repository.clipboardHistory.collectAsState()
    val shortcuts by repository.customShortcuts.collectAsState()
    val savedStructures by repository.savedStructures.collectAsState()

    var activeTab by remember(initialTab, settings.defaultTab) {
        mutableStateOf(initialTab ?: settings.defaultTab)
    }
    var showSubscriptsFirst by remember { mutableStateOf(true) }
    var isQwertyMode by remember { mutableStateOf(false) }
    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var inspectedElement by remember { mutableStateOf<PeriodicElement?>(null) }

    fun insertAndRecord(text: String) {
        target.commitText(text)
        repository.recordRecentSymbol(text)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .background(CarbonBlack)
    ) {
        // 1. Top 7-Tab Strip + Search Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CarbonSurface)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                KeyboardTab.entries.forEach { tab ->
                    val selected = activeTab == tab && !isSearchMode && !isQwertyMode
                    Surface(
                        onClick = {
                            activeTab = tab
                            isSearchMode = false
                            isQwertyMode = false
                        },
                        color = if (selected) ChemYellow else CarbonSurfaceElevated,
                        border = BorderStroke(1.dp, if (selected) ChemYellow else CarbonOutline),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("kb_tab_${tab.name.lowercase()}")
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab.shortTitle,
                                color = if (selected) CarbonBlack else PureWhite,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Search toggle
            Surface(
                onClick = {
                    isSearchMode = !isSearchMode
                    if (isSearchMode) isQwertyMode = false else searchQuery = ""
                },
                color = if (isSearchMode) ChemYellow else CarbonKeySurface,
                border = BorderStroke(1.dp, if (isSearchMode) ChemYellow else CarbonOutline),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .size(32.dp)
                    .testTag("kb_search_toggle")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isSearchMode) Icons.Filled.Close else Icons.Filled.Search,
                        contentDescription = "Search symbols",
                        tint = if (isSearchMode) CarbonBlack else ChemYellow,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // 2. Persistent Subscript / Superscript Quick Strip
        if (settings.showSubSupBar && !isSearchMode) {
            SubSupQuickBar(
                showSubscriptsFirst = showSubscriptsFirst,
                onToggleMode = { showSubscriptsFirst = !showSubscriptsFirst },
                onInsert = { insertAndRecord(it) },
                hapticEnabled = settings.hapticFeedback
            )
        }

        // Element quick-inspection banner (when long-pressing a periodic table tile)
        AnimatedVisibility(visible = inspectedElement != null) {
            inspectedElement?.let { el ->
                Surface(
                    color = CarbonSurfaceElevated,
                    border = BorderStroke(1.dp, el.category.badgeColor.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${el.atomicNumber} ${el.name} (${el.symbol}) • ${el.atomicMass}",
                                color = ChemYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${el.category.displayName} • Ox: ${el.oxidationStates} • ${el.electronConfig}",
                                color = MutedSlate,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(
                            onClick = { inspectedElement = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Close info", tint = MutedSlate, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // 3. Main Content Area (Search / ABC QWERTY / Active Chem Tab)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(CarbonBlack)
        ) {
            when {
                isSearchMode -> {
                    KeyboardSearchOverlay(
                        query = searchQuery,
                        favorites = favorites,
                        hapticEnabled = settings.hapticFeedback,
                        compact = settings.compactKeys,
                        onQueryChar = { searchQuery += it },
                        onQueryBackspace = {
                            if (searchQuery.isNotEmpty()) searchQuery = searchQuery.dropLast(1)
                        },
                        onQueryClear = { searchQuery = "" },
                        onSelectResult = { key ->
                            insertAndRecord(key.insertText)
                        },
                        onToggleFavorite = { key ->
                            repository.toggleFavorite(key.insertText)
                            onFeedbackMessage("Toggled favorite: ${key.label}")
                        }
                    )
                }

                isQwertyMode -> {
                    MiniQwertyPanel(
                        hapticEnabled = settings.hapticFeedback,
                        onInsert = { target.commitText(it) }
                    )
                }

                else -> {
                    when (activeTab) {
                        KeyboardTab.ORGANIC -> OrganicTabContent(
                            favorites = favorites,
                            hapticEnabled = settings.hapticFeedback,
                            compact = settings.compactKeys,
                            onInsert = { insertAndRecord(it) },
                            onToggleFav = { repository.toggleFavorite(it) }
                        )

                        KeyboardTab.INORGANIC -> InorganicTabContent(
                            favorites = favorites,
                            hapticEnabled = settings.hapticFeedback,
                            compact = settings.compactKeys,
                            onInsert = { insertAndRecord(it) },
                            onToggleFav = { repository.toggleFavorite(it) }
                        )

                        KeyboardTab.PHYSICAL -> PhysicalTabContent(
                            favorites = favorites,
                            hapticEnabled = settings.hapticFeedback,
                            compact = settings.compactKeys,
                            onInsert = { insertAndRecord(it) },
                            onToggleFav = { repository.toggleFavorite(it) }
                        )

                        KeyboardTab.PERIODIC_TABLE -> PeriodicTableKeyboardTab(
                            hapticEnabled = settings.hapticFeedback,
                            onInsertSymbol = { insertAndRecord(it) },
                            onInspectElement = { inspectedElement = it }
                        )

                        KeyboardTab.STRUCTURES -> StructuresTabContent(
                            favorites = favorites,
                            savedStructures = savedStructures,
                            hapticEnabled = settings.hapticFeedback,
                            compact = settings.compactKeys,
                            onInsert = { insertAndRecord(it) },
                            onToggleFav = { repository.toggleFavorite(it) },
                            onOpenEditor = { target.openStructureEditor() }
                        )

                        KeyboardTab.GREEK -> GreekTabContent(
                            favorites = favorites,
                            hapticEnabled = settings.hapticFeedback,
                            compact = settings.compactKeys,
                            onInsert = { insertAndRecord(it) },
                            onToggleFav = { repository.toggleFavorite(it) }
                        )

                        KeyboardTab.HISTORY -> HistoryTabContent(
                            recents = recents,
                            favorites = favorites,
                            clipboardHistory = clipboardHistory,
                            shortcuts = shortcuts,
                            onInsert = { insertAndRecord(it) },
                            onToggleFav = { repository.toggleFavorite(it) },
                            onClearRecents = { repository.clearRecentSymbols() },
                            onClearClipboard = { repository.clearClipboardHistory() }
                        )
                    }
                }
            }
        }

        // 4. Bottom Action Row (ABC, Undo, Redo, Copy, Paste, Space, Switch, Backspace, Enter)
        KeyboardBottomBar(
            isQwertyMode = isQwertyMode,
            hapticEnabled = settings.hapticFeedback,
            onToggleQwerty = {
                isQwertyMode = !isQwertyMode
                if (isQwertyMode) isSearchMode = false
            },
            onUndo = { target.undo() },
            onRedo = { target.redo() },
            onCopy = {
                val copied = target.copySelectionOrAll()
                if (!copied.isNullOrBlank()) {
                    repository.copyToClipboard(copied, "Copied from editor")
                    onFeedbackMessage("Copied to clipboard")
                }
            },
            onPaste = {
                val clip = repository.readSystemClipboard() ?: clipboardHistory.firstOrNull()
                if (!clip.isNullOrEmpty()) {
                    target.pasteText(clip)
                    repository.recordRecentSymbol(clip)
                }
            },
            onSpace = { target.commitText(" ") },
            onBackspace = { target.deleteBack() },
            onEnter = { target.performEnter() },
            onSwitchKeyboard = { target.switchKeyboard() }
        )
    }
}

@Composable
private fun OrganicTabContent(
    favorites: List<String>,
    hapticEnabled: Boolean,
    compact: Boolean,
    onInsert: (String) -> Unit,
    onToggleFav: (String) -> Unit
) {
    var filter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Rings", "Groups", "Symbols", "Arrows")
    val keys = when (filter) {
        "Rings" -> OrganicChemData.ringStructures
        "Groups" -> OrganicChemData.functionalGroups
        "Symbols" -> OrganicChemData.organicSymbols
        "Arrows" -> OrganicChemData.reactionArrows
        else -> OrganicChemData.allKeys
    }

    CategorizedKeyGrid(
        filters = filters,
        selectedFilter = filter,
        onSelectFilter = { filter = it },
        keys = keys,
        favorites = favorites,
        hapticEnabled = hapticEnabled,
        compact = compact,
        onInsert = onInsert,
        onToggleFav = onToggleFav
    )
}

@Composable
private fun InorganicTabContent(
    favorites: List<String>,
    hapticEnabled: Boolean,
    compact: Boolean,
    onInsert: (String) -> Unit,
    onToggleFav: (String) -> Unit
) {
    var filter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Common Ions", "Special", "All 118 Elements")

    Column(modifier = Modifier.fillMaxSize()) {
        FilterChipRow(filters = filters, selected = filter, onSelect = { filter = it })

        if (filter == "All 118 Elements") {
            val elementKeys = remember {
                PeriodicTableData.elements.map { el ->
                    ChemKey(
                        id = "inorg_el_${el.atomicNumber}",
                        label = el.symbol,
                        subLabel = "${el.atomicNumber} ${el.name}",
                        insertText = el.symbol,
                        description = el.name,
                        category = el.category.displayName,
                        isHighlighted = el.atomicNumber in listOf(1, 6, 7, 8, 11, 17, 26, 29)
                    )
                }
            }
            ChemKeyGridBody(
                keys = elementKeys,
                favorites = favorites,
                hapticEnabled = hapticEnabled,
                compact = compact,
                columns = 5,
                onInsert = onInsert,
                onToggleFav = onToggleFav
            )
        } else {
            val keys = when (filter) {
                "Common Ions" -> InorganicChemData.commonIons
                "Special" -> InorganicChemData.specialSymbols
                else -> InorganicChemData.allKeys
            }
            ChemKeyGridBody(
                keys = keys,
                favorites = favorites,
                hapticEnabled = hapticEnabled,
                compact = compact,
                columns = 4,
                onInsert = onInsert,
                onToggleFav = onToggleFav
            )
        }
    }
}

@Composable
private fun PhysicalTabContent(
    favorites: List<String>,
    hapticEnabled: Boolean,
    compact: Boolean,
    onInsert: (String) -> Unit,
    onToggleFav: (String) -> Unit
) {
    var filter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Math", "Thermo", "Electro", "Kinetics", "Solutions")
    val keys = when (filter) {
        "Math" -> PhysicalChemData.mathematicalSymbols
        "Thermo" -> PhysicalChemData.thermodynamics
        "Electro" -> PhysicalChemData.electrochemistry
        "Kinetics" -> PhysicalChemData.kinetics
        "Solutions" -> PhysicalChemData.solutions
        else -> PhysicalChemData.allKeys
    }

    CategorizedKeyGrid(
        filters = filters,
        selectedFilter = filter,
        onSelectFilter = { filter = it },
        keys = keys,
        favorites = favorites,
        hapticEnabled = hapticEnabled,
        compact = compact,
        onInsert = onInsert,
        onToggleFav = onToggleFav
    )
}

@Composable
private fun StructuresTabContent(
    favorites: List<String>,
    savedStructures: List<com.example.data.SavedStructureSummary>,
    hapticEnabled: Boolean,
    compact: Boolean,
    onInsert: (String) -> Unit,
    onToggleFav: (String) -> Unit,
    onOpenEditor: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onOpenEditor,
                color = ChemYellow,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .testTag("kb_open_structure_editor")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Draw,
                        contentDescription = "Open Structure Editor",
                        tint = CarbonBlack,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Visual Structure Editor (Draw & Export PNG)",
                        color = CarbonBlack,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        val combinedKeys = remember(savedStructures) {
            val savedKeys = savedStructures.map { st ->
                ChemKey(
                    id = "saved_${st.id}",
                    label = st.formula.ifBlank { st.name },
                    subLabel = st.name,
                    insertText = st.formula.ifBlank { st.name },
                    description = "Saved Structure: ${st.name}",
                    category = "Saved",
                    isHighlighted = true
                )
            }
            savedKeys + GreekAndTemplatesData.quickStructureTemplates + OrganicChemData.ringStructures
        }

        ChemKeyGridBody(
            keys = combinedKeys,
            favorites = favorites,
            hapticEnabled = hapticEnabled,
            compact = compact,
            columns = 3,
            onInsert = onInsert,
            onToggleFav = onToggleFav
        )
    }
}

@Composable
private fun GreekTabContent(
    favorites: List<String>,
    hapticEnabled: Boolean,
    compact: Boolean,
    onInsert: (String) -> Unit,
    onToggleFav: (String) -> Unit
) {
    var filter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Lowercase (α–ω)", "Uppercase (Α–Ω)")
    val keys = when (filter) {
        "Lowercase (α–ω)" -> GreekAndTemplatesData.greekLowercase
        "Uppercase (Α–Ω)" -> GreekAndTemplatesData.greekUppercase
        else -> GreekAndTemplatesData.greekLowercase + GreekAndTemplatesData.greekUppercase
    }

    CategorizedKeyGrid(
        filters = filters,
        selectedFilter = filter,
        onSelectFilter = { filter = it },
        keys = keys,
        favorites = favorites,
        hapticEnabled = hapticEnabled,
        compact = compact,
        columns = 5,
        onInsert = onInsert,
        onToggleFav = onToggleFav
    )
}

@Composable
private fun HistoryTabContent(
    recents: List<String>,
    favorites: List<String>,
    clipboardHistory: List<String>,
    shortcuts: List<com.example.data.CustomShortcut>,
    onInsert: (String) -> Unit,
    onToggleFav: (String) -> Unit,
    onClearRecents: () -> Unit,
    onClearClipboard: () -> Unit
) {
    var section by remember { mutableStateOf("Favorites") }
    val sections = listOf("Favorites", "Recent", "Reactions", "Formulas", "Clipboard", "Shortcuts")

    Column(modifier = Modifier.fillMaxSize()) {
        FilterChipRow(filters = sections, selected = section, onSelect = { section = it })

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            when (section) {
                "Favorites" -> {
                    if (favorites.isEmpty()) {
                        item {
                            EmptyHistoryTip("Long-press any keycap to add it to Favorites.")
                        }
                    } else {
                        item {
                            FlowSymbolRow(
                                items = favorites,
                                isFavorite = true,
                                onInsert = onInsert,
                                onToggleFav = onToggleFav
                            )
                        }
                    }
                }

                "Recent" -> {
                    if (recents.isEmpty()) {
                        item {
                            EmptyHistoryTip("Recently typed symbols and formulas appear here.")
                        }
                    } else {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Surface(
                                    onClick = onClearRecents,
                                    color = CarbonSurfaceElevated,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Filled.DeleteSweep, contentDescription = "Clear recents", tint = MutedSlate, modifier = Modifier.size(13.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Clear", color = MutedSlate, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                        item {
                            FlowSymbolRow(
                                items = recents,
                                isFavorite = false,
                                onInsert = onInsert,
                                onToggleFav = onToggleFav
                            )
                        }
                    }
                }

                "Reactions" -> {
                    items(GreekAndTemplatesData.reactionTemplates, key = { it.id }) { tpl ->
                        TemplateRowCard(
                            title = tpl.title,
                            formula = tpl.formula,
                            subtitle = tpl.description,
                            onClick = { onInsert(tpl.formula) }
                        )
                    }
                }

                "Formulas" -> {
                    items(GreekAndTemplatesData.formulaTemplates, key = { it.id }) { tpl ->
                        TemplateRowCard(
                            title = tpl.title,
                            formula = tpl.formula,
                            subtitle = tpl.description,
                            onClick = { onInsert(tpl.formula) }
                        )
                    }
                }

                "Clipboard" -> {
                    if (clipboardHistory.isEmpty()) {
                        item {
                            EmptyHistoryTip("Copied equations and formulas appear here for one-tap paste.")
                        }
                    } else {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Surface(
                                    onClick = onClearClipboard,
                                    color = CarbonSurfaceElevated,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Clear History", color = MutedSlate, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                        items(clipboardHistory) { clip ->
                            TemplateRowCard(
                                title = "Clipboard Item",
                                formula = clip,
                                subtitle = "Tap to paste",
                                onClick = { onInsert(clip) }
                            )
                        }
                    }
                }

                "Shortcuts" -> {
                    items(shortcuts, key = { it.id }) { sc ->
                        TemplateRowCard(
                            title = "Type ${sc.trigger} + Space",
                            formula = sc.expansion,
                            subtitle = "Tap to insert directly",
                            onClick = { onInsert(sc.expansion) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FlowSymbolRow(
    items: List<String>,
    isFavorite: Boolean,
    onInsert: (String) -> Unit,
    onToggleFav: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        items.chunked(4).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                rowItems.forEach { sym ->
                    Surface(
                        onClick = { onInsert(sym) },
                        color = if (isFavorite) ChemYellowMuted else CarbonKeySurface,
                        border = BorderStroke(1.dp, if (isFavorite) ChemYellow.copy(alpha = 0.6f) else CarbonOutline),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sym,
                                color = if (isFavorite) ChemYellow else PureWhite,
                                fontFamily = ScientificSymbolFontFamily,
                                fontSize = if (sym.length > 6) 11.sp else 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isFavorite) {
                                IconButton(
                                    onClick = { onToggleFav(sym) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(16.dp)
                                ) {
                                    Icon(Icons.Filled.Star, contentDescription = "Unfavorite", tint = ChemYellow, modifier = Modifier.size(10.dp))
                                }
                            }
                        }
                    }
                }
                repeat(4 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TemplateRowCard(
    title: String,
    formula: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = CarbonKeySurface,
        border = BorderStroke(1.dp, CarbonOutline),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formula,
                    color = ChemYellow,
                    fontFamily = ScientificSymbolFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$title • $subtitle",
                    color = MutedSlate,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryTip(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = MutedSlate, fontSize = 12.sp)
    }
}

@Composable
private fun CategorizedKeyGrid(
    filters: List<String>,
    selectedFilter: String,
    onSelectFilter: (String) -> Unit,
    keys: List<ChemKey>,
    favorites: List<String>,
    hapticEnabled: Boolean,
    compact: Boolean,
    columns: Int = 4,
    onInsert: (String) -> Unit,
    onToggleFav: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        FilterChipRow(filters = filters, selected = selectedFilter, onSelect = onSelectFilter)
        ChemKeyGridBody(
            keys = keys,
            favorites = favorites,
            hapticEnabled = hapticEnabled,
            compact = compact,
            columns = columns,
            onInsert = onInsert,
            onToggleFav = onToggleFav
        )
    }
}

@Composable
private fun FilterChipRow(
    filters: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        filters.forEach { item ->
            FilterPill(
                label = item,
                selected = selected == item,
                accentColor = ChemYellow,
                onClick = { onSelect(item) }
            )
        }
    }
}

@Composable
private fun ChemKeyGridBody(
    keys: List<ChemKey>,
    favorites: List<String>,
    hapticEnabled: Boolean,
    compact: Boolean,
    columns: Int,
    onInsert: (String) -> Unit,
    onToggleFav: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(keys, key = { it.id }) { chemKey ->
            ChemKeyButton(
                chemKey = chemKey,
                isFavorite = favorites.contains(chemKey.insertText),
                hapticEnabled = hapticEnabled,
                compact = compact,
                onClick = { onInsert(chemKey.insertText) },
                onLongPressFavorite = { onToggleFav(chemKey.insertText) }
            )
        }
    }
}

@Composable
private fun KeyboardSearchOverlay(
    query: String,
    favorites: List<String>,
    hapticEnabled: Boolean,
    compact: Boolean,
    onQueryChar: (String) -> Unit,
    onQueryBackspace: () -> Unit,
    onQueryClear: () -> Unit,
    onSelectResult: (ChemKey) -> Unit,
    onToggleFavorite: (ChemKey) -> Unit
) {
    val results = remember(query) {
        if (query.isBlank()) {
            OrganicChemData.ringStructures.take(4) +
                OrganicChemData.reactionArrows.take(4) +
                InorganicChemData.commonIons.take(4)
        } else {
            GreekAndTemplatesData.searchAll(query)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search query display bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .background(CarbonSurfaceElevated, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = ChemYellow, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (query.isEmpty()) "Type below to search 118 elements, rings, ions..." else query,
                    color = if (query.isEmpty()) MutedSlate else PureWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (query.isNotEmpty()) {
                    Surface(
                        onClick = onQueryClear,
                        color = CarbonKeySurface,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Clear", color = LabTeal, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                }
                Surface(
                    onClick = onQueryBackspace,
                    color = CarbonKeySurface,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Search backspace", tint = PureWhite, modifier = Modifier.padding(4.dp).size(14.dp))
                }
            }
        }

        // Results row (horizontal scrollable or 3-column short grid)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(results.take(12), key = { "sr_${it.id}_${it.label}" }) { chemKey ->
                ChemKeyButton(
                    chemKey = chemKey,
                    isFavorite = favorites.contains(chemKey.insertText),
                    hapticEnabled = hapticEnabled,
                    compact = true,
                    onClick = { onSelectResult(chemKey) },
                    onLongPressFavorite = { onToggleFavorite(chemKey) }
                )
            }
        }

        // Compact A-Z keypad to type search queries inside any IME host!
        val r1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
        val r2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
        val r3 = listOf("z", "x", "c", "v", "b", "n", "m", "2", "3", "4")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CarbonSurface)
                .padding(horizontal = 4.dp, vertical = 3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            SearchLetterRow(r1, onQueryChar)
            SearchLetterRow(r2, onQueryChar)
            SearchLetterRow(r3, onQueryChar)
        }
    }
}

@Composable
private fun SearchLetterRow(letters: List<String>, onChar: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        letters.forEach { ch ->
            Surface(
                onClick = { onChar(ch) },
                color = CarbonKeySurface,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(ch.uppercase(), color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
