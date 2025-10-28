package io.github.kdroidfilter.seforimapp.features.settings.fonts

import androidx.compose.runtime.Immutable

@Immutable
data class FontsSettingsState(
    val bookFontCode: String = "notoserifhebrew",
    val commentaryFontCode: String = "notorashihebrew",
    val targumFontCode: String = "notorashihebrew",
) {
    companion object {
        val preview = FontsSettingsState(
            bookFontCode = "notoserifhebrew",
            commentaryFontCode = "notorashihebrew",
            targumFontCode = "notorashihebrew",
        )
    }
}

