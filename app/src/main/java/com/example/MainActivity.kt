package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.KeyboardStudioScreen
import com.example.ui.screens.LibraryAndSettingsScreen
import com.example.ui.screens.PeriodicTableScreen
import com.example.ui.screens.StructureEditorScreen
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CarbonSurfaceElevated
import com.example.ui.theme.ChemYellow
import com.example.ui.theme.ChemYellowMuted
import com.example.ui.theme.ChemistryKeyboardTheme
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.PureWhite
import kotlinx.coroutines.launch

private data class AppNavDestination(
    val index: Int,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(CarbonBlack.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(CarbonSurface.toArgb())
        )
        handleStartIntent(intent)

        setContent {
            ChemistryKeyboardTheme {
                ChemistryProAppRoot(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleStartIntent(intent)
    }

    private fun handleStartIntent(intent: Intent?) {
        if (intent?.getStringExtra(EXTRA_START_TAB) == "structure_editor") {
            viewModel.selectNavTab(1)
        }
    }

    companion object {
        const val EXTRA_START_TAB = "extra_start_tab"
    }
}

@Composable
private fun ChemistryProAppRoot(viewModel: MainViewModel) {
    val selectedNavIndex by viewModel.requestedNavIndex.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val destinations = remember {
        listOf(
            AppNavDestination(0, "Keyboard", Icons.Filled.Keyboard, Icons.Outlined.Keyboard, "nav_keyboard"),
            AppNavDestination(1, "Structures", Icons.Filled.Draw, Icons.Outlined.Draw, "nav_structures"),
            AppNavDestination(2, "Elements", Icons.Filled.GridView, Icons.Outlined.GridView, "nav_periodic"),
            AppNavDestination(3, "Hub", Icons.Filled.Tune, Icons.Outlined.Tune, "nav_hub")
        )
    }

    if (selectedNavIndex != 0) {
        BackHandler {
            viewModel.selectNavTab(0)
        }
    }

    fun showMessage(msg: String) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(msg)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(CarbonBlack)
    ) {
        val useNavRail = maxWidth >= 700.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = CarbonBlack,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = CarbonSurfaceElevated,
                        contentColor = ChemYellow
                    )
                }
            },
            bottomBar = {
                if (!useNavRail) {
                    NavigationBar(
                        containerColor = CarbonSurface,
                        contentColor = PureWhite
                    ) {
                        destinations.forEach { dest ->
                            val selected = selectedNavIndex == dest.index
                            NavigationBarItem(
                                selected = selected,
                                onClick = { viewModel.selectNavTab(dest.index) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = dest.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ChemYellow,
                                    selectedTextColor = ChemYellow,
                                    indicatorColor = ChemYellowMuted,
                                    unselectedIconColor = MutedSlate,
                                    unselectedTextColor = MutedSlate
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavRail) {
                    NavigationRail(
                        containerColor = CarbonSurface,
                        contentColor = PureWhite,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        destinations.forEach { dest ->
                            val selected = selectedNavIndex == dest.index
                            NavigationRailItem(
                                selected = selected,
                                onClick = { viewModel.selectNavTab(dest.index) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = dest.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ChemYellow,
                                    selectedTextColor = ChemYellow,
                                    indicatorColor = ChemYellowMuted,
                                    unselectedIconColor = MutedSlate,
                                    unselectedTextColor = MutedSlate
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val contentModifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 900.dp)

                    when (selectedNavIndex) {
                        0 -> KeyboardStudioScreen(
                            viewModel = viewModel,
                            onShowMessage = ::showMessage,
                            onNavigateToStructureEditor = { viewModel.selectNavTab(1) },
                            modifier = contentModifier
                        )
                        1 -> StructureEditorScreen(
                            viewModel = viewModel,
                            onShowMessage = ::showMessage,
                            modifier = contentModifier
                        )
                        2 -> PeriodicTableScreen(
                            viewModel = viewModel,
                            onShowMessage = ::showMessage,
                            onInsertAndOpenKeyboard = { symbol ->
                                viewModel.insertIntoScratchpad(symbol)
                                viewModel.repository.recordRecentSymbol(symbol)
                                viewModel.selectNavTab(0)
                                showMessage("Inserted $symbol into Scratchpad")
                            },
                            modifier = contentModifier
                        )
                        3 -> LibraryAndSettingsScreen(
                            viewModel = viewModel,
                            onShowMessage = ::showMessage,
                            onInsertAndOpenKeyboard = { formula ->
                                viewModel.insertIntoScratchpad(formula)
                                viewModel.repository.recordRecentSymbol(formula)
                                viewModel.selectNavTab(0)
                                showMessage("Inserted into Scratchpad")
                            },
                            modifier = contentModifier
                        )
                    }
                }
            }
        }
    }
}
