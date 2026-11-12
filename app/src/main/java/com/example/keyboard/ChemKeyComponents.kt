package com.example.keyboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChemKey
import com.example.data.GreekAndTemplatesData
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CarbonKeyPressed
import com.example.ui.theme.CarbonKeySurface
import com.example.ui.theme.CarbonOutline
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CarbonSurfaceElevated
import com.example.ui.theme.ChemYellow
import com.example.ui.theme.ChemYellowMuted
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ScientificSymbolFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChemKeyButton(
    chemKey: ChemKey,
    isFavorite: Boolean,
    hapticEnabled: Boolean,
    compact: Boolean,
    onClick: () -> Unit,
    onLongPressFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(stiffness = 700f),
        label = "key_scale"
    )

    val containerColor = when {
        isPressed -> CarbonKeyPressed
        chemKey.isHighlighted -> ChemYellowMuted
        else -> CarbonKeySurface
    }
    val borderColor = when {
        chemKey.isHighlighted -> ChemYellow.copy(alpha = 0.72f)
        else -> CarbonOutline
    }
    val primaryTextColor = if (chemKey.isHighlighted) ChemYellow else PureWhite

    Surface(
        modifier = modifier
            .scale(scale)
            .height(if (compact) 46.dp else 52.dp)
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(color = ChemYellow.copy(alpha = 0.25f)),
                onClick = {
                    if (hapticEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    onClick()
                },
                onLongClick = {
                    if (hapticEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onLongPressFavorite()
                }
            )
            .testTag("key_${chemKey.id}"),
        shape = RoundedCornerShape(10.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isFavorite) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Favorite",
                    tint = ChemYellow,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(10.dp)
                )
            }

            if (chemKey.ringType != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    RingGlyphIcon(
                        ringType = chemKey.ringType,
                        strokeColor = PureWhite,
                        accentColor = ChemYellow,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = chemKey.subLabel ?: chemKey.label,
                        color = primaryTextColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = chemKey.label,
                        color = primaryTextColor,
                        fontFamily = ScientificSymbolFontFamily,
                        fontSize = when {
                            chemKey.label.length > 10 -> 11.sp
                            chemKey.label.length > 5 -> 13.sp
                            else -> 16.sp
                        },
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!chemKey.subLabel.isNullOrBlank()) {
                        Text(
                            text = chemKey.subLabel,
                            color = MutedSlate,
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubSupQuickBar(
    showSubscriptsFirst: Boolean,
    onToggleMode: () -> Unit,
    onInsert: (String) -> Unit,
    hapticEnabled: Boolean
) {
    val haptic = LocalHapticFeedback.current
    val keys = if (showSubscriptsFirst) {
        GreekAndTemplatesData.subscripts + GreekAndTemplatesData.superscripts.takeLast(2)
    } else {
        GreekAndTemplatesData.superscripts
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CarbonSurface)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            onClick = {
                if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onToggleMode()
            },
            color = ChemYellow,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .height(32.dp)
                .testTag("toggle_sub_sup")
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (showSubscriptsFirst) "X₂" else "X²⁺",
                    color = CarbonBlack,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            keys.forEach { item ->
                val isCharge = item.insertText == "⁺" || item.insertText == "⁻"
                Surface(
                    onClick = {
                        if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onInsert(item.insertText)
                    },
                    color = if (isCharge) ChemYellowMuted else CarbonSurfaceElevated,
                    border = BorderStroke(
                        1.dp,
                        if (isCharge) ChemYellow.copy(alpha = 0.65f) else CarbonOutline
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .widthIn(min = 32.dp)
                        .testTag("subsup_${item.id}")
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.label,
                            color = if (isCharge) ChemYellow else PureWhite,
                            fontFamily = ScientificSymbolFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiniQwertyPanel(
    hapticEnabled: Boolean,
    onInsert: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var isShift by remember { mutableStateOf(true) }
    val numRow = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val row1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val row2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val row3 = listOf("z", "x", "c", "v", "b", "n", "m")
    val opsRow = listOf("+", "−", "=", "(", ")", "[", "]", "/", ".", ",")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        QwertyKeyRow(numRow, false, hapticEnabled, onInsert)
        QwertyKeyRow(row1, isShift, hapticEnabled, onInsert)
        QwertyKeyRow(row2, isShift, hapticEnabled, onInsert)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                onClick = {
                    if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    isShift = !isShift
                },
                color = if (isShift) ChemYellow else CarbonKeySurface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1.3f)
                    .height(38.dp)
                    .testTag("qwerty_shift")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "⇧",
                        color = if (isShift) CarbonBlack else PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
            row3.forEach { ch ->
                val display = if (isShift) ch.uppercase() else ch
                Surface(
                    onClick = {
                        if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onInsert(display)
                    },
                    color = CarbonKeySurface,
                    border = BorderStroke(1.dp, CarbonOutline),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = display,
                            color = PureWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
        QwertyKeyRow(opsRow, false, hapticEnabled, onInsert)
    }
}

@Composable
private fun QwertyKeyRow(
    keys: List<String>,
    uppercase: Boolean,
    hapticEnabled: Boolean,
    onInsert: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        keys.forEach { ch ->
            val display = if (uppercase) ch.uppercase() else ch
            Surface(
                onClick = {
                    if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onInsert(display)
                },
                color = CarbonKeySurface,
                border = BorderStroke(1.dp, CarbonOutline),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = display,
                        color = PureWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun KeyboardBottomBar(
    isQwertyMode: Boolean,
    hapticEnabled: Boolean,
    onToggleQwerty: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onSpace: () -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onSwitchKeyboard: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CarbonSurface)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ABC / Chem toggle
        Surface(
            onClick = {
                if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onToggleQwerty()
            },
            color = if (isQwertyMode) ChemYellow else CarbonKeySurface,
            border = BorderStroke(1.dp, if (isQwertyMode) ChemYellow else CarbonOutline),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .height(42.dp)
                .testTag("bottom_abc_toggle")
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isQwertyMode) "CHEM" else "ABC",
                    color = if (isQwertyMode) CarbonBlack else PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        // Undo
        BottomIconKey(
            icon = Icons.AutoMirrored.Filled.Undo,
            contentDescription = "Undo",
            tag = "bottom_undo",
            hapticEnabled = hapticEnabled,
            onClick = onUndo
        )

        // Redo
        BottomIconKey(
            icon = Icons.AutoMirrored.Filled.Redo,
            contentDescription = "Redo",
            tag = "bottom_redo",
            hapticEnabled = hapticEnabled,
            onClick = onRedo
        )

        // Copy
        BottomIconKey(
            icon = Icons.Filled.ContentCopy,
            contentDescription = "Copy",
            tag = "bottom_copy",
            hapticEnabled = hapticEnabled,
            onClick = onCopy
        )

        // Paste
        BottomIconKey(
            icon = Icons.Filled.ContentPaste,
            contentDescription = "Paste",
            tag = "bottom_paste",
            hapticEnabled = hapticEnabled,
            onClick = onPaste
        )

        // Space bar
        Surface(
            onClick = {
                if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSpace()
            },
            color = CarbonKeySurface,
            border = BorderStroke(1.dp, CarbonOutline),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .testTag("bottom_space")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "SPACE",
                    color = MutedSlate,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        // Switch IME
        BottomIconKey(
            icon = Icons.Filled.Keyboard,
            contentDescription = "Switch Keyboard",
            tag = "bottom_switch_ime",
            hapticEnabled = hapticEnabled,
            onClick = onSwitchKeyboard
        )

        // Backspace with hold-to-repeat
        Surface(
            color = CarbonKeySurface,
            border = BorderStroke(1.dp, CarbonOutline),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(46.dp)
                .height(42.dp)
                .pointerInput(hapticEnabled) {
                    detectTapGestures(
                        onTap = {
                            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onBackspace()
                        },
                        onLongPress = {
                            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val job = scope.launch {
                                while (true) {
                                    onBackspace()
                                    delay(65)
                                }
                            }
                            // Wait until pointer released via tryAwaitRelease not needed with separate press?
                            // pointerInput cancel handles release if we use detectTapGestures onPress!
                            job.cancel()
                        },
                        onPress = {
                            val job = scope.launch {
                                delay(380)
                                while (true) {
                                    if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onBackspace()
                                    delay(65)
                                }
                            }
                            tryAwaitRelease()
                            job.cancel()
                        }
                    )
                }
                .testTag("bottom_backspace")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = PureWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Enter (Yellow Highlighted)
        Surface(
            onClick = {
                if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onEnter()
            },
            color = ChemYellow,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(48.dp)
                .height(42.dp)
                .testTag("bottom_enter")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardReturn,
                    contentDescription = "Enter",
                    tint = CarbonBlack,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun BottomIconKey(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    tag: String,
    hapticEnabled: Boolean,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Surface(
        onClick = {
            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        color = CarbonKeySurface,
        border = BorderStroke(1.dp, CarbonOutline),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .size(width = 36.dp, height = 42.dp)
            .testTag(tag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = PureWhite,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}
