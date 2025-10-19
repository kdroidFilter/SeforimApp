package io.github.kdroidfilter.seforimapp.features.screens.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.github.kdroidfilter.seforim.navigation.Navigator
import io.github.kdroidfilter.seforim.tabs.TabAwareViewModel
import io.github.kdroidfilter.seforim.tabs.TabStateManager
import io.github.kdroidfilter.seforim.tabs.TabTitleUpdateManager
import io.github.kdroidfilter.seforim.tabs.TabType
import io.github.kdroidfilter.seforim.tabs.TabsDestination
import io.github.kdroidfilter.seforimapp.features.screens.bookcontent.state.StateKeys
import io.github.kdroidfilter.seforimapp.pagination.PagingDefaults
import io.github.kdroidfilter.seforimapp.pagination.SearchResultsPagingSource
import io.github.kdroidfilter.seforimlibrary.core.models.SearchResult
import io.github.kdroidfilter.seforimlibrary.dao.repository.SeforimRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class SearchViewModel(
    savedStateHandle: SavedStateHandle,
    private val tabStateManager: TabStateManager,
    private val repository: SeforimRepository,
    private val titleUpdateManager: TabTitleUpdateManager,
    private val navigator: Navigator
) : TabAwareViewModel(
    tabId = savedStateHandle.get<String>(StateKeys.TAB_ID) ?: "",
    stateManager = tabStateManager
) {

    private val currentTabId: String = savedStateHandle.get<String>(StateKeys.TAB_ID) ?: ""
    private val stateManager = SearchStateManager(currentTabId, tabStateManager)

    private val _resultsPagingData = MutableStateFlow<Flow<PagingData<SearchResult>>?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val resultsPagingData: Flow<PagingData<SearchResult>> = _resultsPagingData
        .filterNotNull()
        .flatMapLatest { it }

    val uiState = stateManager.state

    init {
        initialize(savedStateHandle)
    }

    private fun initialize(savedStateHandle: SavedStateHandle) {
        val routeQuery = savedStateHandle.get<String>("searchQuery") ?: ""
        val routePrecision = savedStateHandle.get<Int>("precision") ?: 2

        // Initialize core values from route
        stateManager.updateQuery(routeQuery)
        val persistedPrecision = tabStateManager.getState<Int>(currentTabId, SearchStateKeys.PRECISION)
        val effectivePrecision = persistedPrecision ?: routePrecision
        stateManager.updatePrecision(effectivePrecision)

        // Pre-fill queryInput with saved value or route query
        if (uiState.value.queryInput.isBlank()) {
            stateManager.updateQueryInput(routeQuery, save = false)
        }

        // Build initial pager using saved scroll position for performance
        val initialIndex = when {
            uiState.value.anchorId != -1L -> uiState.value.anchorIndex
            uiState.value.scrollIndex > 0 -> uiState.value.scrollIndex
            else -> 0
        }
        val pageSize = PagingDefaults.SEARCH.PAGE_SIZE
        val initialKey: Int? = if (initialIndex > 0) initialIndex / pageSize else null
        rebuildPager(routeQuery, effectivePrecision, initialKey)

        // Initial tab title
        titleUpdateManager.updateTabTitle(currentTabId, routeQuery.ifBlank { "Search" }, TabType.SEARCH)
    }

    private fun rebuildPager(query: String, precision: Int, initialKey: Int? = null) {
        _resultsPagingData.value = Pager(
            config = PagingDefaults.SEARCH.config(placeholders = false),
            initialKey = initialKey,
            pagingSourceFactory = {
                SearchResultsPagingSource(
                    repository = repository,
                    query = query,
                    precision = precision
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                // Only update the input; do not trigger search until Submit
                stateManager.updateQueryInput(event.text)
                // Do not rebuild pager or change title yet to avoid any reloads
            }
            is SearchEvent.Submit -> {
                // Apply the submitted text as the active query and rebuild from the start
                val newQuery = event.query
                stateManager.updateQuery(newQuery)
                // Reset scroll/anchor for a fresh result list
                stateManager.updateScroll(anchorId = -1L, anchorIndex = 0, scrollIndex = 0, scrollOffset = 0)
                titleUpdateManager.updateTabTitle(currentTabId, newQuery.ifBlank { "Search" }, TabType.SEARCH)
                rebuildPager(newQuery, uiState.value.precision, initialKey = null)
                // Update the tab's route so that TabsNavHost navigations stay in sync with the applied query
                viewModelScope.launch {
                    navigator.navigate(
                        TabsDestination.Search(
                            searchQuery = newQuery,
                            precision = uiState.value.precision,
                            tabId = currentTabId
                        )
                    )
                }
            }
            is SearchEvent.PrecisionChanged -> {
                stateManager.updatePrecision(event.value)
                rebuildPager(uiState.value.query, uiState.value.precision, initialKey = null)
            }
            is SearchEvent.ContentScrolled -> {
                stateManager.updateScroll(
                    anchorId = event.anchorId,
                    anchorIndex = event.anchorIndex,
                    scrollIndex = event.scrollIndex,
                    scrollOffset = event.scrollOffset
                )
            }
            is SearchEvent.OpenResult -> {
                viewModelScope.launch {
                    navigator.navigate(
                        TabsDestination.BookContent(
                            bookId = event.result.bookId,
                            tabId = currentTabId,
                            lineId = event.result.lineId
                        )
                    )
                }
            }
            SearchEvent.SaveState -> stateManager.saveAll()
        }
    }
}
