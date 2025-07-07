import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import com.kdroid.composetray.utils.SingleInstanceManager
import io.github.kdroidfilter.platformtools.darkmodedetector.isSystemInDarkMode
import io.github.kdroidfilter.seforimapp.App
import io.github.kdroidfilter.seforimapp.core.presentation.utils.getCenteredWindowState
import io.github.kdroidfilter.seforimapp.core.presentation.utils.processKeyShortcuts
import io.github.kdroidfilter.seforimapp.framework.di.desktopModule
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.foundation.modifier.trackActivation
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.lightWithLightHeader
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.TitleBarStyle
import org.koin.compose.KoinApplication
import seforimapp.composeapp.generated.resources.Res
import seforimapp.composeapp.generated.resources.app_name
import java.awt.Dimension
import java.awt.Window
import java.util.Locale

fun main() {
    Locale.setDefault(Locale("he","il"))
    application {
        val windowState = remember { getCenteredWindowState(1280, 720) }
        var isWindowVisible by remember { mutableStateOf(true) }
        val isDarkTheme = isSystemInDarkMode()

        val isSingleInstance = SingleInstanceManager.isSingleInstance(onRestoreRequest = {
            isWindowVisible = true
            windowState.isMinimized = false
            Window.getWindows().first().toFront()
        })
        if (!isSingleInstance) {
            exitApplication()
            return@application
        }
        KoinApplication(application = {
            modules(desktopModule)
        }) {
            IntUiTheme(
                theme = if (isDarkTheme) JewelTheme.darkThemeDefinition() else JewelTheme.lightThemeDefinition(),
                styling = ComponentStyling.default()
                    .decoratedWindow(titleBarStyle = if (isSystemInDarkMode()) TitleBarStyle.dark() else TitleBarStyle.lightWithLightHeader()),
            ) {
                DecoratedWindow(
                    onCloseRequest = { exitApplication() },
                    title = stringResource(Res.string.app_name),
//            icon = painterResource(Res.drawable.icon),
                    state = windowState,
                    visible = isWindowVisible,
                    onKeyEvent = { keyEvent ->
                        processKeyShortcuts(keyEvent = keyEvent, onNavigateTo = {
                            //TODO
                        })
                    },                ) {
                    window.minimumSize = Dimension(350, 600)
                    TitleBar(modifier = Modifier.newFullscreenControls()) {

                    }
                    Column (
                        modifier =
                            Modifier.trackActivation().fillMaxSize().background(JewelTheme.globalColors.panelBackground),
                    ) {

                    }
                }
            }
        }
    }
}