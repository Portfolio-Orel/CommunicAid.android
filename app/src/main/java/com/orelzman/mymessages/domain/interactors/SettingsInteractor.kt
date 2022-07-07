package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.model.entities.SettingsKeys

interface SettingsInteractor {
    fun getSettings(key: SettingsKeys): Settings?
    suspend fun getAllSettings(userId: String): List<Settings>
    suspend fun createSettings(settings: Settings, userId: String)
}