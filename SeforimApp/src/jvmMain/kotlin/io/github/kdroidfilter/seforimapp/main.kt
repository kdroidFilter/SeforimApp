package io.github.kdroidfilter.seforimapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.kdroid.composetray.tray.api.ExperimentalTrayAppApi
import com.kdroid.composetray.utils.SingleInstanceManager
import dev.zacsweers.metro.createGraph
import io.github.kdroidfilter.platformtools.darkmodedetector.mac.setMacOsAdaptiveTitleBar
import io.github.kdroidfilter.seforimapp.core.MainAppState
import io.github.kdroidfilter.seforimapp.core.presentation.components.MainTitleBar
import io.github.kdroidfilter.seforimapp.core.presentation.tabs.TabsNavHost
import io.github.kdroidfilter.seforimapp.core.presentation.theme.ThemeUtils
import io.github.kdroidfilter.seforimapp.core.presentation.utils.processKeyShortcuts
import io.github.kdroidfilter.seforimapp.core.settings.AppSettings
import io.github.kdroidfilter.seforimapp.features.onboarding.OnBoardingWindow
import io.github.kdroidfilter.seforimapp.framework.database.getDatabasePath
import io.github.kdroidfilter.seforimapp.framework.di.AppGraph
import io.github.kdroidfilter.seforimapp.framework.di.LocalAppGraph
import io.github.kdroidfilter.seforimapp.framework.session.SessionManager
import io.github.vinceglb.filekit.FileKit
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.DecoratedWindowScope
import seforimapp.seforimapp.generated.resources.AppIcon
import seforimapp.seforimapp.generated.resources.Res
import seforimapp.seforimapp.generated.resources.app_name
import java.awt.Dimension
import java.awt.Window
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalTrayAppApi::class)
fun main() {
    setMacOsAdaptiveTitleBar()
    //    val enableLogs = System.getenv("ENABLE_LOGS")
    //    if (enableLogs == null) {
    //        SilenceLogs.everything(hardMuteStdout = true, hardMuteStderr = true)
    //    }


    val appId = "io.github.kdroidfilter.seforimapp"
    SingleInstanceManager.configuration = SingleInstanceManager.Configuration(
        lockIdentifier = appId
    )

    Locale.setDefault(Locale.Builder().setLanguage("he").build())
    application {
        FileKit.init(appId)

        val windowState = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(1280.dp, 720.dp)
        )

        var isWindowVisible by remember { mutableStateOf(true) }

        val mainState = MainAppState
        val showOnboarding: Boolean? = mainState.showOnBoarding.collectAsState().value

        val isSingleInstance = SingleInstanceManager.isSingleInstance(onRestoreRequest = {
            isWindowVisible = true
            windowState.isMinimized = false
            Window.getWindows().first().toFront()
        })
        if (!isSingleInstance) {
            exitApplication()
            return@application
        }

        // Create the application graph via Metro and expose via CompositionLocal
        val appGraph = remember { createGraph<AppGraph>() }
        // Ensure AppSettings uses the DI-provided Settings immediately
        AppSettings.initialize(appGraph.settings)

        CompositionLocalProvider(LocalAppGraph provides appGraph) {
            val themeDefinition = ThemeUtils.buildThemeDefinition()

            IntUiTheme(
                theme = themeDefinition, styling = ComponentStyling.default().decoratedWindow(
                    titleBarStyle = ThemeUtils.pickTitleBarStyle(),
                )
            ) {
                // Decide whether to show onboarding based on database availability and completion flag
                LaunchedEffect(Unit) {
                    try {
                        // getDatabasePath() throws if not configured or file missing
                        getDatabasePath()
                        // If DB exists, show onboarding only if not finished yet
                        val finished = AppSettings.isOnboardingFinished()
                        mainState.setShowOnBoarding(!finished)
                    } catch (_: Exception) {
                        // If DB is missing/unconfigured, show onboarding
                        mainState.setShowOnBoarding(true)
                    }
                }

                if (showOnboarding == true) {
                    OnBoardingWindow()
                } else if (showOnboarding == false) {
                    DecoratedWindow(
                        onCloseRequest = {
                            // Persist session if enabled, then exit
                            SessionManager.saveIfEnabled(appGraph)
                            exitApplication()
                        },
                        title = stringResource(Res.string.app_name),
                        icon = painterResource(Res.drawable.AppIcon),
                        state = windowState,
                        visible = isWindowVisible,
                        onKeyEvent = { keyEvent ->
                            processKeyShortcuts(
                                keyEvent = keyEvent, onNavigateTo = {
                                    //TODO
                                })
                        },
                    ) {
                        /**
                         * A hack to work around the window flashing its background color when closed
                         * (https://youtrack.jetbrains.com/issue/CMP-5651).
                         */
                        val background = JewelTheme.globalColors.panelBackground
                        LaunchedEffect(window, background) {
                            window.background = java.awt.Color(background.toArgb())
                        }


                        LaunchedEffect(Unit) {
                            window.minimumSize = Dimension(600, 300)
                        }
                        MainTitleBar()

                        // Restore previously saved session once when main window becomes active
                        var sessionRestored by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            if (!sessionRestored) {
                                SessionManager.restoreIfEnabled(appGraph)
                                sessionRestored = true
                            }
                        }
                        TabsNavHost()
                    }
                } // else (null) -> render nothing until decision made
            }
        }
    }
}

/**
 * A hack to work around the window flashing its background color when closed
 * (https://youtrack.jetbrains.com/issue/CMP-5651).
 */
@Composable
fun DecoratedWindowScope.windowBackgroundFlashingOnCloseWorkaround(background: Color) {
    LaunchedEffect(window, background) {
        window.background = java.awt.Color(background.toArgb())
    }
}