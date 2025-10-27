package io.github.kdroidfilter.seforimapp.features.bookcontent.ui.panels.bookcontent.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import io.github.kdroidfilter.seforim.htmlparser.buildAnnotatedFromHtml
import io.github.kdroidfilter.seforimapp.core.settings.AppSettings
import io.github.kdroidfilter.seforimapp.features.bookcontent.BookContentEvent
import io.github.kdroidfilter.seforimapp.logger.debugln
import io.github.kdroidfilter.seforimlibrary.core.models.Book
import io.github.kdroidfilter.seforimlibrary.core.models.Line
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeoutOrNull
import org.jetbrains.compose.resources.Font
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.CircularProgressIndicator
import org.jetbrains.jewel.ui.component.Text
import seforimapp.seforimapp.generated.resources.Res
import seforimapp.seforimapp.generated.resources.notoserifhebrew

@OptIn(FlowPreview::class)
@Composable
fun BookContentView(
    book: Book,
    linesPagingData: Flow<PagingData<Line>>,
    selectedLine: Line?,
    onLineSelected: (Line) -> Unit,
    onEvent: (BookContentEvent) -> Unit,
    modifier: Modifier = Modifier,
    preservedListState: LazyListState? = null,
    scrollIndex: Int = 0,
    scrollOffset: Int = 0,
    scrollToLineTimestamp: Long = 0,
    anchorId: Long = -1L,
    anchorIndex: Int = 0,
    topAnchorLineId: Long = -1L,
    topAnchorTimestamp: Long = 0L,
    onScroll: (Long, Int, Int, Int) -> Unit = { _, _, _, _ -> }
) {
    // Collect paging data
    val lazyPagingItems: LazyPagingItems<Line> = linesPagingData.collectAsLazyPagingItems()

    // Don't use the saved scroll position initially if we have an anchor
    // The restoration will be handled after pagination loads
    val listState = preservedListState ?: rememberLazyListState(
        initialFirstVisibleItemIndex = if (anchorId != -1L) 0 else scrollIndex,
        initialFirstVisibleItemScrollOffset = if (anchorId != -1L) 0 else scrollOffset
    )

    // Collect text size from settings
    val rawTextSize by AppSettings.textSizeFlow.collectAsState()

    // Animate text size changes for smoother transitions
    val textSize by animateFloatAsState(
        targetValue = rawTextSize,
        animationSpec = tween(durationMillis = 300),
        label = "textSizeAnimation"
    )

    // Collect line height from settings
    val rawLineHeight by AppSettings.lineHeightFlow.collectAsState()

    // Animate line height changes for smoother transitions
    val lineHeight by animateFloatAsState(
        targetValue = rawLineHeight,
        animationSpec = tween(durationMillis = 300),
        label = "lineHeightAnimation"
    )

    // Track restoration state per book
    var hasRestored by remember(book.id) { mutableStateOf(false) }

    // Track the restored anchor to avoid re-restoration
    var restoredAnchorId by remember(book.id) { mutableStateOf(-1L) }

    // Optimize selected line ID lookup
    val selectedLineId = remember(selectedLine) { selectedLine?.id }

    // Ensure the selected line is visible when explicitly requested (keyboard/nav)
    // without forcing it to the very top of the viewport.
    LaunchedEffect(scrollToLineTimestamp, selectedLineId, topAnchorTimestamp, topAnchorLineId) {
        if (scrollToLineTimestamp == 0L || selectedLineId == null) return@LaunchedEffect

        // Skip minimal bring-into-view when a top-anchoring request is active for this selection
        val isTopAnchorRequest = (topAnchorTimestamp == scrollToLineTimestamp && topAnchorLineId == selectedLineId)
        if (isTopAnchorRequest) return@LaunchedEffect

        while (lazyPagingItems.loadState.refresh is LoadState.Loading) {
            delay(16)
        }

        val snapshot = lazyPagingItems.itemSnapshotList
        val index = snapshot.indices.firstOrNull { snapshot[it]?.id == selectedLineId }
        if (index != null) {
            val first = listState.firstVisibleItemIndex
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: first
            if (index < first || index > last) {
                // Scroll just enough so the item is not glued to the top
                val targetOffsetPx = 32  // small top padding in px; minimal jump
                listState.scrollToItem(index, targetOffsetPx)
            }
        }
    }

    // Robust top-anchored restoration for TOC-driven selection.
    // Trigger on every new anchorId. This ensures repeated TOC clicks always re-align at the top.
    LaunchedEffect(topAnchorTimestamp, topAnchorLineId) {
        if (topAnchorTimestamp == 0L || topAnchorLineId == -1L) return@LaunchedEffect

        // Reset restoration guard for this top-anchor event
        hasRestored = false

        // Wait for any ongoing refresh to complete
        while (lazyPagingItems.loadState.refresh is LoadState.Loading) {
            delay(16)
        }

        // Helper to locate the target index in the current snapshot
        fun currentTargetIndex(): Int? {
            val snapshot = lazyPagingItems.itemSnapshotList
            return snapshot.indices.firstOrNull { snapshot[it]?.id == topAnchorLineId }
        }

        var targetIndex = currentTargetIndex()
        if (targetIndex == null) {
            debugln { "Top-anchor target $topAnchorLineId not yet in snapshot; waiting" }
            withTimeoutOrNull(1500L) {
                snapshotFlow { lazyPagingItems.itemSnapshotList.items }
                    .map { items -> items.indices.firstOrNull { items[it]?.id == topAnchorLineId } }
                    .filterNotNull()
                    .first().also { idx -> targetIndex = idx }
            }
        }

        targetIndex?.let { idx ->
            debugln { "Top-anchoring to index $idx for line $topAnchorLineId" }
            listState.scrollToItem(idx, 0)
            restoredAnchorId = topAnchorLineId
            hasRestored = true
        }
    }

    // Initial restoration from saved state (TabSystem): prefer saved anchor, otherwise saved index/offset.
    // Runs once per book unless a top-anchor request has been issued (which handles itself).
    LaunchedEffect(book.id, topAnchorTimestamp) {
        if (topAnchorTimestamp != 0L) return@LaunchedEffect
        if (hasRestored) return@LaunchedEffect

        // Wait for initial page load to complete
        while (lazyPagingItems.loadState.refresh is LoadState.Loading) {
            delay(16)
        }

        if (lazyPagingItems.itemCount <= 0) return@LaunchedEffect

        // Try saved anchor if available
        if (anchorId != -1L) {
            val snapshot = lazyPagingItems.itemSnapshotList
            val idx = snapshot.indices.firstOrNull { snapshot[it]?.id == anchorId }
            if (idx != null) {
                debugln { "Restoring by saved anchor: idx=$idx, offset=$scrollOffset" }
                listState.scrollToItem(idx, scrollOffset.coerceAtLeast(0))
                hasRestored = true
                restoredAnchorId = anchorId
                return@LaunchedEffect
            }
        }

        // Fallback to index/offset when no anchor or anchor not in snapshot
        if (scrollIndex > 0 || scrollOffset > 0) {
            val itemCount = lazyPagingItems.itemCount
            val targetIndex = scrollIndex.coerceIn(0, maxOf(0, itemCount - 1))
            val targetOffset = scrollOffset.coerceAtLeast(0)
            debugln { "Restoring by index/offset: index=$targetIndex, offset=$targetOffset" }
            listState.scrollToItem(targetIndex, targetOffset)
            hasRestored = true
        }
    }

    // Save scroll position with anchor information - optimized with derivedStateOf
    val scrollData = remember(listState, lazyPagingItems) {
        derivedStateOf {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val itemCount = lazyPagingItems.itemCount
            val safeIndex = firstVisibleIndex.coerceAtMost(itemCount - 1)

            // Get the ID of the first visible line as the anchor
            val currentAnchorId = if (safeIndex in 0 until itemCount) {
                lazyPagingItems[safeIndex]?.id ?: -1L
            } else {
                -1L
            }

            val scrollOff = listState.firstVisibleItemScrollOffset

            AnchorData(
                anchorId = currentAnchorId,
                anchorIndex = safeIndex,
                scrollIndex = firstVisibleIndex,
                scrollOffset = scrollOff
            )
        }
    }

    LaunchedEffect(scrollData, hasRestored, scrollIndex, scrollOffset) {
        // Guard: avoid overwriting a previously saved non-zero position with (0,0) before restoration
        // Start collecting always, but filter out the initial top emission if we still need to restore
        snapshotFlow { scrollData.value }
            .distinctUntilChanged()
            .debounce(250)
            .collect { data ->
                if (!hasRestored) {
                    val hasSavedNonZero = (scrollIndex > 0 || scrollOffset > 0)
                    val isCurrentZero = (data.scrollIndex == 0 && data.scrollOffset == 0)
                    if (hasSavedNonZero && isCurrentZero) {
                        debugln { "Skipping early (0,0) scroll save to preserve saved position: saved=($scrollIndex,$scrollOffset)" }
                        return@collect
                    }
                }
                debugln { "Saving scroll: anchor=${data.anchorId}, index=${data.scrollIndex}, offset=${data.scrollOffset}" }
                onScroll(data.anchorId, data.anchorIndex, data.scrollIndex, data.scrollOffset)
            }
    }

    // Memoize key event handler to avoid recreation
    val keyEventHandler = remember(onEvent) {
        { keyEvent: androidx.compose.ui.input.key.KeyEvent ->
            debugln { "[BookContentView] Key event: key=${keyEvent.key}, type=${keyEvent.type}" }

            if (keyEvent.type != KeyEventType.KeyDown) {
                false
            } else {
                when (keyEvent.key) {
                    Key.DirectionUp -> {
                        debugln { "[BookContentView] Up arrow key pressed, navigating to previous line" }
                        onEvent(BookContentEvent.NavigateToPreviousLine)
                        true
                    }
                    Key.DirectionDown -> {
                        debugln { "[BookContentView] Down arrow key pressed, navigating to next line" }
                        onEvent(BookContentEvent.NavigateToNextLine)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    SelectionContainer(
        modifier = modifier
            .fillMaxSize()
            .onKeyEvent(keyEventHandler)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id },
                contentType = { "line" }  // Optimization: specify content type
            ) { index ->
                val line = lazyPagingItems[index]

                if (line != null) {
                    LineItem(
                        line = line,
                        isSelected = selectedLineId == line.id,
                        baseTextSize = textSize,
                        lineHeight = lineHeight,
                        onLineSelected = onLineSelected,
                        scrollToLineTimestamp = scrollToLineTimestamp
                    )
                } else {
                    // Placeholder while loading
                    LoadingPlaceholder()
                }
            }

            // Show loading indicators
            lazyPagingItems.apply {
                when {
                    // Avoid flicker: only show full loader on refresh if we have no items yet
                    loadState.refresh is LoadState.Loading && itemCount == 0 -> {
                        item(contentType = "loading") {
                            LoadingIndicator()
                        }
                    }
                    // Keep small loader for pagination append
                    loadState.append is LoadState.Loading -> {
                        item(contentType = "loading") {
                            LoadingIndicator(isSmall = true)
                        }
                    }
                    loadState.refresh is LoadState.Error -> {
                        val error = (loadState.refresh as LoadState.Error).error
                        item(contentType = "error") {
                            ErrorIndicator(message = "Error: ${error.message}")
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        val error = (loadState.append as LoadState.Error).error
                        item(contentType = "error") {
                            ErrorIndicator(message = "Error loading more: ${error.message}")
                        }
                    }
                }
            }
        }
    }
}

// Data class for anchor information
private data class AnchorData(
    val anchorId: Long,
    val anchorIndex: Int,
    val scrollIndex: Int,
    val scrollOffset: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LineItem(
    line: Line,
    isSelected: Boolean,
    baseTextSize: Float = 16f,
    lineHeight: Float = 1.5f,
    onLineSelected: (Line) -> Unit,
    scrollToLineTimestamp: Long
) {
    // Memoize the annotated string with proper keys
    val annotated = remember(line.id, line.content, baseTextSize) {
        buildAnnotatedFromHtml(line.content, baseTextSize)
    }

    // Memoize click handler to avoid recreation
    val clickHandler = remember(line, onLineSelected) {
        { onLineSelected(line) }
    }

    // Get theme color in composable context
    val borderColor = if (isSelected) JewelTheme.globalColors.outlines.focused else Color.Transparent

    val bringRequester = remember { BringIntoViewRequester() }

    // On navigation/explicit request, bring the selected line minimally into view
    LaunchedEffect(isSelected, scrollToLineTimestamp) {
        if (isSelected && scrollToLineTimestamp != 0L) {
            try {
                bringRequester.bringIntoView()
            } catch (_: Throwable) { /* no-op: layout might not be ready yet */ }
        }
    }

    val textModifier = remember {
        Modifier.fillMaxWidth()
    }
        .bringIntoViewRequester(bringRequester)
        .pointerInput(line) {
        detectTapGestures(onTap = { clickHandler() })
    }

    val hebrewFontFamily =
        FontFamily(
            Font(
                resource = Res.font.notoserifhebrew,
                weight = FontWeight.Normal
            )
        )


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(borderColor)
                    .zIndex(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = annotated,
                textAlign = TextAlign.Justify,
                fontFamily = hebrewFontFamily,
                lineHeight = (baseTextSize * lineHeight).sp,
                modifier = textModifier
            )
        }
    }
}

// Extract reusable components to avoid inline composition
@Composable
private fun LoadingPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun LoadingIndicator(isSmall: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = if (isSmall) Modifier.size(24.dp) else Modifier
        )
    }
}

@Composable
private fun ErrorIndicator(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.Red
        )
    }
}
