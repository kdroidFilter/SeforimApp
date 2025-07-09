package io.github.kdroidfilter.seforimapp.features.screens.bookcontent

sealed class BookContentEvents {
    data class UpdateScrollPosition(val position: Int): BookContentEvents()
    data object SaveAllStates : BookContentEvents()
    data class OnSearchTextChange(val text: String): BookContentEvents()
    data class OnChapterSelected(val index: Int): BookContentEvents()


}