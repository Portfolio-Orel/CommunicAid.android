package com.orels.presentation.ui.settings

import com.orels.domain.model.entities.Settings
import com.orels.domain.model.entities.SettingsKey

data class SettingsState(
    val settingsList: List<Settings> = emptyList(),
    val updatedSettings: List<Settings> = emptyList(),
    val eventSettings: EventsSettings? = null,

    val loadingSettings: List<SettingsKey> = emptyList(),
    val settingsWaitingForPermissions: List<Settings> = emptyList(),

    val isLoadingSignOut: Boolean = false
)

enum class EventsSettings {
    Saved,
    Unchanged,
    Error;
}