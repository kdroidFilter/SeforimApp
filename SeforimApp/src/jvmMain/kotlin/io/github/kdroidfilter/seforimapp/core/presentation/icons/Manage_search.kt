package io.github.kdroidfilter.seforimapp.core.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Manage_search: ImageVector
    get() {
        if (_Manage_search != null) return _Manage_search!!
        
        _Manage_search = ImageVector.Builder(
            name = "Manage_search",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(80f, 760f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(400f)
                verticalLineToRelative(80f)
                close()
                moveToRelative(0f, -200f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(80f)
                close()
                moveToRelative(0f, -200f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(80f)
                close()
                moveToRelative(744f, 400f)
                lineTo(670f, 606f)
                quadToRelative(-24f, 17f, -52.5f, 25.5f)
                reflectiveQuadTo(560f, 640f)
                quadToRelative(-83f, 0f, -141.5f, -58.5f)
                reflectiveQuadTo(360f, 440f)
                reflectiveQuadToRelative(58.5f, -141.5f)
                reflectiveQuadTo(560f, 240f)
                reflectiveQuadToRelative(141.5f, 58.5f)
                reflectiveQuadTo(760f, 440f)
                quadToRelative(0f, 29f, -8.5f, 57.5f)
                reflectiveQuadTo(726f, 550f)
                lineToRelative(154f, 154f)
                close()
                moveTo(560f, 560f)
                quadToRelative(50f, 0f, 85f, -35f)
                reflectiveQuadToRelative(35f, -85f)
                reflectiveQuadToRelative(-35f, -85f)
                reflectiveQuadToRelative(-85f, -35f)
                reflectiveQuadToRelative(-85f, 35f)
                reflectiveQuadToRelative(-35f, 85f)
                reflectiveQuadToRelative(35f, 85f)
                reflectiveQuadToRelative(85f, 35f)
            }
        }.build()
        
        return _Manage_search!!
    }

private var _Manage_search: ImageVector? = null

