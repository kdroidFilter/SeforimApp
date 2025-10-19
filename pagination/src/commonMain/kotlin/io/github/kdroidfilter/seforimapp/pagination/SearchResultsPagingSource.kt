package io.github.kdroidfilter.seforimapp.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.kdroidfilter.seforimapp.logger.debugln
import io.github.kdroidfilter.seforimlibrary.core.models.SearchResult
import io.github.kdroidfilter.seforimlibrary.dao.repository.SeforimRepository

/**
 * PagingSource for full-text search results.
 * Loads results in fixed-size pages using repository.search(query, limit, offset).
 */
class SearchResultsPagingSource(
    private val repository: SeforimRepository,
    private val query: String,
    private val precision: Int // Kept for future adjustments; not altering page size here
) : PagingSource<Int, SearchResult>() {

    // Track loaded results to avoid duplicates across pages
    private val loadedLineIds = mutableSetOf<Long>()
    private val loadedNormalizedSnippets = mutableSetOf<String>()

    private fun normalizeSnippet(snippet: String): String =
        snippet
            .replace(Regex("<[^>]+>"), "") // strip basic HTML tags
            .replace(Regex("\\s+"), " ") // collapse whitespace
            .trim()

    override fun getRefreshKey(state: PagingState<Int, SearchResult>): Int? {
        // Try to find the page key of the closest page to anchorPosition
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResult> = try {
        val page = params.key ?: 0
        val pageSize = PagingDefaults.SEARCH.PAGE_SIZE
        val offset = page * pageSize
        // Clear duplicate tracker on Refresh
        if (params is LoadParams.Refresh) {
            loadedLineIds.clear()
        }

        val results = repository.search(query = query, limit = pageSize, offset = offset)

        // Log raw result set for diagnostics
        debugln {
            val ids = results.joinToString(limit = 50, truncated = "…") { r -> "${r.lineId}@${r.lineIndex}" }
            "[SearchPaging] page=${page} offset=${offset} size=${results.size} q='${query.take(60)}' ids=[${ids}]"
        }

        // Detect local duplicates within the page
        run {
            val dupes = results.groupBy { it.lineId }.filterValues { it.size > 1 }
            if (dupes.isNotEmpty()) {
                debugln { "[SearchPaging] local duplicates (page=${page}): ${dupes.keys.take(20)}" }
            }
        }

        // Filter out duplicates by unique lineId to ensure stable, clean list
        val beforeIds = loadedLineIds.size
        val beforeSnips = loadedNormalizedSnippets.size
        val newResults = results.filter { r ->
            val okId = loadedLineIds.add(r.lineId)
            val okSnippet = loadedNormalizedSnippets.add(normalizeSnippet(r.snippet))
            okId && okSnippet
        }
        val skipped = results.size - newResults.size
        if (skipped > 0) {
            val afterIds = loadedLineIds.size
            val afterSnips = loadedNormalizedSnippets.size
            debugln { "[SearchPaging] filtered ${skipped} duplicate(s) across pages (ids: ${beforeIds}->${afterIds}, snippets: ${beforeSnips}->${afterSnips})" }
        }

        val prevKey = if (page == 0) null else page - 1
        val nextKey = if (results.size < pageSize) null else page + 1

        LoadResult.Page(
            data = newResults,
            prevKey = prevKey,
            nextKey = nextKey
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }
}
