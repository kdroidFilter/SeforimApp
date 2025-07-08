package io.github.kdroidfilter.seforimapp.framework.di

import androidx.lifecycle.SavedStateHandle
import io.github.kdroidfilter.seforimapp.core.presentation.navigation.DefaultNavigator
import io.github.kdroidfilter.seforimapp.core.presentation.navigation.Navigator
import io.github.kdroidfilter.seforimapp.core.presentation.tabs.TabsDestination
import io.github.kdroidfilter.seforimapp.core.presentation.tabs.TabsViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel
import java.util.UUID

val desktopModule = module {
    single<Navigator> {
        DefaultNavigator(startDestination = TabsDestination.Home(UUID.randomUUID().toString()))
    }
    viewModel {
        TabsViewModel(navigator = get(), )
    }
}