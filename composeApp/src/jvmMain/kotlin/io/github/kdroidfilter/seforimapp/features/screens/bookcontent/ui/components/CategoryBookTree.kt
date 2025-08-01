package io.github.kdroidfilter.seforimapp.features.screens.bookcontent.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.unit.dp
import io.github.kdroidfilter.seforimlibrary.core.models.Book
import io.github.kdroidfilter.seforimlibrary.core.models.Category
import io.github.kdroidfilter.seforimapp.features.screens.bookcontent.models.NavigationUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.jewel.ui.component.Text

@Stable
private data class TreeItem(
    val id: String,
    val level: Int,
    val content: @Composable () -> Unit
)

@OptIn(FlowPreview::class)
@Composable
fun CategoryBookTree(
    navigationState: NavigationUiState,
    onCategoryClick: (Category) -> Unit,
    onBookClick: (Book) -> Unit,
    onScroll: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    /* ---------------------------------------------------------------------
     * Build the flat hierarchical list to display.
     * -------------------------------------------------------------------- */
    val treeItems = remember(
        navigationState.rootCategories,
        navigationState.expandedCategories,
        navigationState.categoryChildren,
        navigationState.booksInCategory,
        navigationState.selectedCategory,
        navigationState.selectedBook
    ) {
        buildTreeItems(
            rootCategories = navigationState.rootCategories,
            expandedCategories = navigationState.expandedCategories,
            categoryChildren = navigationState.categoryChildren,
            booksInCategory = navigationState.booksInCategory,
            selectedCategory = navigationState.selectedCategory,
            selectedBook = navigationState.selectedBook,
            onCategoryClick = onCategoryClick,
            onBookClick = onBookClick
        )
    }

    /* ---------------------------------------------------------------------
     * Restore the LazyListState.
     * -------------------------------------------------------------------- */
    val listState: LazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = navigationState.scrollIndex,
        initialFirstVisibleItemScrollOffset = navigationState.scrollOffset
    )

    var hasRestored by remember { mutableStateOf(false) }


    /* ---------------- 1. Restore when the list is truly ready -------- */
    LaunchedEffect(treeItems.size) {
        if (treeItems.isNotEmpty() && !hasRestored) {
            val safeIndex = navigationState.scrollIndex
                .coerceIn(0, treeItems.lastIndex)
            listState.scrollToItem(safeIndex, navigationState.scrollOffset)
            hasRestored = true          // ← wait until finished before listening to scrolls
        }
    }

    /* ---------------- 2. Propagate scrolls *after* restoration ---------- */
    LaunchedEffect(listState, hasRestored) {
        if (hasRestored) {
            snapshotFlow {
                listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
            }
                .distinctUntilChanged()
                .debounce(250)
                .collect { (i, o) -> onScroll(i, o) }
        }
    }


    /* ---------------------------------------------------------------------
     * UI.
     * -------------------------------------------------------------------- */
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = treeItems,
            key = { it.id }
        ) { item ->
            Box(modifier = Modifier.padding(start = (item.level * 16).dp)) {
                item.content()
            }
        }
    }
}

/* -------------------------------------------------------------------------
 * Helpers
 * ---------------------------------------------------------------------- */
private fun buildTreeItems(
    rootCategories: List<Category>,
    expandedCategories: Set<Long>,
    categoryChildren: Map<Long, List<Category>>,
    booksInCategory: Set<Book>,
    selectedCategory: Category?,
    selectedBook: Book?,
    onCategoryClick: (Category) -> Unit,
    onBookClick: (Book) -> Unit
): List<TreeItem> = buildList {
    fun addCategory(category: Category, level: Int) {
        add(
            TreeItem(
                id = "category_${category.id}",
                level = level,
                content = {
                    CategoryItem(
                        category = category,
                        isExpanded = expandedCategories.contains(category.id),
                        isSelected = selectedCategory?.id == category.id,
                        onClick = { onCategoryClick(category) }
                    )
                }
            )
        )

        if (expandedCategories.contains(category.id)) {
            // Books in this category
            booksInCategory
                .filter { it.categoryId == category.id }
                .forEach { book ->
                    add(
                        TreeItem(
                            id = "book_${book.id}",
                            level = level + 1,
                            content = {
                                BookItem(
                                    book = book,
                                    isSelected = selectedBook?.id == book.id,
                                    onClick = { onBookClick(book) }
                                )
                            }
                        )
                    )
                }

            // Subcategories
            categoryChildren[category.id]?.forEach { child ->
                addCategory(child, level + 1)
            }
        }
    }

    rootCategories.forEach { category ->
        addCategory(category, 0)
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    isExpanded: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isExpanded) "-" else "+",
            modifier = Modifier.width(24.dp)
        )
        Text(text = "📁", modifier = Modifier.width(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = category.title)
    }
}

@Composable
private fun BookItem(
    book: Book,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(24.dp))
        Text(text = "📕", modifier = Modifier.width(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = book.title, fontWeight = if (isSelected) Bold else Normal)
    }
}