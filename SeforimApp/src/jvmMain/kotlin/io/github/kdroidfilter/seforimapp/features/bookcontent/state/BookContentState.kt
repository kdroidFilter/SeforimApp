package io.github.kdroidfilter.seforimapp.features.bookcontent.state

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import io.github.kdroidfilter.seforimlibrary.core.models.*
import io.github.kdroidfilter.seforimlibrary.dao.repository.CommentaryWithText
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneState
import io.github.kdroidfilter.seforimlibrary.core.models.Line
import io.github.kdroidfilter.seforimlibrary.core.models.TocEntry

/**
 * Auxiliary models that are not part of the persistent state
 */
@Immutable
data class Providers(
    val linesPagingData: Flow<PagingData<Line>>,
    val buildCommentariesPagerFor: (Long, Long?) -> Flow<PagingData<CommentaryWithText>>,
    val getAvailableCommentatorsForLine: suspend (Long) -> Map<String, Long>,
    val buildLinksPagerFor: (Long, Long?) -> Flow<PagingData<CommentaryWithText>>,
    val getAvailableLinksForLine: suspend (Long) -> Map<String, Long>
)

/**
 * Represents a visible TOC entry in the flattened list
 */
@Immutable
data class VisibleTocEntry(
    val entry: TocEntry,
    val level: Int,
    val isExpanded: Boolean,
    val hasChildren: Boolean,
    val isLastChild: Boolean
)
/**
 * Unified state for BookContent (UI + Business)
 */
@Stable
data class BookContentState @OptIn(ExperimentalSplitPaneApi::class) constructor(
    val navigation: NavigationState = NavigationState(),
    val toc: TocState = TocState(),
    val content: ContentState = ContentState(),
    val layout: LayoutState = LayoutState(),
    val isLoading: Boolean = false,
    val providers: Providers? = null
)

@Immutable
data class NavigationState(
    // Business
    val rootCategories: List<Category> = emptyList(),
    val expandedCategories: Set<Long> = emptySet(),
    val categoryChildren: Map<Long, List<Category>> = emptyMap(),
    val booksInCategory: Set<Book> = emptySet(),
    val selectedCategory: Category? = null,
    val selectedBook: Book? = null,
    val searchText: String = "",

    // UI
    val isVisible: Boolean = true,
    val scrollIndex: Int = 0,
    val scrollOffset: Int = 0
)

@Immutable
data class TocState(
    // Business
    val entries: List<TocEntry> = emptyList(),
    val expandedEntries: Set<Long> = emptySet(),
    val children: Map<Long, List<TocEntry>> = emptyMap(),

    // UI
    val isVisible: Boolean = false,
    val scrollIndex: Int = 0,
    val scrollOffset: Int = 0
)

@Immutable
data class ContentState(
    // Data
    val lines: List<Line> = emptyList(),
    val selectedLine: Line? = null,
    val commentaries: List<CommentaryWithText> = emptyList(),

    // Visibility
    val showCommentaries: Boolean = false,
    val showTargum: Boolean = false,

    // Scroll positions
    val paragraphScrollPosition: Int = 0,
    val chapterScrollPosition: Int = 0,
    val selectedChapter: Int = 0,
    val scrollIndex: Int = 0,
    val scrollOffset: Int = 0,

    // Anchoring
    val anchorId: Long = -1L,
    val anchorIndex: Int = 0,

    // Commentaries UI state
    val commentariesSelectedTab: Int = 0,
    val commentariesScrollIndex: Int = 0,
    val commentariesScrollOffset: Int = 0,
    val commentatorsListScrollIndex: Int = 0,
    val commentatorsListScrollOffset: Int = 0,
    // Per-column (per commentator) scroll positions
    val commentariesColumnScrollIndexByCommentator: Map<Long, Int> = emptyMap(),
    val commentariesColumnScrollOffsetByCommentator: Map<Long, Int> = emptyMap(),

    // Filters selected in UI (for current line)
    val selectedCommentatorIds: Set<Long> = emptySet(),
    val selectedTargumSourceIds: Set<Long> = emptySet(),

    // Business selections by line/book (kept for use cases)
    val selectedCommentatorsByLine: Map<Long, Set<Long>> = emptyMap(),
    val selectedCommentatorsByBook: Map<Long, Set<Long>> = emptyMap(),
    val selectedLinkSourcesByLine: Map<Long, Set<Long>> = emptyMap(),
    val selectedLinkSourcesByBook: Map<Long, Set<Long>> = emptyMap(),

    // Scrolling behavior control
    val shouldScrollToLine: Boolean = false,
    val scrollToLineTimestamp: Long = 0L
)

/**
 * Layout state uses SplitPaneState to directly bind with UI panes
 */
@Stable
data class LayoutState @OptIn(ExperimentalSplitPaneApi::class) constructor(
    val mainSplitState: SplitPaneState = SplitPaneState(initialPositionPercentage = SplitDefaults.MAIN, moveEnabled = true),
    val tocSplitState: SplitPaneState = SplitPaneState(initialPositionPercentage = SplitDefaults.TOC, moveEnabled = true),
    val contentSplitState: SplitPaneState = SplitPaneState(initialPositionPercentage = 0.7f, moveEnabled = true),
    val targumSplitState: SplitPaneState = SplitPaneState(initialPositionPercentage = 0.8f, moveEnabled = true),
    val previousPositions: PreviousPositions = PreviousPositions()
)

@Immutable
data class PreviousPositions(
    val main: Float = SplitDefaults.MAIN,
    val toc: Float = SplitDefaults.TOC,
    val content: Float = 0.7f,
    val links: Float = 0.8f
)
