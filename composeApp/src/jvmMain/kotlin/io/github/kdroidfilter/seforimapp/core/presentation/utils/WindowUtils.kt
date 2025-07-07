package io.github.kdroidfilter.seforimapp.core.presentation.utils

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import java.awt.Toolkit

fun getCenteredWindowState(width: Int, height: Int): WindowState {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val windowX = (screenSize.width - width) / 2
    val windowY = (screenSize.height - height) / 2

    return WindowState(
        size = DpSize(width.dp, height.dp),
        position = WindowPosition(windowX.dp, windowY.dp)
    )
}

fun processKeyShortcuts(keyEvent: KeyEvent, onNavigateTo: (String) -> Unit): Boolean {
    if (!keyEvent.isAltPressed || keyEvent.type != KeyEventType.KeyDown) return false
    return when (keyEvent.key) {
        Key.W -> {
            onNavigateTo("Welcome")
            true
        }

        Key.M -> {
            onNavigateTo("Markdown")
            true
        }

        Key.C -> {
            onNavigateTo("Components")
            true
        }

        else -> false
    }
}