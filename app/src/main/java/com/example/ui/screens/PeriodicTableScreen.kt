package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.data.ElementCategory
import com.example.data.PeriodicElement
import com.example.data.PeriodicTableData
import com.example.keyboard.FilterPill
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CarbonKeySurface
import com.example.ui.theme.CarbonOutline
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CarbonSurfaceElevated
import com.example.ui.theme.ChemYellow
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.PureWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodicTableScreen(
    viewModel: MainViewModel,
    onShowMessage: (String) -> Unit,
    onInsertAndOpenKeyboard: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ElementCategory?>(null) }
    var activeElement by remember { mutableStateOf<PeriodicElement?>(null) }
    val favorites by viewModel.repository.favoriteSymbols.collectAsState()

    val matchingIds = remember(searchQuery, selectedCategory) {
        val q = searchQuery.trim().lowercase()
        PeriodicTableData.elements
            .filter { el ->
                val catOk = selectedCategory == null || el.category == selectedCategory
                val queryOk = q.isEmpty() ||
                    el.name.lowercase().contains(q) ||
                    el.symbol.lowercase().contains(q) ||
                    el.atomicNumber.toString() == q
                catOk && queryOk
            }
            .map { it.atomicNumber }
            .toSet()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "PERIODIC TABLE OF ELEMENTS (118)",
            color = ChemYellow,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search element name, symbol, or atomic # (e.g. Fe, 79)", color = MutedSlate, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = ChemYellow) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear search", tint = MutedSlate)
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChemYellow,
                unfocusedBorderColor = CarbonOutline,
                focusedContainerColor = CarbonSurface,
                unfocusedContainerColor = CarbonSurface,
                focusedTextColor = PureWhite,
                unfocusedTextColor = PureWhite
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("periodic_search_field")
        )

        // Category filter pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterPill(
                label = "All (118)",
                selected = selectedCategory == null,
                accentColor = ChemYellow,
                onClick = { selectedCategory = null }
            )
            ElementCategory.entries.forEach { cat ->
                FilterPill(
                    label = cat.displayName,
                    selected = selectedCategory == cat,
                    accentColor = cat.badgeColor,
                    onClick = { selectedCategory = if (selectedCategory == cat) null else cat }
                )
            }
        }

        // Scrollable 18-Column Matrix
        val hScroll = rememberScrollState()
        val vScroll = rememberScrollState()

        Surface(
            color = CarbonSurface,
            border = BorderStroke(1.dp, CarbonOutline),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(vScroll)
                    .horizontalScroll(hScroll)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (period in 1..9) {
                    if (period == 8) Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (group in 1..18) {
                            val el = PeriodicTableData.elementAt(period, group)
                            if (el == null) {
                                val marker = when {
                                    period == 6 && group == 3 -> "57–71"
                                    period == 7 && group == 3 -> "89–103"
                                    else -> null
                                }
                                if (marker != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 58.dp, height = 62.dp)
                                            .background(CarbonSurfaceElevated, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(marker, color = MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Spacer(Modifier.size(width = 58.dp, height = 62.dp))
                                }
                            } else {
                                val highlighted = matchingIds.contains(el.atomicNumber)
                                val alpha = if (highlighted) 1f else 0.22f
                                Surface(
                                    onClick = { activeElement = el },
                                    color = CarbonKeySurface.copy(alpha = alpha),
                                    border = BorderStroke(
                                        width = if (highlighted && (searchQuery.isNotEmpty() || selectedCategory != null)) 1.5.dp else 1.dp,
                                        color = el.category.badgeColor.copy(alpha = if (highlighted) 0.85f else 0.2f)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .size(width = 58.dp, height = 62.dp)
                                        .testTag("pt_el_${el.atomicNumber}")
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(4.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = el.atomicNumber.toString(),
                                            color = el.category.badgeColor.copy(alpha = alpha),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = el.symbol,
                                            color = PureWhite.copy(alpha = alpha),
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(
                                            text = el.name,
                                            color = MutedSlate.copy(alpha = alpha),
                                            fontSize = 8.sp,
                                            maxLines = 1
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

    // Element Detail Sheet
    activeElement?.let { el ->
        val isFav = favorites.contains(el.symbol)
        ModalBottomSheet(
            onDismissRequest = { activeElement = null },
            containerColor = CarbonSurface,
            contentColor = PureWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = el.category.badgeColor.copy(alpha = 0.16f),
                            border = BorderStroke(1.5.dp, el.category.badgeColor),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = el.symbol,
                                    color = el.category.badgeColor,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "${el.atomicNumber}. ${el.name}",
                                color = PureWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = el.category.displayName,
                                color = el.category.badgeColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.repository.toggleFavorite(el.symbol) }) {
                        Icon(
                            imageVector = if (isFav) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = "Favorite element",
                            tint = ChemYellow
                        )
                    }
                }

                Surface(
                    color = CarbonSurfaceElevated,
                    border = BorderStroke(1.dp, CarbonOutline),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ElementSpecRow("Atomic Mass", "${el.atomicMass} u")
                        ElementSpecRow("Electron Configuration", el.electronConfig)
                        ElementSpecRow("Oxidation States", el.oxidationStates)
                        ElementSpecRow("Electronegativity", el.electronegativity)
                        ElementSpecRow("Room Temp. State", el.stateAtRoomTemp)
                        ElementSpecRow("Group / Period", "Group ${el.group} • Period ${el.period}")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.repository.copyToClipboard(el.symbol, el.name)
                            onShowMessage("Copied ${el.symbol} to clipboard")
                        },
                        border = BorderStroke(1.dp, CarbonOutline),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Copy ${el.symbol}", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            onInsertAndOpenKeyboard(el.symbol)
                            activeElement = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChemYellow,
                            contentColor = CarbonBlack
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Insert ${el.symbol}", fontWeight = FontWeight.ExtraBold)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ElementSpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MutedSlate, fontSize = 12.sp)
        Text(value, color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
