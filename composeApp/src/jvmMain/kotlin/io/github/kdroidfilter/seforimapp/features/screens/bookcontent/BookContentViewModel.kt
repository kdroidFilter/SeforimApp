package io.github.kdroidfilter.seforimapp.features.screens.bookcontent

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneState
import io.github.kdroidfilter.seforimapp.core.presentation.tabs.TabAwareViewModel
import io.github.kdroidfilter.seforimapp.core.presentation.tabs.TabStateManager

class BookContentViewModel(
    savedStateHandle: SavedStateHandle,
    stateManager: TabStateManager
) : TabAwareViewModel(
    tabId = savedStateHandle.get<String>("tabId") ?: "",
    stateManager = stateManager
) {

    // SplitPane state
    @OptIn(ExperimentalSplitPaneApi::class)
    private val _splitPaneState = MutableStateFlow(
        getState<SplitPaneState>("splitPaneState") ?: SplitPaneState(
            initialPositionPercentage = getState<Float>("splitPanePosition") ?: 0.3f,
            moveEnabled = true,
        )
    )

    @OptIn(ExperimentalSplitPaneApi::class)
    val splitPaneState = _splitPaneState.asStateFlow()

    // Search text state
    private val _searchText = MutableStateFlow(
        getState<String>("searchText") ?: ""
    )
    val searchText = _searchText.asStateFlow()

    // Scroll position state
    private val _scrollPosition = MutableStateFlow(
        getState<Int>("scrollPosition") ?: 0
    )
    val scrollPosition = _scrollPosition.asStateFlow()

    // Selected chapter state
    private val _selectedChapter = MutableStateFlow(
        getState<Int>("selectedChapter") ?: 0
    )
    val selectedChapter = _selectedChapter.asStateFlow()

    fun updateSearchText(text: String) {
        _searchText.value = text
        saveState("searchText", text)
    }

    fun updateScrollPosition(position: Int) {
        _scrollPosition.value = position
        saveState("scrollPosition", position)
    }

    fun selectChapter(chapter: Int) {
        _selectedChapter.value = chapter
        saveState("selectedChapter", chapter)
    }

    @OptIn(ExperimentalSplitPaneApi::class)
    fun saveAllStates() {
        saveState("splitPaneState", splitPaneState.value)
        saveState("splitPanePosition", splitPaneState.value.positionPercentage)
        saveState("searchText", searchText.value)
        saveState("scrollPosition", scrollPosition.value)
        saveState("selectedChapter", selectedChapter.value)
    }

    /**
     * Public method to get a state value for a specific key.
     * This is needed because the getState method in TabAwareViewModel is protected.
     */
    fun <T> getStateValue(key: String): T? {
        return getState(key)
    }

    /**
     * Public method to save a state value for a specific key.
     * This is needed because the saveState method in TabAwareViewModel is protected.
     */
    fun saveStateValue(key: String, value: Any) {
        saveState(key, value)
    }
}
