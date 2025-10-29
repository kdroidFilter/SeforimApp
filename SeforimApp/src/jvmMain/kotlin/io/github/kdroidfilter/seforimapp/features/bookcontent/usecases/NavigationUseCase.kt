@file:OptIn(ExperimentalSplitPaneApi::class)

package io.github.kdroidfilter.seforimapp.features.bookcontent.usecases

import io.github.kdroidfilter.seforimapp.features.bookcontent.state.BookContentStateManager
import io.github.kdroidfilter.seforimlibrary.core.models.Book
import io.github.kdroidfilter.seforimlibrary.core.models.Category
import io.github.kdroidfilter.seforimlibrary.dao.repository.SeforimRepository
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi

/**
 * UseCase pour gérer la navigation dans l'arbre des catégories et livres
 */
class NavigationUseCase(
    private val repository: SeforimRepository,
    private val stateManager: BookContentStateManager
) {
    
    /**
     * Charge les catégories racine
     */
    suspend fun loadRootCategories() {
        val rootCategories = repository.getRootCategories()
        
        stateManager.updateNavigation {
            copy(rootCategories = rootCategories)
        }
        
        // Si des catégories étaient déjà expandées, recharger leur sous-arbre minimal
        // (enfants directs + livres) afin que la vue arborescente puisse s’afficher immédiatement
        // au retour d’onglet/restauration de session.
        val expandedCategories = stateManager.state.first().navigation.expandedCategories
        if (expandedCategories.isNotEmpty()) {
            val booksToLoad = mutableSetOf<Book>()
            val childrenMap = mutableMapOf<Long, List<Category>>()

            suspend fun restoreExpandedSubtree(categoryId: Long, guard: Int = 0) {
                if (guard > 512) return // sécurité contre les cycles
                // Livres dans la catégorie
                runCatching { repository.getBooksByCategory(categoryId) }
                    .onSuccess { if (it.isNotEmpty()) booksToLoad.addAll(it) }
                // Enfants directs
                val children = runCatching { repository.getCategoryChildren(categoryId) }
                    .getOrElse { emptyList() }
                if (children.isNotEmpty()) {
                    childrenMap[categoryId] = children
                }
                // Si un enfant est également marqué comme expanded, restaurer récursivement
                children.forEach { child ->
                    if (expandedCategories.contains(child.id)) {
                        restoreExpandedSubtree(child.id, guard + 1)
                    }
                }
            }

            // Restaurer pour chaque catégorie marquée expanded
            expandedCategories.forEach { categoryId ->
                runCatching { restoreExpandedSubtree(categoryId) }
            }

            if (booksToLoad.isNotEmpty() || childrenMap.isNotEmpty()) {
                stateManager.updateNavigation {
                    copy(
                        booksInCategory = booksInCategory + booksToLoad,
                        categoryChildren = categoryChildren + childrenMap
                    )
                }
            }
        }
    }
    
    /**
     * Sélectionne une catégorie
     */
    suspend fun selectCategory(category: Category) {
        stateManager.updateNavigation {
            copy(selectedCategory = category)
        }
        expandCategory(category)
    }
    
    /**
     * Expand/collapse une catégorie
     */
    suspend fun expandCategory(category: Category) {
        val currentState = stateManager.state.first().navigation
        val isExpanded = currentState.expandedCategories.contains(category.id)
        
        if (isExpanded) {
            // Collapse
            stateManager.updateNavigation {
                copy(expandedCategories = expandedCategories - category.id)
            }
        } else {
            // Expand - charger les enfants et les livres si nécessaire
            val needsChildrenLoad = !currentState.categoryChildren.containsKey(category.id)
            
            if (needsChildrenLoad) {
                val children = repository.getCategoryChildren(category.id)
                val books = repository.getBooksByCategory(category.id)
                
                stateManager.updateNavigation {
                    copy(
                        expandedCategories = expandedCategories + category.id,
                        categoryChildren = if (children.isNotEmpty()) {
                            categoryChildren + (category.id to children)
                        } else categoryChildren,
                        booksInCategory = if (books.isNotEmpty()) {
                            booksInCategory + books
                        } else booksInCategory
                    )
                }
            } else {
                // Juste toggle l'expansion
                stateManager.updateNavigation {
                    copy(expandedCategories = expandedCategories + category.id)
                }
            }
        }
    }
    
    /**
     * Met à jour le texte de recherche
     */
    fun updateSearchText(text: String) {
        stateManager.updateNavigation {
            copy(searchText = text)
        }
    }
    
    /**
     * Toggle la visibilité de l'arbre de navigation
     */
    fun toggleBookTree(): Float {
        val currentState = stateManager.state.value
        val isVisible = currentState.navigation.isVisible
        val newPosition: Float
        
        if (isVisible) {
            // Cacher - sauvegarder la position actuelle
            val prev = currentState.layout.mainSplitState.positionPercentage
            stateManager.updateLayout {
                copy(
                    previousPositions = previousPositions.copy(
                        main = prev
                    )
                )
            }
            newPosition = 0f
            currentState.layout.mainSplitState.positionPercentage = newPosition
        } else {
            // Montrer - restaurer la position précédente
            newPosition = currentState.layout.previousPositions.main
            currentState.layout.mainSplitState.positionPercentage = newPosition
        }
        
        stateManager.updateNavigation {
            copy(isVisible = !isVisible)
        }
        
        return newPosition
    }
    
    /**
     * Met à jour la position de scroll de l'arbre
     */
    fun updateBookTreeScrollPosition(index: Int, offset: Int) {
        stateManager.updateNavigation {
            copy(
                scrollIndex = index,
                scrollOffset = offset
            )
        }
    }
    
    /**
     * Sélectionne un livre
     */
    fun selectBook(book: Book) {
        stateManager.updateNavigation {
            copy(selectedBook = book)
        }
    }
}
