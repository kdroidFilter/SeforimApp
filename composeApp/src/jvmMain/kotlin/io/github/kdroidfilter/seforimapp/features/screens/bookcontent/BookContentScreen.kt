package io.github.kdroidfilter.seforimapp.features.screens.bookcontent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.jewel.foundation.theme.JewelTheme
import java.awt.Cursor

@Composable
fun BookContentScreen() {
    SplitLayouts()
}

private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

/**
 * A Saver to store SplitPaneState's positionFraction.
 */
@OptIn(ExperimentalSplitPaneApi::class)
val SplitPaneStateSaver = listSaver<SplitPaneState, Any>(
    save = { state ->
        listOf(state.positionPercentage, state.moveEnabled)
    },
    restore = { savedList ->
        SplitPaneState(
            initialPositionPercentage = savedList[0] as Float,
            moveEnabled = savedList[1] as Boolean
        )
    }
)


/**
 * Saveable version of rememberSplitPaneState.
 */
@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun rememberSaveableSplitPaneState(
    initialPositionPercentage: Float = 0f,
    moveEnabled: Boolean = true
): SplitPaneState {
    return rememberSaveable(saver = SplitPaneStateSaver) {
        SplitPaneState(
            moveEnabled = moveEnabled,
            initialPositionPercentage = initialPositionPercentage
        )
    }
}
@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun SplitLayouts() {
    Column(Modifier.fillMaxSize()) {
        val splitterState =  rememberSaveableSplitPaneState()
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