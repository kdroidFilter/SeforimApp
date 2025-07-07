// tabs/TabsNavHost.kt
package io.github.kdroidfilter.seforimapp.core.presentation.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.kdroidfilter.seforimapp.core.presentation.navigation.NavigationAction
import io.github.kdroidfilter.seforimapp.core.presentation.navigation.Navigator
import io.github.kdroidfilter.seforimapp.core.presentation.navigation.animatedComposable
import io.github.kdroidfilter.seforimapp.core.presentation.utils.ObserveAsEvents
import kotlinx.coroutines.launch
import org.jetbrains.jewel.ui.component.Text
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TabsNavHost() {
    val navigator = koinInject<Navigator>()
    val tabsViewModel: TabsViewModel = koinViewModel()
    val navController = rememberNavController()

    val tabs by tabsViewModel.tabs.collectAsState()
    val selectedTabIndex by tabsViewModel.selectedTabIndex.collectAsState()

    // Observer les changements d'onglets et naviguer vers la destination
    LaunchedEffect(selectedTabIndex) {
        if (tabs.isNotEmpty() && selectedTabIndex < tabs.size) {
            val currentTab = tabs[selectedTabIndex]
            navController.navigate(currentTab.destination)
        }
    }

    ObserveAsEvents(flow = navigator.navigationActions) { action ->
        when (action) {
            is NavigationAction.Navigate -> {
                // La navigation est maintenant gérée par les onglets
                // On pourrait ignorer cela ou l'utiliser pour d'autres cas
            }

            NavigationAction.NavigateUp -> navController.navigateUp()
        }
    }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect {
            navigator.setCanGoBack(navController.previousBackStackEntry != null)
        }
    }
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = navigator.startDestination,
        modifier = Modifier
    ) {
        animatedComposable<TabsDestination.Home> {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Button({
                    scope.launch {
                        navigator.navigate(TabsDestination.Search("Search Page"))
                    }
                }) {
                    Text("Click me")
                }
            }
        }

        animatedComposable<TabsDestination.Search> { backStackEntry ->
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                val destination = backStackEntry.toRoute<TabsDestination.Search>()
                Column {
                    Text("Search Page: ${destination.searchQuery}")
                    Text(selectedTabIndex.toString())
                    Button({
                        scope.launch {
                            navigator.navigate(TabsDestination.BookContent(123))
                        }
                    }) {
                        Text("Open Book Content")
                    }
                }
            }
        }

        animatedComposable<TabsDestination.BookContent> { backStackEntry ->
            val destination = backStackEntry.toRoute<TabsDestination.BookContent>()
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {

                Column {
                    Text("Book Content: ${destination.bookId}")
                }
            }
        }
    }
}