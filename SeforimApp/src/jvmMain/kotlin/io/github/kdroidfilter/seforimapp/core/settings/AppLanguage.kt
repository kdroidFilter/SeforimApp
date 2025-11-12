package io.github.kdroidfilter.seforimapp.core.settings

enum class AppLanguage(val languageCode: String) {
    HEBREW("he"),
    ENGLISH("en");

    companion object {
        fun fromCode(code: String?): AppLanguage = when (code) {
            ENGLISH.languageCode -> ENGLISH
            else -> HEBREW
        }
    }
}
