package io.github.kdroidfilter.seforimapp.features.screens.bookcontent

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneState

data class BookContentState @OptIn(ExperimentalSplitPaneApi::class) constructor(
    val splitPaneState: SplitPaneState,
    val searchText: String,
    val scrollPosition: Int,
    val selectedChapter: Int,
)

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun rememberBookContentState(viewModel: BookContentViewModel)  : BookContentState {
    return BookContentState(
        splitPaneState = viewModel.splitPaneState.collectAsState().value,
        searchText = viewModel.searchText.collectAsState().value,
        scrollPosition = viewModel.scrollPosition.collectAsState().value,
        selectedChapter = viewModel.selectedChapter.collectAsState().value,
    )
}