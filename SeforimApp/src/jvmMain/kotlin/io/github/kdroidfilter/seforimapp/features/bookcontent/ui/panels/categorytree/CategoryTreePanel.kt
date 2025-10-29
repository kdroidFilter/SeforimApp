package io.github.kdroidfilter.seforimapp.features.bookcontent.ui.panels.categorytree

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isMetaPressed
import io.github.kdroidfilter.seforimapp.features.bookcontent.BookContentEvent
import io.github.kdroidfilter.seforimapp.features.bookcontent.state.BookContentState
import io.github.kdroidfilter.seforimapp.features.bookcontent.ui.components.PaneHeader
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import seforimapp.seforimapp.generated.resources.Res
import seforimapp.seforimapp.generated.resources.search_placeholder
import seforimapp.seforimapp.generated.resources.book_list

@Composable
fun CategoryTreePanel(
    uiState: BookContentState,
    onEvent: (BookContentEvent) -> Unit,
    modifier: Modifier = Modifier,
    // Optional: integrate search results counts and filtering
    searchViewModel: io.github.kdroidfilter.seforimapp.features.search.SearchResultViewModel? = null
) {
    val paneHoverSource = remember { MutableInteractionSource() }
    Column(modifier = modifier.hoverable(paneHoverSource)) {
        PaneHeader(
            label = stringResource(Res.string.book_list),
            interactionSource = paneHoverSource,
            onHide = { onEvent(BookContentEvent.ToggleBookTree) }
        )
        Column(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
//            SearchField(
//                searchText = uiState.navigation.searchText,
//                onSearchTextChange = { onEvent(BookContentEvent.SearchTextChanged(it)) }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))

            val windowInfo = LocalWindowInfo.current

            // If a searchViewModel is provided, derive counts maps and selection overrides using flows
            val searchUi = searchViewModel?.uiState?.collectAsState()?.value
            val categoryAgg = searchViewModel?.categoryAggFlow?.collectAsState()?.value
            val categoryCounts: Map<Long, Int> = categoryAgg?.categoryCounts ?: emptyMap()
            val bookCounts: Map<Long, Int> = categoryAgg?.bookCounts ?: emptyMap()
            val booksForCategoryOverride: Map<Long, List<io.github.kdroidfilter.seforimlibrary.core.models.Book>> = categoryAgg?.booksForCategory ?: emptyMap()
            val selectedCategoryIdOverride = searchUi?.scopeCategoryPath?.lastOrNull()?.id
            val selectedBookIdOverride = searchUi?.scopeBook?.id

            CategoryBookTreeView(
                navigationState = uiState.navigation,
                onCategoryClick = {
                    // Always toggle expansion/selection for UX
                    onEvent(BookContentEvent.CategorySelected(it))
                    if (searchViewModel != null) {
                        searchViewModel.filterByCategoryId(it.id)
                    }
                },
                onBookClick = {
                    if (searchViewModel != null) {
                        searchViewModel.filterByBookId(it.id)
                    } else {
                        val mods = windowInfo.keyboardModifiers
                        if (mods.isCtrlPressed || mods.isMetaPressed) {
                            onEvent(BookContentEvent.BookSelectedInNewTab(it))
                        } else {
                            onEvent(BookContentEvent.BookSelected(it))
                        }
                    }
                },
                onScroll = { index, offset -> onEvent(BookContentEvent.BookTreeScrolled(index, offset)) },
                categoryCounts = categoryCounts,
                bookCounts = bookCounts,
                selectedCategoryIdOverride = selectedCategoryIdOverride,
                selectedBookIdOverride = selectedBookIdOverride,
                showCounts = searchViewModel != null,
                booksForCategoryOverride = booksForCategoryOverride
            )
        }
    }
}

@Composable
private fun SearchField(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    val textFieldState = rememberTextFieldState(searchText)

    LaunchedEffect(searchText) {
        if (textFieldState.text.toString() != searchText) {
            textFieldState.edit { replace(0, length, searchText) }
        }
    }

    LaunchedEffect(textFieldState.text) {
        onSearchTextChange(textFieldState.text.toString())
    }

    TextField(
        state = textFieldState,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(Res.string.search_placeholder)) }
    )
}

// No local flatten helpers needed; we consume aggregated counts directly from ViewModel flows
