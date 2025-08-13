package io.github.kdroidfilter.seforimapp.core.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Contextual_token: ImageVector
    get() {
        if (_Contextual_token != null) return _Contextual_token!!
        
        _Contextual_token = ImageVector.Builder(
            name = "Contextual_token",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(240f, 640f)
                horizontalLineToRelative(280f)
                verticalLineToRelative(-120f)
                horizontalLineTo(240f)
                close()
                moveToRelative(360f, 0f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(-320f)
                horizontalLineTo(600f)
                close()
                moveTo(240f, 440f)
                horizontalLineToRelative(280f)
                verticalLineToRelative(-120f)
                horizontalLineTo(240f)
                close()
                moveToRelative(-80f, 360f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(80f, 720f)
                verticalLineToRelative(-480f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(160f, 160f)
                horizontalLineToRelative(640f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(880f, 240f)
                verticalLineToRelative(480f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(800f, 800f)
                close()
                moveToRelative(0f, -80f)
                horizontalLineToRelative(640f)
                verticalLineToRelative(-480f)
                horizontalLineTo(160f)
                close()
                moveToRelative(0f, 0f)
                verticalLineToRelative(-480f)
                close()
            }
        }.build()
        
        return _Contextual_token!!
    }

private var _Contextual_token: ImageVector? = null

