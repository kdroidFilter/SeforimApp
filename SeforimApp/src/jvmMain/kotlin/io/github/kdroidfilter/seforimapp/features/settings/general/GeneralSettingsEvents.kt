package io.github.kdroidfilter.seforimapp.features.settings.general

sealed interface GeneralSettingsEvents {
    data class SetCloseTreeOnNewBook(val value: Boolean) : GeneralSettingsEvents
    data class SetPersistSession(val value: Boolean) : GeneralSettingsEvents
    data class SetRamSaver(val value: Boolean) : GeneralSettingsEvents
    data object ResetApp : GeneralSettingsEvents
}
