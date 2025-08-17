package io.github.kdroidfilter.seforimapp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Format_letter_spacing: ImageVector
    get() {
        if (_Format_letter_spacing != null) return _Format_letter_spacing!!
        
        _Format_letter_spacing = ImageVector.Builder(
            name = "Format_letter_spacing",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(320f, 880f)
                lineTo(160f, 720f)
                lineToRelative(160f, -160f)
                lineToRelative(57f, 56f)
                lineToRelative(-64f, 64f)
                horizontalLineToRelative(334f)
                lineToRelative(-63f, -64f)
                lineToRelative(56f, -56f)
                lineToRelative(160f, 160f)
                lineTo(640f, 880f)
                lineToRelative(-57f, -56f)
                lineToRelative(64f, -64f)
                horizontalLineTo(313f)
                lineToRelative(63f, 64f)
                close()
                moveTo(200f, 480f)
                verticalLineToRelative(-400f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(400f)
                close()
                moveToRelative(240f, 0f)
                verticalLineToRelative(-400f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(400f)
                close()
                moveToRelative(240f, 0f)
                verticalLineToRelative(-400f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(400f)
                close()
            }
        }.build()
        
        return _Format_letter_spacing!!
    }

private var _Format_letter_spacing: ImageVector? = null

