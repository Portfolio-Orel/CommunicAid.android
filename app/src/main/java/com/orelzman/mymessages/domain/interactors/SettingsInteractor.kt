package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.Settings

interface SettingsInteractor {
    fun getSettings(key: String): Settings?
    suspend fun getAllSettings(userId: String): List<Settings>
    suspend fun createSettings(settings: Settings, userId: String)
}