package io.github.kdroidfilter.seforimapp.core.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ZoomOut: ImageVector
    get() {
        if (_ZoomOut != null) return _ZoomOut!!
        
        _ZoomOut = ImageVector.Builder(
            name = "ZoomOut",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f,
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(12.027f, 6.149f)
                arcToRelative(5.52f, 5.52f, 0f, false, true, -1.27f, 3.908f)
                lineToRelative(4.26f, 4.26f)
                lineToRelative(-0.7f, 0.71f)
                lineToRelative(-4.26f, -4.27f)
                arcToRelative(5.52f, 5.52f, 0f, true, true, 1.97f, -4.608f)
                close()
                moveToRelative(-5.45f, 4.888f)
                arcToRelative(4.51f, 4.51f, 0f, false, false, 3.18f, -1.32f)
                lineToRelative(-0.04f, 0.02f)
                arcToRelative(4.51f, 4.51f, 0f, false, false, 1.36f, -3.2f)
                arcToRelative(4.5f, 4.5f, 0f, true, false, -4.5f, 4.5f)
                close()
                moveToRelative(-2.54f, -4.98f)
                horizontalLineToRelative(5f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(-5f)
                verticalLineToRelative(-1f)
                close()
            }
        }.build()
        
        return _ZoomOut!!
    }

private var _ZoomOut: ImageVector? = null

