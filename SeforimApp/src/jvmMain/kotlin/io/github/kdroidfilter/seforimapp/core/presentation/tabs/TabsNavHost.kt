package io.github.kdroidfilter.seforimapp.core.presentation.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.kdroidfilter.seforim.navigation.TabNavControllerRegistry
import io.github.kdroidfilter.seforim.navigation.nonAnimatedComposable
import io.github.kdroidfilter.seforim.tabs.TabsDestination
import io.github.kdroidfilter.seforim.tabs.TabsViewModel
import io.github.kdroidfilter.seforimapp.core.settings.AppSettings
import io.github.kdroidfilter.seforimapp.features.bookcontent.BookContentScreen
import io.github.kdroidfilter.seforimapp.framework.di.LocalAppGraph
import org.jetbrains.jewel.foundation.modifier.trackActivation
import org.jetbrains.jewel.foundation.theme.JewelTheme

@Composable
fun TabsNavHost() {
    val appGraph = LocalAppGraph.current
    val tabsViewModel: TabsViewModel = appGraph.tabsViewModel

    val tabs by tabsViewModel.tabs.collectAsState()
    val selectedTabIndex by tabsViewModel.selectedTabIndex.collectAsState()
    val ramSaverEnabled by AppSettings.ramSaverEnabledFlow.collectAsState()

    val registry: TabNavControllerRegistry = appGraph.tabNavControllerRegistry

    if (ramSaverEnabled) {
        // RAM Saver: single NavHost shared across tabs, navigate on selection/destination change
        val navController = rememberNavController()
        var lastSelectedId by remember { mutableStateOf<Int?>(null) }
        var lastNavigatedDest by remember { mutableStateOf<TabsDestination?>(null) }

        // Register controller for the currently selected tab id
        LaunchedEffect(selectedTabIndex, tabs) {
            val current = tabs.getOrNull(selectedTabIndex)
            if (current != null) {
                lastSelectedId?.let { registry.remove(it) }
                registry.set(current.id, navController)
            }
        }

        // Navigate when selection changes
        LaunchedEffect(selectedTabIndex) {
            val current = tabs.getOrNull(selectedTabIndex) ?: return@LaunchedEffect
            navController.navigate(current.destination)
            lastNavigatedDest = current.destination
        }
        // Navigate when the destination of the selected tab changes
        LaunchedEffect(tabs, selectedTabIndex) {
            val current = tabs.getOrNull(selectedTabIndex) ?: return@LaunchedEffect
            if (current.destination != lastNavigatedDest) {
                navController.navigate(current.destination)
                lastNavigatedDest = current.destination
            }
        }

        NavHost(
            navController = navController,
            startDestination = tabs.firstOrNull()?.destination ?: TabsDestination.Home(tabId = "default"),
            modifier = Modifier
                .trackActivation()
                .fillMaxSize()
                .background(JewelTheme.globalColors.panelBackground)
        ) {
            // Home destination renders the BookContent screen shell.
            nonAnimatedComposable<TabsDestination.Home> { backStackEntry ->
                val destination = backStackEntry.toRoute<TabsDestination.Home>()
                backStackEntry.savedStateHandle["tabId"] = destination.tabId
                val viewModel = remember(appGraph, destination) {
                    appGraph.bookContentViewModel(backStackEntry.savedStateHandle)
                }
                BookContentScreen(viewModel)
            }
            nonAnimatedComposable<TabsDestination.Search> { backStackEntry ->
                val destination = backStackEntry.toRoute<TabsDestination.Search>()
                backStackEntry.savedStateHandle["tabId"] = destination.tabId
                backStackEntry.savedStateHandle["searchQuery"] = destination.searchQuery
                val viewModel = remember(appGraph, destination) {
                    appGraph.searchResultViewModel(backStackEntry.savedStateHandle)
                }
                val bookVm = remember(appGraph, destination) {
                    appGraph.bookContentViewModel(backStackEntry.savedStateHandle)
                }
                val bcUiState by bookVm.uiState.collectAsState()
                io.github.kdroidfilter.seforimapp.features.search.SearchResultInBookShell(
                    bookUiState = bcUiState,
                    onEvent = bookVm::onEvent,
                    viewModel = viewModel
                )
            }
            nonAnimatedComposable<TabsDestination.BookContent> { backStackEntry ->
                val destination = backStackEntry.toRoute<TabsDestination.BookContent>()
                backStackEntry.savedStateHandle["tabId"] = destination.tabId
                if (destination.bookId > 0) backStackEntry.savedStateHandle["bookId"] = destination.bookId
                destination.lineId?.let { backStackEntry.savedStateHandle["lineId"] = it }
                val viewModel = remember(appGraph, destination) {
                    appGraph.bookContentViewModel(backStackEntry.savedStateHandle)
                }
                BookContentScreen(viewModel)
            }
        }
    } else {
        // Classic: one NavHost per tab
        Box(
            modifier = Modifier
                .trackActivation()
                .fillMaxSize()
                .background(JewelTheme.globalColors.panelBackground)
        ) {
            tabs.forEachIndexed { index, tabItem ->
                key(tabItem.id) {
                    val navController = rememberNavController()
                    DisposableEffect(tabItem.id) {
                        registry.set(tabItem.id, navController)
                        onDispose { registry.remove(tabItem.id) }
                    }
                    val isSelected = index == selectedTabIndex
                    var lastNavigatedDestination = remember(tabItem.id) { tabItem.destination }
                    LaunchedEffect(tabItem.destination) {
                        if (tabItem.destination != lastNavigatedDestination) {
                            navController.navigate(tabItem.destination)
                            lastNavigatedDestination = tabItem.destination
                        }
                    }
                    NavHost(
                        navController = navController,
                        startDestination = tabItem.destination,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { alpha = if (isSelected) 1f else 0f }
                            .zIndex(if (isSelected) 1f else 0f)
                    ) {
                        nonAnimatedComposable<TabsDestination.Home> { backStackEntry ->
                            val destination = backStackEntry.toRoute<TabsDestination.Home>()
                            backStackEntry.savedStateHandle["tabId"] = destination.tabId
                            val viewModel = remember(appGraph, destination) {
                                appGraph.bookContentViewModel(backStackEntry.savedStateHandle)
                            }
                            BookContentScreen(viewModel)
                        }
                        nonAnimatedComposable<TabsDestination.Search> { backStackEntry ->
                            val destination = backStackEntry.toRoute<TabsDestination.Search>()
                            backStackEntry.savedStateHandle["tabId"] = destination.tabId
                            backStackEntry.savedStateHandle["searchQuery"] = destination.searchQuery
                            val viewModel = remember(appGraph, destination) {
                                appGraph.searchResultViewModel(backStackEntry.savedStateHandle)
                            }
                            val bookVm = remember(appGraph, destination) {
                                appGraph.bookContentViewModel(backStackEntry.savedStateHandle)
                            }
                            val bcUiState by bookVm.uiState.collectAsState()
                            io.github.kdroidfilter.seforimapp.features.search.SearchResultInBookShell(
                                bookUiState = bcUiState,
                                onEvent = bookVm::onEvent,
                                viewModel = viewModel
                            )
                        }
                        nonAnimatedComposable<TabsDestination.BookContent> { backStackEntry ->
                            val destination = backStackEntry.toRoute<TabsDestination.BookContent>()
                            backStackEntry.savedStateHandle["tabId"] = destination.tabId
                            if (destination.bookId > 0) backStackEntry.savedStateHandle["bookId"] = destination.bookId
                            destination.lineId?.let { backStackEntry.savedStateHandle["lineId"] = it }
                            val viewModel = remember(appGraph, destination) {
                                appGraph.bookContentViewModel(backStackEntry.savedStateHandle)
                            }
                            BookContentScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}
