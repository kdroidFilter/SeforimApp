package io.github.kdroidfilter.seforimapp.core.presentation.navigation

import androidx.navigation.NavOptionsBuilder
import io.github.kdroidfilter.seforimapp.core.presentation.tabs.TabsDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

interface Navigator {
    val startDestination: TabsDestination
    val navigationActions: Flow<NavigationAction>

    suspend fun navigate(
        destination: TabsDestination,
        navOptions: NavOptionsBuilder.() -> Unit = {}
    )

    suspend fun navigateUp()

    val canGoBack: StateFlow<Boolean>

    fun setCanGoBack(value: Boolean)

}

class DefaultNavigator(
    override val startDestination: TabsDestination
): Navigator {
    private val _Tabs_navigationActions =
        MutableSharedFlow<NavigationAction>(extraBufferCapacity = 1)
    override val navigationActions = _Tabs_navigationActions.asSharedFlow()

    override suspend fun navigate(
        destination: TabsDestination,
        navOptions: NavOptionsBuilder.() -> Unit
    ) = _Tabs_navigationActions.emit(
        NavigationAction.Navigate(destination, navOptions)
    )

    override suspend fun navigateUp() =
        _Tabs_navigationActions.emit(NavigationAction.NavigateUp)

    private val _canGoBack = MutableStateFlow(false)
    override val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

     override fun setCanGoBack(value: Boolean) { _canGoBack.value = value }
}