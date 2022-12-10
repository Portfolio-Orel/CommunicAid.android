package com.orels.presentation.ui.settings

import com.orels.domain.model.entities.Settings
import com.orels.domain.model.entities.SettingsKey

data class SettingsState(
    val settingsList: List<Settings> = emptyList(),
    val updatedSettings: List<Settings> = emptyList(),

    val loadingSettings: List<SettingsKey> = emptyList(),
    val settingsWaitingForPermissions: List<Settings> = emptyList(),

    val isLoadingSettings: Boolean = false,
    val isLoadingSignOut: Boolean = false
)