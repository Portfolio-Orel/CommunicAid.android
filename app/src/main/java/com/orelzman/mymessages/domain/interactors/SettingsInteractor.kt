package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.model.entities.SettingsKeys

interface SettingsInteractor {
    /**
     * Fetches settings by [key] from the cache.
     * @author Orel Zilberman
     */
    fun getSettings(key: SettingsKeys): Settings?

    /**
     * Saves [settings] in the cache only.
     * @author Orel Zilberman
     */
    fun saveSettings(settings: Settings)

    /**
     * Fetches all settings from backend and caches it.
     * @author Orel Zilberman
     */
    suspend fun init()

    /**
     * Creates new [settings] in the backend and caches it.
     * @author Orel Zilberman
     */
    suspend fun createSettings(settings: Settings)
}