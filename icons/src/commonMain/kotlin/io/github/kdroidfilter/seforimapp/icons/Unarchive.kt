package io.github.kdroidfilter.seforimapp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Unarchive: ImageVector
    get() {
        if (_Unarchive != null) return _Unarchive!!
        
        _Unarchive = ImageVector.Builder(
            name = "Unarchive",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(480f, 400f)
                lineTo(320f, 560f)
                lineToRelative(56f, 56f)
                lineToRelative(64f, -64f)
                verticalLineToRelative(168f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(-168f)
                lineToRelative(64f, 64f)
                lineToRelative(56f, -56f)
                close()
                moveToRelative(-280f, -80f)
                verticalLineToRelative(440f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(-440f)
                close()
                moveToRelative(0f, 520f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(120f, 760f)
                verticalLineToRelative(-499f)
                quadToRelative(0f, -14f, 4.5f, -27f)
                reflectiveQuadToRelative(13.5f, -24f)
                lineToRelative(50f, -61f)
                quadToRelative(11f, -14f, 27.5f, -21.5f)
                reflectiveQuadTo(250f, 120f)
                horizontalLineToRelative(460f)
                quadToRelative(18f, 0f, 34.5f, 7.5f)
                reflectiveQuadTo(772f, 149f)
                lineToRelative(50f, 61f)
                quadToRelative(9f, 11f, 13.5f, 24f)
                reflectiveQuadToRelative(4.5f, 27f)
                verticalLineToRelative(499f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(760f, 840f)
                close()
                moveToRelative(16f, -600f)
                horizontalLineToRelative(528f)
                lineToRelative(-34f, -40f)
                horizontalLineTo(250f)
                close()
                moveToRelative(264f, 300f)
            }
        }.build()
        
        return _Unarchive!!
    }

private var _Unarchive: ImageVector? = null

