package com.orelzman.mymessages.presentation.settings

import com.orelzman.mymessages.domain.model.entities.Settings

data class SettingsState(
    val settingsList: List<Settings> = emptyList(),
    val isUpdated: Boolean = false,
    val eventSettings: EventsSettings? = null,

    val isLoading: Boolean = false
)

enum class EventsSettings {
    Saved,
    Unchanged,
    Error;
}