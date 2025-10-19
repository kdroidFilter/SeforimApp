package io.github.kdroidfilter.seforimapp.features.bookcontent.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import io.github.kdroidfilter.seforimapp.core.presentation.utils.cursorForHorizontalResize
import io.github.kdroidfilter.seforimapp.core.presentation.utils.cursorForVerticalResize

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun EnhancedHorizontalSplitPane(
    splitPaneState: SplitPaneState,
    modifier: Modifier = Modifier,
    firstMinSize: Float = 200f,
    secondMinSize: Float = 200f,
    firstContent: @Composable BoxScope.() -> Unit,
    secondContent: (@Composable BoxScope.() -> Unit)?,
    showSplitter: Boolean = true
) {
    val effectiveSecondMin = if (secondContent == null) 0f else secondMinSize

    // When the second pane is hidden, expand the first to 100% to avoid blank space
    LaunchedEffect(secondContent == null) {
        if (secondContent == null) {
            splitPaneState.positionPercentage = 1f
        }
    }

    HorizontalSplitPane(
        splitPaneState = splitPaneState,
        modifier = modifier
    ) {
        first(firstMinSize.dp) {
            Box(
                modifier = Modifier.fillMaxSize(),
                content = firstContent
            )
        }
        second(effectiveSecondMin.dp) {
            if (secondContent != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    content = secondContent
                )
            } else {
                // Keep the pane structure stable even when hidden
                Box(modifier = Modifier.fillMaxSize())
            }
        }
        if (showSplitter && secondContent != null) {
            splitter {
                visiblePart {
                    Divider(
                        Orientation.Vertical,
                        Modifier.fillMaxHeight().width(1.dp),
                        color = JewelTheme.globalColors.borders.disabled
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
                    ) {}
                }
            }
        }
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun EnhancedVerticalSplitPane(
    splitPaneState: SplitPaneState,
    modifier: Modifier = Modifier,
    firstMinSize: Float = 200f,
    secondMinSize: Float = 200f,
    firstContent: @Composable BoxScope.() -> Unit,
    secondContent: (@Composable BoxScope.() -> Unit)?,
    showSplitter: Boolean = true
) {
    val effectiveSecondMin = if (secondContent == null) 0f else secondMinSize

    // When the second pane is hidden, expand the first to 100% to avoid blank space
    LaunchedEffect(secondContent == null) {
        if (secondContent == null) {
            splitPaneState.positionPercentage = 1f
        }
    }

    VerticalSplitPane(
        splitPaneState = splitPaneState,
        modifier = modifier
    ) {
        first(firstMinSize.dp) {
            Box(
                modifier = Modifier.fillMaxSize(),
                content = firstContent
            )
        }
        second(effectiveSecondMin.dp) {
            if (secondContent != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    content = secondContent
                )
            } else {
                // Keep the pane structure stable even when hidden
                Box(modifier = Modifier.fillMaxSize())
            }
        }
        if (showSplitter && secondContent != null) {
            splitter {
                visiblePart {
                    Divider(
                        Orientation.Horizontal,
                        Modifier.fillMaxWidth().height(1.dp),
                        color = JewelTheme.globalColors.borders.disabled
                    )
                }
                handle {
                    Box(
                        Modifier
                            .height(5.dp)
                            .fillMaxWidth()
                            .markAsHandle()
                            .cursorForVerticalResize(),
                        contentAlignment = Alignment.Center
                    ) {}
                }
            }
        }
    }
}
