package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.data.GreekAndTemplatesData
import com.example.data.KeyboardTab
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CarbonKeySurface
import com.example.ui.theme.CarbonOutline
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CarbonSurfaceElevated
import com.example.ui.theme.ChemYellow
import com.example.ui.theme.LabCoral
import com.example.ui.theme.LabTeal
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ScientificSymbolFontFamily

@Composable
fun LibraryAndSettingsScreen(
    viewModel: MainViewModel,
    onShowMessage: (String) -> Unit,
    onInsertAndOpenKeyboard: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.repository.settings.collectAsState()
    val shortcuts by viewModel.repository.customShortcuts.collectAsState()
    val clipboardHistory by viewModel.repository.clipboardHistory.collectAsState()

    var newTrigger by remember { mutableStateOf("") }
    var newExpansion by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonBlack),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "TEMPLATES, SHORTCUTS & SETTINGS",
                color = ChemYellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        // System Keyboard Setup Card
        item {
            SectionCard(title = "Android System Keyboard (IME) Setup") {
                Text(
                    text = "Use Chemistry Keyboard – Pro inside WhatsApp, Notes, Google Docs, Classroom, and any Android app (Android 10+).",
                    color = MutedSlate,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "1. Tap 'Open Keyboard Settings' and turn ON Chemistry Keyboard – Pro.\n2. Tap 'Switch Keyboard' and choose Chemistry Keyboard – Pro.",
                    color = PureWhite,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) },
                        colors = ButtonDefaults.buttonColors(containerColor = ChemYellow, contentColor = CarbonBlack),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Keyboard Settings", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.showInputMethodPicker()
                        },
                        border = BorderStroke(1.dp, ChemYellow),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Keyboard, contentDescription = null, tint = ChemYellow, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Switch Keyboard", color = ChemYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Keyboard Preferences
        item {
            SectionCard(title = "Keyboard Preferences") {
                SettingSwitchRow(
                    title = "Haptic Key Feedback",
                    subtitle = "Vibrate subtly on chemical symbol keystrokes",
                    checked = settings.hapticFeedback,
                    onCheckedChange = { enabled ->
                        viewModel.repository.updateSettings { it.copy(hapticFeedback = enabled) }
                    }
                )
                SettingSwitchRow(
                    title = "Show Subscript / Superscript Strip",
                    subtitle = "Keep ₀–₉, ⁰–⁹, ⁺, ⁻ pinned above keys",
                    checked = settings.showSubSupBar,
                    onCheckedChange = { enabled ->
                        viewModel.repository.updateSettings { it.copy(showSubSupBar = enabled) }
                    }
                )
                SettingSwitchRow(
                    title = "Compact Key Height",
                    subtitle = "Fit more chemical keys per row on smaller screens",
                    checked = settings.compactKeys,
                    onCheckedChange = { enabled ->
                        viewModel.repository.updateSettings { it.copy(compactKeys = enabled) }
                    }
                )
                Spacer(Modifier.height(6.dp))
                Text("Default Opening Tab", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(KeyboardTab.ORGANIC, KeyboardTab.INORGANIC, KeyboardTab.PHYSICAL, KeyboardTab.PERIODIC_TABLE).forEach { tab ->
                        val active = settings.defaultTab == tab
                        Surface(
                            onClick = {
                                viewModel.repository.updateSettings { it.copy(defaultTab = tab) }
                            },
                            color = if (active) ChemYellow else CarbonKeySurface,
                            border = BorderStroke(1.dp, if (active) ChemYellow else CarbonOutline),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = tab.shortTitle,
                                    color = if (active) CarbonBlack else PureWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Custom Chemical Shortcuts Creator
        item {
            SectionCard(title = "Custom Chemical Shortcuts (Type trigger + Space)") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newTrigger,
                        onValueChange = { newTrigger = it },
                        placeholder = { Text("/trigger", color = MutedSlate, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChemYellow,
                            unfocusedBorderColor = CarbonOutline,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                        ),
                        modifier = Modifier.weight(0.4f).testTag("shortcut_trigger_input")
                    )
                    OutlinedTextField(
                        value = newExpansion,
                        onValueChange = { newExpansion = it },
                        placeholder = { Text("Formula (e.g. KMnO₄)", color = MutedSlate, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChemYellow,
                            unfocusedBorderColor = CarbonOutline,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                        ),
                        modifier = Modifier.weight(0.6f).testTag("shortcut_expansion_input")
                    )
                    Surface(
                        onClick = {
                            if (newTrigger.isNotBlank() && newExpansion.isNotBlank()) {
                                viewModel.repository.addOrUpdateShortcut(newTrigger, newExpansion)
                                onShowMessage("Added shortcut ${newTrigger.trim()} → ${newExpansion.trim()}")
                                newTrigger = ""
                                newExpansion = ""
                            }
                        },
                        color = ChemYellow,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(48.dp).testTag("shortcut_add_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Add, contentDescription = "Add shortcut", tint = CarbonBlack)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                shortcuts.forEach { sc ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .background(CarbonKeySurface, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${sc.trigger}  →  ${sc.expansion}", color = PureWhite, fontFamily = ScientificSymbolFontFamily, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { viewModel.repository.deleteShortcut(sc.id) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete shortcut", tint = LabCoral, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Formula & Reaction Templates
        item {
            SectionCard(title = "Formula & Reaction Equation Templates") {
                Text("Tap Copy or Insert into Scratchpad:", color = MutedSlate, fontSize = 11.sp)
            }
        }
        items(GreekAndTemplatesData.formulaTemplates + GreekAndTemplatesData.reactionTemplates, key = { it.id }) { tpl ->
            Surface(
                color = CarbonSurface,
                border = BorderStroke(1.dp, CarbonOutline),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tpl.formula, color = ChemYellow, fontFamily = ScientificSymbolFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("${tpl.title} • ${tpl.description}", color = MutedSlate, fontSize = 11.sp)
                    }
                    Row {
                        IconButton(onClick = {
                            viewModel.repository.copyToClipboard(tpl.formula, tpl.title)
                            onShowMessage("Copied: ${tpl.formula}")
                        }) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy template", tint = PureWhite, modifier = Modifier.size(18.dp))
                        }
                        Surface(
                            onClick = { onInsertAndOpenKeyboard(tpl.formula) },
                            color = LabTeal.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, LabTeal.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Use", color = LabTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }
                }
            }
        }

        // Clipboard History
        if (clipboardHistory.isNotEmpty()) {
            item {
                SectionCard(title = "Clipboard History (${clipboardHistory.size})") {
                    clipboardHistory.take(8).forEach { clip ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .background(CarbonKeySurface, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(clip, color = PureWhite, fontFamily = ScientificSymbolFontFamily, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = {
                                    viewModel.repository.copyToClipboard(clip)
                                    onShowMessage("Copied to clipboard")
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = ChemYellow, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // APK Build Instructions (offline documentation inside Settings)
        item {
            SectionCard(title = "Android Studio & APK Build Info") {
                Text(
                    text = "Offline-first native Kotlin + Jetpack Compose InputMethodService (minSdk 24, targetSdk 36, Android 10+ verified).\n\nBuild Debug APK:\n  gradle :app:assembleDebug\n\nBuild Release APK:\n  gradle :app:assembleRelease",
                    color = MutedSlate,
                    fontSize = 11.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        color = CarbonSurface,
        border = BorderStroke(1.dp, CarbonOutline),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, color = ChemYellow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            content()
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = MutedSlate, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CarbonBlack,
                checkedTrackColor = ChemYellow,
                uncheckedThumbColor = MutedSlate,
                uncheckedTrackColor = CarbonSurfaceElevated
            )
        )
    }
}
