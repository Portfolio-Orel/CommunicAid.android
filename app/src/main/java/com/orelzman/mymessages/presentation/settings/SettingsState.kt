package com.orelzman.mymessages.presentation.settings

import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.model.entities.SettingsKey

data class SettingsState(
    val settingsList: List<Settings> = emptyList(),
    val updatedSettings: List<SettingsKey> = emptyList(),
    val eventSettings: EventsSettings? = null,

    val isLoading: Boolean = false
)

enum class EventsSettings {
    Saved,
    Unchanged,
    Error;
}