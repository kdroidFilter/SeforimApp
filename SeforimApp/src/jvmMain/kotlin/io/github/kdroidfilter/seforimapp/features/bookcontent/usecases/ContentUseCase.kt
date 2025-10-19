@file:OptIn(ExperimentalSplitPaneApi::class)

package io.github.kdroidfilter.seforimapp.features.bookcontent.usecases

import androidx.paging.Pager
import androidx.paging.PagingData
import io.github.kdroidfilter.seforimapp.features.bookcontent.state.BookContentStateManager
import io.github.kdroidfilter.seforimapp.logger.debugln
import io.github.kdroidfilter.seforimapp.pagination.LinesPagingSource
import io.github.kdroidfilter.seforimapp.pagination.PagingDefaults
import io.github.kdroidfilter.seforimlibrary.core.models.Line
import io.github.kdroidfilter.seforimlibrary.dao.repository.SeforimRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi

/**
 * UseCase pour gérer le contenu du livre et la navigation dans les lignes
 */
class ContentUseCase(
    private val repository: SeforimRepository,
    private val stateManager: BookContentStateManager
) {
    
    /**
     * Construit un Pager pour les lignes du livre
     */
    fun buildLinesPager(
        bookId: Long,
        initialLineId: Long? = null
    ): Flow<PagingData<Line>> {
        return Pager(
            config = PagingDefaults.LINES.config(placeholders = false),
            pagingSourceFactory = {
                LinesPagingSource(repository, bookId, initialLineId)
            }
        ).flow
    }
    
    /**
     * Sélectionne une ligne
     */
    suspend fun selectLine(line: Line) {
        debugln { "[selectLine] Selecting line with id=${line.id}, index=${line.lineIndex}" }
        
        stateManager.updateContent {
            copy(selectedLine = line)
        }
    }
    
    /**
     * Charge et sélectionne une ligne spécifique
     */
    suspend fun loadAndSelectLine(lineId: Long): Line? {
        val line = repository.getLine(lineId)
        
        if (line != null) {
            debugln { "[loadAndSelectLine] Loading line $lineId at index ${line.lineIndex}" }
            
            stateManager.updateContent {
                copy(
                    selectedLine = line,
                    anchorId = line.id,
                    anchorIndex = 0,
                    scrollToLineTimestamp = System.currentTimeMillis()
                )
            }
        }
        
        return line
    }
    
    /**
     * Navigue vers la ligne précédente
     */
    suspend fun navigateToPreviousLine(): Line? {
        val currentState = stateManager.state.first()
        val currentLine = currentState.content.selectedLine ?: return null
        val currentBook = currentState.navigation.selectedBook ?: return null
        
        debugln { "[navigateToPreviousLine] Current line index=${currentLine.lineIndex}" }
        
        // Vérifier qu'on est dans le bon livre
        if (currentLine.bookId != currentBook.id) return null
        
        // Si on est déjà à la première ligne
        if (currentLine.lineIndex <= 0) return null
        
        return try {
            val previousLine = repository.getPreviousLine(currentBook.id, currentLine.lineIndex)
            
            if (previousLine != null) {
                debugln { "[navigateToPreviousLine] Found line at index ${previousLine.lineIndex}" }
                selectLine(previousLine)
                
                stateManager.updateContent {
                    copy(scrollToLineTimestamp = System.currentTimeMillis())
                }
            }
            
            previousLine
        } catch (e: Exception) {
            debugln { "[navigateToPreviousLine] Error: ${e.message}" }
            null
        }
    }
    
    /**
     * Navigue vers la ligne suivante
     */
    suspend fun navigateToNextLine(): Line? {
        val currentState = stateManager.state.first()
        val currentLine = currentState.content.selectedLine ?: return null
        val currentBook = currentState.navigation.selectedBook ?: return null
        
        debugln { "[navigateToNextLine] Current line index=${currentLine.lineIndex}" }
        
        // Vérifier qu'on est dans le bon livre
        if (currentLine.bookId != currentBook.id) return null
        
        return try {
            val nextLine = repository.getNextLine(currentBook.id, currentLine.lineIndex)
            
            if (nextLine != null) {
                debugln { "[navigateToNextLine] Found line at index ${nextLine.lineIndex}" }
                selectLine(nextLine)
                
                stateManager.updateContent {
                    copy(scrollToLineTimestamp = System.currentTimeMillis())
                }
            }
            
            nextLine
        } catch (e: Exception) {
            debugln { "[navigateToNextLine] Error: ${e.message}" }
            null
        }
    }
    
    /**
     * Met à jour la position de scroll du contenu
     */
    fun updateContentScrollPosition(
        anchorId: Long,
        anchorIndex: Int,
        scrollIndex: Int,
        scrollOffset: Int
    ) {
        debugln { "Updating scroll: anchor=$anchorId, anchorIndex=$anchorIndex, scrollIndex=$scrollIndex, offset=$scrollOffset" }
        
        stateManager.updateContent {
            copy(
                anchorId = anchorId,
                anchorIndex = anchorIndex,
                scrollIndex = scrollIndex,
                scrollOffset = scrollOffset
            )
        }
    }
    
    /**
     * Toggle l'affichage des commentaires
     */
    fun toggleCommentaries(): Boolean {
        val currentState = stateManager.state.value
        val isVisible = currentState.content.showCommentaries
        val newPosition: Float
        
        if (isVisible) {
            // Cacher
            val prev = currentState.layout.contentSplitState.positionPercentage
            stateManager.updateLayout {
                copy(
                    previousPositions = previousPositions.copy(
                        content = prev
                    )
                )
            }
            // Fully expand the main content when comments are hidden
            newPosition = 1f
            currentState.layout.contentSplitState.positionPercentage = newPosition
        } else {
            // Montrer
            newPosition = currentState.layout.previousPositions.content
            currentState.layout.contentSplitState.positionPercentage = newPosition
        }
        
        stateManager.updateContent {
            copy(showCommentaries = !isVisible)
        }
        
        return !isVisible
    }
    
    /**
     * Toggle l'affichage des liens/targum
     */
    fun toggleTargum(): Boolean {
        val currentState = stateManager.state.value
        val isVisible = currentState.content.showTargum
        val newPosition: Float
        
        if (isVisible) {
            // Cacher: d'abord sauvegarder la position actuelle, puis réduire
            val prev = currentState.layout.targumSplitState.positionPercentage
            stateManager.updateLayout {
                copy(
                    previousPositions = previousPositions.copy(
                        links = prev
                    )
                )
            }
            // Fully expand the main content when links pane is hidden
            newPosition = 1f
            currentState.layout.targumSplitState.positionPercentage = newPosition
        } else {
            // Montrer: restaurer la dernière position enregistrée
            newPosition = currentState.layout.previousPositions.links
            currentState.layout.targumSplitState.positionPercentage = newPosition
        }
        
        stateManager.updateContent {
            copy(showTargum = !isVisible)
        }
        
        return !isVisible
    }
    
    /**
     * Met à jour les positions de scroll des paragraphes et chapitres
     */
    fun updateParagraphScrollPosition(position: Int) {
        stateManager.updateContent {
            copy(paragraphScrollPosition = position)
        }
    }
    
    fun updateChapterScrollPosition(position: Int) {
        stateManager.updateContent {
            copy(chapterScrollPosition = position)
        }
    }
    
    fun selectChapter(index: Int) {
        stateManager.updateContent {
            copy(selectedChapter = index)
        }
    }
    
    /**
     * Réinitialise les positions de scroll lors du changement de livre
     */
    fun resetScrollPositions() {
        stateManager.updateContent(save = false) {
            copy(
                scrollIndex = 0,
                scrollOffset = 0,
                anchorId = -1L,
                anchorIndex = 0,
                paragraphScrollPosition = 0,
                chapterScrollPosition = 0
            )
        }
    }
}
