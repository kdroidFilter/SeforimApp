import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.SimpleTabContent
import org.jetbrains.jewel.ui.component.TabData
import org.jetbrains.jewel.ui.component.TabStrip
import org.jetbrains.jewel.ui.component.styling.TabStyle
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.painter.hints.Stateful
import org.jetbrains.jewel.ui.painter.rememberResourcePainterProvider
import org.jetbrains.jewel.ui.theme.defaultTabStyle
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
import kotlin.math.max

fun main() {
    Locale.setDefault(Locale("he", "il"))
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
                    },
                ) {
                    window.minimumSize = Dimension(350, 600)
                    TitleBar(modifier = Modifier.newFullscreenControls()) {


                        BoxWithConstraints {
                            val windowWidth = maxWidth
                            Row(modifier = Modifier.align(Alignment.Start).width(windowWidth - 40.dp)) {
                                DefaultTabShowcase()
                            }
                            Row(
                                modifier = Modifier.align(Alignment.End).fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Salut")

                            }
                        }
                    }

                    Column(
                        modifier =
                            Modifier.trackActivation().fillMaxSize()
                                .background(JewelTheme.globalColors.panelBackground),
                    ) {

                    }
                }
            }
        }
    }
}

@Composable
private fun DefaultTabShowcase() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    var tabIds by remember { mutableStateOf((1..2).toList()) }
    val maxId = remember(tabIds) { tabIds.maxOrNull() ?: 0 }

    val tabs =
        remember(tabIds, selectedTabIndex) {
            tabIds.mapIndexed { index, id ->
                TabData.Default(
                    selected = index == selectedTabIndex,
                    content = { tabState ->
                        val iconProvider = rememberResourcePainterProvider(AllIconsKeys.Actions.Find)
                        val icon by iconProvider.getPainter(Stateful(tabState))
                        SimpleTabContent(label = "Default Tab $id", state = tabState, icon = icon)
                    },
                    onClose = {
                        tabIds = tabIds.toMutableList().apply { removeAt(index) }
                        if (selectedTabIndex >= index) {
                            val maxPossibleIndex = max(0, tabIds.lastIndex)
                            selectedTabIndex = (selectedTabIndex - 1).coerceIn(0..maxPossibleIndex)
                        }
                    },
                    onClick = { selectedTabIndex = index },
                )
            }
        }

    TabStripWithAddButton(tabs = tabs, style = JewelTheme.defaultTabStyle) {
        val insertionIndex = (selectedTabIndex + 1).coerceIn(0..tabIds.size)
        val nextTabId = maxId + 1

        tabIds = tabIds.toMutableList().apply { add(insertionIndex, nextTabId) }
        selectedTabIndex = insertionIndex
    }
}

@Composable
private fun TabStripWithAddButton(tabs: List<TabData>, style: TabStyle, onAddClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        TabStrip(tabs = tabs, style = style, modifier = Modifier.weight(1f))

        IconButton(onClick = onAddClick, modifier = Modifier.size(JewelTheme.defaultTabStyle.metrics.tabHeight)) {
            Icon(key = AllIconsKeys.General.Add, contentDescription = "Add a tab")
        }
    }
}
