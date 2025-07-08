package io.github.kdroidfilter.seforimapp.features.screens.bookcontent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.koin.compose.viewmodel.koinViewModel
import java.awt.Cursor

@Composable
fun BookContentScreen() {
    val viewModel : BookContentViewModel = koinViewModel()
    val state = rememberBookContentState(viewModel)
    SplitLayouts(state)
}

private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun SplitLayouts(state: BookContentState) {
    Column(Modifier.fillMaxSize()) {
        val splitterState : SplitPaneState = state.splitPaneState
        HorizontalSplitPane(
            splitPaneState = splitterState
        ) {
            first(150.dp) {
                Column {

                }
            }
            second(50.dp) {

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
                            .width(5.dp)                 // hit area de 3 dp
                            .fillMaxHeight()
                            .markAsHandle()
                            .cursorForHorizontalResize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            Modifier
                                .width(1.dp)            // ligne visuelle de 1 dp
                                .fillMaxHeight()
                                .background(JewelTheme.globalColors.borders.disabled)
                        )
                    }

                }
            }
        }
    }
}