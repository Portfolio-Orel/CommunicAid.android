package com.orelzman.mymessages.data.local.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.model.dto.body.create.CreateOrUpdateSettingsBody
import com.orelzman.mymessages.domain.model.dto.response.toSettings
import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.repository.Repository
import javax.inject.Inject

class SettingsInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : SettingsInteractor {
    val db = database.settingsDao

    override fun getSettings(key: String): Settings? =
        db.get(key)

    override suspend fun getAllSettings(userId: String): List<Settings> {
        val settings = db.getAll()
        if (settings.isEmpty()) {
            val result = repository.getSettings(userId)
            db.insert(result.toSettings())
            return result.toSettings()
        }
        return settings
    }

    override suspend fun createSettings(settings: Settings, userId: String) {
        val createOrUpdateSettingsBody = CreateOrUpdateSettingsBody(
            key = settings.key.keyInServer,
            value = settings.value,
            userId = userId
        )
        repository.createOrUpdateSettings(createOrUpdateSettingsBody)
        db.insert(settings)
    }
}