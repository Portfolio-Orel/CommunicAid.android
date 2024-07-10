package com.orels.presentation.ui.settings

import com.orels.auth.domain.model.User
import com.orels.domain.model.entities.Settings
import com.orels.domain.model.entities.SettingsKey
import com.orels.domain.repository.Environments

data class SettingsState(
    val user: User? = null,

    val settingsList: List<Settings> = emptyList(),
    val updatedSettings: List<Settings> = emptyList(),

    val loadingSettings: List<SettingsKey> = emptyList(),
    val settingsWaitingForPermissions: List<Settings> = emptyList(),

    val isLoadingSettings: Boolean = false,
    val isLoadingSignOut: Boolean = false,

    val environment: Environments? = null
)