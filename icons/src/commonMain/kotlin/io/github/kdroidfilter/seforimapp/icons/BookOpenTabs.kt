package io.github.kdroidfilter.seforimapp.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun BookOpenTabs(tint : Color): ImageVector {
    val _Book_5 = ImageVector.Builder(
        name = "Book_5",
        defaultWidth = 16.dp,
        defaultHeight = 16.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
        autoMirror = true,
        tintColor = tint,
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000))
        ) {
            moveTo(270f, 880f)
            quadToRelative(-45f, 0f, -77.5f, -30.5f)
            reflectiveQuadTo(160f, 774f)
            verticalLineToRelative(-558f)
            quadToRelative(0f, -38f, 23.5f, -68f)
            reflectiveQuadToRelative(61.5f, -38f)
            lineToRelative(395f, -78f)
            verticalLineToRelative(640f)
            lineToRelative(-379f, 76f)
            quadToRelative(-9f, 2f, -15f, 9.5f)
            reflectiveQuadToRelative(-6f, 16.5f)
            quadToRelative(0f, 11f, 9f, 18.5f)
            reflectiveQuadToRelative(21f, 7.5f)
            horizontalLineToRelative(450f)
            verticalLineToRelative(-640f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(720f)
            close()
            moveToRelative(90f, -233f)
            lineToRelative(200f, -39f)
            verticalLineToRelative(-478f)
            lineToRelative(-200f, 39f)
            close()
            moveToRelative(-80f, 16f)
            verticalLineToRelative(-478f)
            lineToRelative(-15f, 3f)
            quadToRelative(-11f, 2f, -18f, 9.5f)
            reflectiveQuadToRelative(-7f, 18.5f)
            verticalLineToRelative(457f)
            quadToRelative(5f, -2f, 10.5f, -3.5f)
            reflectiveQuadTo(261f, 667f)
            close()
            moveToRelative(-40f, -472f)
            verticalLineToRelative(482f)
            close()
        }
    }.build()

    return _Book_5
}


