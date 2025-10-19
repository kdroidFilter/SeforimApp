package io.github.kdroidfilter.seforimapp.features.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
// Use Jewel widgets for consistency with BookContent
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import io.github.kdroidfilter.seforim.htmlparser.buildAnnotatedFromHtml
import io.github.kdroidfilter.seforimapp.core.settings.AppSettings
import io.github.kdroidfilter.seforimlibrary.core.models.SearchResult
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.GroupHeader
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.component.CircularProgressIndicator
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.Flow
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.TextFieldState
import io.github.kdroidfilter.seforimapp.logger.debugln

/**
 * Presentational view for the Search screen.
 * State is provided by SearchViewModel. Scroll saves are emitted via onScroll.
 */
@OptIn(FlowPreview::class)
@Composable
fun SearchView(
    uiState: SearchUiState,
    resultsFlow: Flow<PagingData<SearchResult>>,
    onQueryInputChange: (String) -> Unit,
    onSubmit: (String) -> Unit,
    onOpenResult: (SearchResult) -> Unit,
    onScroll: (anchorId: Long, anchorIndex: Int, scrollIndex: Int, scrollOffset: Int) -> Unit
) {
    val lazyPagingItems = resultsFlow.collectAsLazyPagingItems()
    // Ensure a hard reload when the applied query changes
    LaunchedEffect(uiState.query) {
        lazyPagingItems.refresh()
    }

    // Keep text size consistent with BookContent
    val baseTextSize by AppSettings.textSizeFlow.collectAsState()

    // Query input state (Jewel TextField requires TextFieldState)
    val queryState: TextFieldState = rememberTextFieldState(uiState.queryInput)
    LaunchedEffect(uiState.queryInput) {
        if (queryState.text.toString() != uiState.queryInput) {
            queryState.edit { replace(0, length, uiState.queryInput) }
        }
    }
    LaunchedEffect(queryState.text) {
        onQueryInputChange(queryState.text.toString())
    }

    val listState: LazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (uiState.anchorId != -1L) 0 else uiState.scrollIndex,
        initialFirstVisibleItemScrollOffset = if (uiState.anchorId != -1L) 0 else uiState.scrollOffset
    )

    var hasRestored by remember(uiState.query, uiState.precision) { mutableStateOf(false) }
    var restoredAnchorId by remember(uiState.query, uiState.precision) { mutableStateOf(-1L) }

    // Attempt restoration after initial load
    LaunchedEffect(lazyPagingItems.loadState.refresh, uiState.anchorId) {
        if (!hasRestored && uiState.anchorId != -1L && uiState.anchorId != restoredAnchorId) {
            if (lazyPagingItems.loadState.refresh is androidx.paging.LoadState.Loading) {
                while (lazyPagingItems.loadState.refresh is androidx.paging.LoadState.Loading) {
                    delay(16)
                }
            }
            if (lazyPagingItems.itemCount > 0) {
                val snapshot = lazyPagingItems.itemSnapshotList
                val anchorIndex = snapshot.indices.firstOrNull { snapshot[it]?.lineId == uiState.anchorId }
                if (anchorIndex != null) {
                    listState.scrollToItem(anchorIndex, uiState.scrollOffset)
                    hasRestored = true
                    restoredAnchorId = uiState.anchorId
                } else {
                    val itemCount = lazyPagingItems.itemCount
                    if (itemCount > 0) {
                        val indexByAnchor = uiState.anchorIndex.takeIf { it in 0 until itemCount }
                        val indexByScroll = uiState.scrollIndex.takeIf { it in 0 until itemCount }
                        val targetIndex = (indexByAnchor ?: indexByScroll ?: 0)
                        val targetOffset = if (targetIndex == uiState.scrollIndex) uiState.scrollOffset.coerceAtLeast(0) else 0
                        listState.scrollToItem(targetIndex, targetOffset)
                        hasRestored = true
                    }
                }
            }
        }
    }

    // Fallback restoration when no anchor is set but a scroll position exists
    LaunchedEffect(lazyPagingItems.loadState.refresh, uiState.scrollIndex, uiState.scrollOffset, uiState.anchorId) {
        if (!hasRestored && uiState.anchorId == -1L && (uiState.scrollIndex > 0 || uiState.scrollOffset > 0)) {
            if (lazyPagingItems.loadState.refresh is androidx.paging.LoadState.Loading) {
                while (lazyPagingItems.loadState.refresh is androidx.paging.LoadState.Loading) {
                    delay(16)
                }
            }
            if (lazyPagingItems.itemCount > 0) {
                val itemCount = lazyPagingItems.itemCount
                val targetIndex = uiState.scrollIndex.coerceIn(0, maxOf(0, itemCount - 1))
                val targetOffset = uiState.scrollOffset.coerceAtLeast(0)
                if (targetIndex != 0 || targetOffset != 0) {
                    listState.scrollToItem(targetIndex, targetOffset)
                    hasRestored = true
                }
            }
        }
    }

    // Save scroll state debounced
    val scrollData = remember(listState, lazyPagingItems) {
        derivedStateOf {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val itemCount = lazyPagingItems.itemCount
            val safeIndex = firstVisibleIndex.coerceAtMost(itemCount - 1)
            val anchorLineId = if (safeIndex in 0 until itemCount) {
                lazyPagingItems[safeIndex]?.lineId ?: -1L
            } else -1L
            Triple(anchorLineId, firstVisibleIndex, listState.firstVisibleItemScrollOffset)
        }
    }

    LaunchedEffect(scrollData, hasRestored, uiState.scrollIndex, uiState.scrollOffset) {
        snapshotFlow { scrollData.value }
            .distinctUntilChanged()
            .debounce(250)
            .collect { (anchorId, index, offset) ->
                if (!hasRestored) {
                    val hasSavedNonZero = (uiState.scrollIndex > 0 || uiState.scrollOffset > 0)
                    val isCurrentZero = (index == 0 && offset == 0)
                    if (hasSavedNonZero && isCurrentZero) return@collect
                }
                onScroll(anchorId, index, index, offset)
            }
    }

    // Debug: log current snapshot IDs and detect visible duplicates for diagnostics
    LaunchedEffect(lazyPagingItems) {
        snapshotFlow { lazyPagingItems.itemSnapshotList.items.mapNotNull { it?.lineId } }
            .distinctUntilChanged()
            .collect { ids ->
                if (ids.isNotEmpty()) {
                    val dups = ids.groupBy { it }.filterValues { it.size > 1 }.keys
                    if (dups.isNotEmpty()) {
                        debugln { "[SearchView] visible duplicates: ${dups.take(20)}" }
                    }
                }
            }
    }

    // Log the actual texts visible on screen to help spot duplicates by content
    LaunchedEffect(listState, lazyPagingItems) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.map { it.index } }
            .distinctUntilChanged()
            .collect { visibleIndices ->
                if (visibleIndices.isEmpty()) return@collect
                val itemCount = lazyPagingItems.itemCount
                val safeIndices = visibleIndices.filter { it in 0 until itemCount }
                val visible = safeIndices.mapNotNull { idx ->
                    runCatching { lazyPagingItems[idx] }.getOrNull()?.let { item ->
                        Triple(item.lineId, item.bookTitle, item.snippet)
                    }
                }
                if (visible.isNotEmpty()) {
                    val preview = visible.joinToString(separator = " | ") { (id, title, snip) ->
                        val t = if (snip.length > 120) snip.take(120) + "…" else snip
                        "$id:$title:$t"
                    }
                    debugln { "[SearchView] visible texts: $preview" }

                    val dupSnippets = visible.groupBy { it.third }.filterValues { it.size > 1 }.keys
                    if (dupSnippets.isNotEmpty()) {
                        val d = dupSnippets.take(5).map { it.take(120) }
                        debugln { "[SearchView] visible text duplicates by snippet: $d" }
                    }
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header with query editor
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(JewelTheme.globalColors.panelBackground)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            GroupHeader(text = "תוצאות חיפוש")
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    state = queryState,
                    modifier = Modifier
                        .weight(1f)
                        .onPreviewKeyEvent { ev ->
                            if (ev.key == Key.Enter || ev.key == Key.NumPadEnter) {
                                onSubmit(queryState.text.toString())
                                true
                            } else false
                        },
                    placeholder = { Text("...הקלד לחיפוש מחדש") },
                    textStyle = TextStyle(fontSize = 13.sp, textDirection = TextDirection.ContentOrRtl)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "דיוק: ${uiState.precision}",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        // Body: loading or results
        when {
            lazyPagingItems.loadState.refresh is androidx.paging.LoadState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            lazyPagingItems.itemCount == 0 -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("לא נמצאו תוצאות")
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
                    items(
                        count = lazyPagingItems.itemCount,
                        key = lazyPagingItems.itemKey { it.lineId },
                        contentType = { "searchResult" }
                    ) { index ->
                        val item = lazyPagingItems[index]
                        if (item != null) {
                            ResultRow(item = item, baseTextSize = baseTextSize) { onOpenResult(item) }
                        }
                    }
                    // Append loading indicator
                    item {
                        if (lazyPagingItems.loadState.append is androidx.paging.LoadState.Loading) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultRow(item: SearchResult, baseTextSize: Float, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(text = item.bookTitle, fontWeight = FontWeight.SemiBold, color = Color(0xFF32527B))
        Spacer(Modifier.height(6.dp))
        // Render snippet with the same HTML interpreter as BookContent
        val annotated = remember(item.lineId, item.snippet, baseTextSize) {
            buildAnnotatedFromHtml(item.snippet, baseTextSize)
        }
        Text(text = annotated)
    }
}
