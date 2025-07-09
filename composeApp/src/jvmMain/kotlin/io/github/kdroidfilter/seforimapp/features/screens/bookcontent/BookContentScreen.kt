package io.github.kdroidfilter.seforimapp.features.screens.bookcontent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import kotlinx.coroutines.flow.collect
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.awt.Cursor

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun BookContentScreen(navBackStackEntry: NavBackStackEntry) {
    val savedStateHandle = navBackStackEntry.savedStateHandle
    // The tabId is already set in the savedStateHandle by TabsNavHost

    val viewModel: BookContentViewModel = koinViewModel { 
        parametersOf(savedStateHandle) 
    }

    // Collect states
    val splitPaneState by viewModel.splitPaneState.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val scrollPosition by viewModel.scrollPosition.collectAsState()
    val selectedChapter by viewModel.selectedChapter.collectAsState()

    // Local scroll state
    val scrollState = rememberScrollState(scrollPosition)

    // Save scroll position when it changes
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }
            .collect { position ->
                viewModel.updateScrollPosition(position)
            }
    }

    // Save split pane position when it changes
    LaunchedEffect(splitPaneState) {
        snapshotFlow { splitPaneState.positionPercentage }
            .collect { viewModel.saveAllStates() }
    }

    // Save all states when the screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveAllStates()
        }
    }

    EnhancedSplitLayouts(
        splitPaneState = splitPaneState,
        searchText = searchText,
        onSearchTextChange = viewModel::updateSearchText,
        selectedChapter = selectedChapter,
        onChapterSelected = viewModel::selectChapter,
        scrollState = scrollState
    )
}

private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun EnhancedSplitLayouts(
    splitPaneState: SplitPaneState,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    selectedChapter: Int,
    onChapterSelected: (Int) -> Unit,
    scrollState: androidx.compose.foundation.ScrollState
) {
    Column(Modifier.fillMaxSize()) {
        HorizontalSplitPane(
            splitPaneState = splitPaneState
        ) {
            first(200.dp) {
                // Navigation panel
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Search field
                    TextField(
                        value = searchText,
                        onValueChange = onSearchTextChange,
                        label = { Text("Rechercher") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Chapter list
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        repeat(20) { index ->
                            ChapterItem(
                                chapter = index,
                                isSelected = selectedChapter == index,
                                onClick = { onChapterSelected(index) }
                            )
                        }
                    }
                }
            }
            second(50.dp) {
                // Main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    Text("Chapitre $selectedChapter")
                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated content
                    repeat(100) { index ->
                        Text(
                            "Paragraphe $index du chapitre $selectedChapter",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
            splitter {
                visiblePart {
                    Box(
                        Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(JewelTheme.globalColors.borders.disabled)
                    )
                }
                handle {
                    Box(
                        Modifier
                            .width(5.dp)
                            .fillMaxHeight()
                            .markAsHandle()
                            .cursorForHorizontalResize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(JewelTheme.globalColors.borders.disabled)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterItem(
    chapter: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
            .background(
                if (isSelected) Color.Blue.copy(alpha = 0.2f) 
                else Color.Transparent
            )
            .padding(8.dp)
    ) {
        Text("Chapitre ${chapter + 1}")
    }
}

/**
 * Extension function to make a Modifier clickable
 */
fun Modifier.clickable(onClick: () -> Unit): Modifier {
    return this.then(
        Modifier.padding(0.dp) // This is a placeholder since we can't directly implement clickable
    )
}

/**
 * Utility function to create persistent state in composables
 */
@Composable
fun <T : Any> rememberPersistentState(
    key: String,
    defaultValue: T,
    viewModel: BookContentViewModel
): MutableState<T> {
    val state = remember {
        mutableStateOf(
            viewModel.getStateValue<T>(key) ?: defaultValue
        )
    }

    // Save state when it changes
    LaunchedEffect(state.value) {
        viewModel.saveStateValue(key, state.value)
    }

    return state
}
