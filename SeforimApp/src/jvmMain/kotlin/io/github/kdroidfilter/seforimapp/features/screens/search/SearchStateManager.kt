package io.github.kdroidfilter.seforimapp.features.screens.search

import io.github.kdroidfilter.seforim.tabs.TabStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SearchStateManager(
    private val tabId: String,
    private val tabStateManager: TabStateManager
) {
    private val _state = MutableStateFlow(loadInitialState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private fun loadInitialState(): SearchUiState {
        val queryInput = getState<String>(SearchStateKeys.QUERY_INPUT) ?: ""
        val precision = getState<Int>(SearchStateKeys.PRECISION) ?: 2
        val anchorId = getState<Long>(SearchStateKeys.ANCHOR_ID) ?: -1L
        val anchorIndex = getState<Int>(SearchStateKeys.ANCHOR_INDEX) ?: 0
        val scrollIndex = getState<Int>(SearchStateKeys.SCROLL_INDEX) ?: 0
        val scrollOffset = getState<Int>(SearchStateKeys.SCROLL_OFFSET) ?: 0

        // "query" and "precision" are initialized by the ViewModel from route args
        return SearchUiState(
            query = "",
            precision = precision,
            queryInput = queryInput,
            anchorId = anchorId,
            anchorIndex = anchorIndex,
            scrollIndex = scrollIndex,
            scrollOffset = scrollOffset
        )
    }

    fun updateQueryInput(text: String, save: Boolean = true) {
        _state.update { it.copy(queryInput = text) }
        if (save) saveState(SearchStateKeys.QUERY_INPUT, text)
    }

    fun updateQuery(text: String) {
        _state.update { it.copy(query = text) }
    }

    fun updatePrecision(value: Int, save: Boolean = true) {
        _state.update { it.copy(precision = value) }
        if (save) saveState(SearchStateKeys.PRECISION, value)
    }

    fun updateScroll(anchorId: Long, anchorIndex: Int, scrollIndex: Int, scrollOffset: Int) {
        _state.update {
            it.copy(
                anchorId = anchorId,
                anchorIndex = anchorIndex,
                scrollIndex = scrollIndex,
                scrollOffset = scrollOffset
            )
        }
        saveState(SearchStateKeys.ANCHOR_ID, anchorId)
        saveState(SearchStateKeys.ANCHOR_INDEX, anchorIndex)
        saveState(SearchStateKeys.SCROLL_INDEX, scrollIndex)
        saveState(SearchStateKeys.SCROLL_OFFSET, scrollOffset)
    }

    fun saveAll() {
        val s = _state.value
        saveState(SearchStateKeys.QUERY_INPUT, s.queryInput)
        saveState(SearchStateKeys.ANCHOR_ID, s.anchorId)
        saveState(SearchStateKeys.ANCHOR_INDEX, s.anchorIndex)
        saveState(SearchStateKeys.SCROLL_INDEX, s.scrollIndex)
        saveState(SearchStateKeys.SCROLL_OFFSET, s.scrollOffset)
    }

    private inline fun <reified T> getState(key: String): T? = tabStateManager.getState(tabId, key)
    private fun saveState(key: String, value: Any) = tabStateManager.saveState(tabId, key, value)
}
