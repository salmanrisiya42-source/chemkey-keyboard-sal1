package com.example.keyboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ElementCategory
import com.example.data.PeriodicElement
import com.example.data.PeriodicTableData
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CarbonKeySurface
import com.example.ui.theme.CarbonOutline
import com.example.ui.theme.CarbonSurfaceElevated
import com.example.ui.theme.ChemYellow
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.PureWhite

@Composable
fun PeriodicTableKeyboardTab(
    hapticEnabled: Boolean,
    onInsertSymbol: (String) -> Unit,
    onInspectElement: (PeriodicElement) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<ElementCategory?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Category quick filter pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 6.dp, vertical = 4.dp),
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

        // Scrollable 18-column Periodic Table Matrix (Periods 1..7 + Lanthanides 8 + Actinides 9)
        val hScroll = rememberScrollState()
        val vScroll = rememberScrollState()
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(vScroll)
                .horizontalScroll(hScroll)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            for (period in 1..9) {
                if (period == 8) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    for (group in 1..18) {
                        val el = PeriodicTableData.elementAt(period, group)
                        if (el == null) {
                            val marker = when {
                                period == 6 && group == 3 -> "57–71"
                                period == 7 && group == 3 -> "89–103"
                                else -> null
                            }
                            if (marker != null && selectedCategory == null) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 42.dp, height = 40.dp)
                                        .background(CarbonSurfaceElevated, RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(marker, color = MutedSlate, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Spacer(modifier = Modifier.size(width = 42.dp, height = 40.dp))
                            }
                        } else {
                            val dimmed = selectedCategory != null && el.category != selectedCategory
                            CompactElementTile(
                                element = el,
                                dimmed = dimmed,
                                hapticEnabled = hapticEnabled,
                                onClick = { onInsertSymbol(el.symbol) },
                                onLongClick = { onInspectElement(el) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompactElementTile(
    element: PeriodicElement,
    dimmed: Boolean,
    hapticEnabled: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val alpha = if (dimmed) 0.28f else 1f
    val borderCol = element.category.badgeColor.copy(alpha = if (dimmed) 0.2f else 0.7f)

    Surface(
        modifier = Modifier
            .size(width = 42.dp, height = 40.dp)
            .clip(RoundedCornerShape(6.dp))
            .combinedClickable(
                onClick = {
                    if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
                onLongClick = {
                    if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick()
                }
            ),
        shape = RoundedCornerShape(6.dp),
        color = CarbonKeySurface.copy(alpha = alpha),
        border = BorderStroke(1.dp, borderCol)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            Text(
                text = element.atomicNumber.toString(),
                color = element.category.badgeColor.copy(alpha = alpha),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopStart)
            )
            Text(
                text = element.symbol,
                color = PureWhite.copy(alpha = alpha),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun FilterPill(
    label: String,
    selected: Boolean,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (selected) accentColor else CarbonSurfaceElevated,
        border = BorderStroke(1.dp, if (selected) accentColor else CarbonOutline),
        shape = RoundedCornerShape(50),
        modifier = Modifier.height(26.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (selected) CarbonBlack else PureWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
