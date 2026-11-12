package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.MainViewModel
import com.example.keyboard.ChemistryKeyboardView
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CarbonOutline
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CarbonSurfaceElevated
import com.example.ui.theme.ChemYellow
import com.example.ui.theme.ChemYellowMuted
import com.example.ui.theme.LabEmerald
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ScientificSymbolFontFamily
import kotlin.math.roundToInt

@Composable
fun KeyboardStudioScreen(
    viewModel: MainViewModel,
    onShowMessage: (String) -> Unit,
    onNavigateToStructureEditor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imeStatus by viewModel.imeStatus.collectAsState()
    val scratchpadText by viewModel.scratchpadText.collectAsState()

    val keyboardTarget = remember(viewModel) {
        viewModel.createScratchpadTarget(onOpenEditor = onNavigateToStructureEditor)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshImeStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonBlack)
    ) {
        val availableHeightDp = maxHeight.value
        val isUltraCompact = availableHeightDp < 440f
        val keyboardHeightDp = if (isUltraCompact) {
            (availableHeightDp * 0.48f).roundToInt().coerceIn(165, 220)
        } else {
            (availableHeightDp * 0.52f).roundToInt().coerceIn(240, 326)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Header + System IME Activation Banner + Scratchpad
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = if (isUltraCompact) 4.dp else 6.dp),
                verticalArrangement = Arrangement.spacedBy(if (isUltraCompact) 4.dp else 6.dp)
            ) {
                // System IME Status Card
                Surface(
                    color = if (imeStatus.isSelected) CarbonSurfaceElevated else ChemYellowMuted.copy(alpha = 0.7f),
                    border = BorderStroke(
                        1.dp,
                        if (imeStatus.isSelected) LabEmerald.copy(alpha = 0.6f) else ChemYellow.copy(alpha = 0.75f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = if (isUltraCompact) 4.dp else 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (imeStatus.isSelected) Icons.Filled.CheckCircle else Icons.Filled.Keyboard,
                                contentDescription = null,
                                tint = if (imeStatus.isSelected) LabEmerald else ChemYellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = when {
                                        imeStatus.isSelected -> "System Keyboard Active"
                                        imeStatus.isEnabled -> "Keyboard Enabled • Switch Now"
                                        else -> "Enable System Chemistry Keyboard"
                                    },
                                    color = PureWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (!isUltraCompact) {
                                    Text(
                                        text = when {
                                            imeStatus.isSelected -> "Ready in Notes, WhatsApp, Docs & all apps."
                                            imeStatus.isEnabled -> "Tap Switch to activate everywhere."
                                            else -> "Enable in Android Keyboard Settings."
                                        },
                                        color = MutedSlate,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(6.dp))

                        if (!imeStatus.isEnabled) {
                            Button(
                                onClick = {
                                    context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ChemYellow,
                                    contentColor = CarbonBlack
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("enable_ime_button")
                            ) {
                                Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Enable", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        } else if (!imeStatus.isSelected) {
                            Button(
                                onClick = {
                                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                    imm?.showInputMethodPicker()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ChemYellow,
                                    contentColor = CarbonBlack
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("switch_ime_button")
                            ) {
                                Text("Switch", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                    imm?.showInputMethodPicker()
                                },
                                border = BorderStroke(1.dp, LabEmerald.copy(alpha = 0.7f)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Switch", color = LabEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Live Chemical Equation Scratchpad
                Surface(
                    color = CarbonSurface,
                    border = BorderStroke(1.dp, CarbonOutline),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 96.dp)
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Scratchpad Toolbar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CarbonSurfaceElevated)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "EQUATION SCRATCHPAD",
                                    color = ChemYellow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "Long-press key to ★ favorite • Try /bz + Space",
                                    color = MutedSlate,
                                    fontSize = 9.sp
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                IconButton(
                                    onClick = {
                                        if (scratchpadText.isNotBlank()) {
                                            viewModel.repository.toggleFavorite(scratchpadText.trim())
                                            onShowMessage("Saved scratchpad line to Favorites")
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Filled.StarBorder, contentDescription = "Favorite equation", tint = ChemYellow, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = {
                                        if (viewModel.repository.copyToClipboard(scratchpadText, "Chemistry Equation")) {
                                            onShowMessage("Copied equation to clipboard")
                                        }
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("scratchpad_copy_button")
                                ) {
                                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy all", tint = PureWhite, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = {
                                        if (scratchpadText.isNotBlank()) {
                                            val share = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_TEXT, scratchpadText)
                                            }
                                            context.startActivity(Intent.createChooser(share, "Share Chemistry Equation"))
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Filled.Share, contentDescription = "Share equation", tint = PureWhite, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { viewModel.clearScratchpad() },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("scratchpad_clear_button")
                                ) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear scratchpad", tint = MutedSlate, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // Equation Display Canvas
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(10.dp)
                        ) {
                            if (scratchpadText.isEmpty()) {
                                Text(
                                    text = "Tap keys below to compose chemical formulas, organic rings, reactions, and physical chemistry equations...",
                                    color = MutedSlate.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            } else {
                                Text(
                                    text = scratchpadText + "▌",
                                    color = PureWhite,
                                    fontFamily = ScientificSymbolFontFamily,
                                    fontSize = 17.sp,
                                    lineHeight = 25.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.testTag("scratchpad_output")
                                )
                            }
                        }
                    }
                }
            }

            // Embedded Interactive 7-Tab Chemistry Keyboard
            Surface(
                color = CarbonBlack,
                border = BorderStroke(1.dp, CarbonOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                ChemistryKeyboardView(
                    repository = viewModel.repository,
                    target = keyboardTarget,
                    heightDp = keyboardHeightDp,
                    onFeedbackMessage = onShowMessage
                )
            }
        }
    }
}
