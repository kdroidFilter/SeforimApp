package io.github.kdroidfilter.seforimapp.core.presentation.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.kdroidfilter.seforimapp.core.presentation.navigation.NavigationAction
import io.github.kdroidfilter.seforimapp.core.presentation.navigation.Navigator
import io.github.kdroidfilter.seforimapp.core.presentation.utils.ObserveAsEvents
import org.jetbrains.jewel.ui.component.Text
import org.koin.compose.koinInject

@Composable
fun TabsNavHost(){
    val navigator = koinInject<Navigator>()
    val navController = rememberNavController()

    ObserveAsEvents(flow = navigator.navigationActions) { action ->
        when(action) {
            is NavigationAction.Navigate -> navController.navigate(
                action.destination
            ) {
                action.navOptions(this)
            }
            NavigationAction.NavigateUp -> navController.navigateUp()
        }
    }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect {
            navigator.setCanGoBack(navController.previousBackStackEntry != null)
        }
    }

    NavHost(
        navController = navController,
        startDestination = navigator.startDestination,
        modifier = Modifier
    ) {
        composable<TabsDestination.Home> {
            Text("Home")
        }

    }

}