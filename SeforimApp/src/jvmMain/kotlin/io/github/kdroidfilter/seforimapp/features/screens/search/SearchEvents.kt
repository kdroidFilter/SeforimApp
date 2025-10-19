package io.github.kdroidfilter.seforimapp.features.screens.search

import io.github.kdroidfilter.seforimlibrary.core.models.SearchResult

sealed interface SearchEvent {
    data class QueryChanged(val text: String) : SearchEvent
    data class PrecisionChanged(val value: Int) : SearchEvent
    data class Submit(val query: String) : SearchEvent
    data class ContentScrolled(
        val anchorId: Long,
        val anchorIndex: Int,
        val scrollIndex: Int,
        val scrollOffset: Int
    ) : SearchEvent
    data class OpenResult(val result: SearchResult) : SearchEvent
    data object SaveState : SearchEvent
}
