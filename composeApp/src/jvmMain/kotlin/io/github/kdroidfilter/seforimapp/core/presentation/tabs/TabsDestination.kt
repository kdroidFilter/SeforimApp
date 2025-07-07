package io.github.kdroidfilter.seforimapp.core.presentation.tabs

import kotlinx.serialization.Serializable

sealed interface TabsDestination {
    @Serializable
    data object Home : TabsDestination

    @Serializable
    data class Search(val searchQuery: String) : TabsDestination

    @Serializable
    data class BookContent(val bookId: Long) : TabsDestination
}