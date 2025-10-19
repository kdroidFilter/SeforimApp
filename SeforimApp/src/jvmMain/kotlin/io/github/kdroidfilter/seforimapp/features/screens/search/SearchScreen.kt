package io.github.kdroidfilter.seforimapp.features.screens.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.paging.PagingData
import io.github.kdroidfilter.seforimlibrary.core.models.SearchResult
import kotlinx.coroutines.flow.Flow

@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    SearchView(
        uiState = uiState,
        resultsFlow = viewModel.resultsPagingData,
        onQueryInputChange = { viewModel.onEvent(SearchEvent.QueryChanged(it)) },
        onSubmit = { query -> viewModel.onEvent(SearchEvent.Submit(query)) },
        onOpenResult = { viewModel.onEvent(SearchEvent.OpenResult(it)) },
        onScroll = { anchorId, anchorIndex, scrollIndex, scrollOffset ->
            viewModel.onEvent(
                SearchEvent.ContentScrolled(
                    anchorId = anchorId,
                    anchorIndex = anchorIndex,
                    scrollIndex = scrollIndex,
                    scrollOffset = scrollOffset
                )
            )
        }
    )
}
