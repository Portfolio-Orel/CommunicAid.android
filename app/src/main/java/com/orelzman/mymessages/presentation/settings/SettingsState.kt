package com.orelzman.mymessages.presentation.settings

import com.orelzman.mymessages.domain.model.entities.Settings

data class SettingsState(
    val settingsList: List<Settings> = emptyList(),
    val isLoading: Boolean = false
)