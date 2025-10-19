package io.github.kdroidfilter.seforimapp.features.screens.search

data class SearchUiState(
    val query: String,
    val precision: Int,
    val queryInput: String,
    val anchorId: Long = -1L,
    val anchorIndex: Int = 0,
    val scrollIndex: Int = 0,
    val scrollOffset: Int = 0
)

