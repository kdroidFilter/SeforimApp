package io.github.kdroidfilter.seforimapp.core.presentation.tabs

import androidx.lifecycle.ViewModel
import io.github.kdroidfilter.seforimapp.core.presentation.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max

data class TabItem(
    val id: Int,
    val title: String = "Default Tab",
    val destination: TabsDestination = TabsDestination.Home
)


class TabsViewModel(
    private val navigator: Navigator,
) : ViewModel() {

    private var nextTabId = 1

    private val _tabs = MutableStateFlow(listOf(TabItem(id = 1, title = "Default Tab 1")))
    val tabs = _tabs.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex = _selectedTabIndex.asStateFlow()

    fun onEvent(event: TabsEvents) {
        when (event) {
            is TabsEvents.onClose -> {
                closeTab(event.index)
            }
            is TabsEvents.onSelected -> {
                selectTab(event.index)
            }
            TabsEvents.onAdd -> {
                addTab()
            }
        }
    }

    private fun closeTab(index: Int) {
        val currentTabs = _tabs.value
        if (currentTabs.size <= 1) return // Ne pas fermer le dernier onglet

        if (index < 0 || index >= currentTabs.size) return // Index invalide

        // Supprimer l'onglet à l'index donné
        val newTabs = currentTabs.toMutableList().apply { removeAt(index) }
        _tabs.value = newTabs

        // Ajuster l'index sélectionné
        val currentSelectedIndex = _selectedTabIndex.value
        val newSelectedIndex = when {
            // Si on ferme l'onglet sélectionné
            index == currentSelectedIndex -> {
                // Si on ferme le dernier onglet, sélectionner le précédent
                if (index == newTabs.size) {
                    max(0, index - 1)
                } else {
                    // Sinon, garder le même index (qui pointera vers l'onglet suivant)
                    index.coerceIn(0, newTabs.lastIndex)
                }
            }
            // Si on ferme un onglet avant celui sélectionné
            index < currentSelectedIndex -> currentSelectedIndex - 1
            // Si on ferme un onglet après celui sélectionné
            else -> currentSelectedIndex
        }

        _selectedTabIndex.value = newSelectedIndex
    }

    private fun selectTab(index: Int) {
        val currentTabs = _tabs.value
        if (index in 0..currentTabs.lastIndex) {
            _selectedTabIndex.value = index
        }
    }

    private fun addTab() {
        val newTab = TabItem(
            id = nextTabId++,
            title = "Default Tab $nextTabId"
        )
        _tabs.value = _tabs.value + newTab
        _selectedTabIndex.value = _tabs.value.lastIndex
    }
}