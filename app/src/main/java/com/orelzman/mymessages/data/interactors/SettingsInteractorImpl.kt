package com.orelzman.mymessages.data.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.model.dto.body.create.CreateOrUpdateSettingsBody
import com.orelzman.mymessages.domain.model.dto.response.toSettings
import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.model.entities.SettingsKeys
import com.orelzman.mymessages.domain.repository.Repository
import javax.inject.Inject

class SettingsInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : SettingsInteractor {
    val db = database.settingsDao

    override fun getSettings(key: SettingsKeys): Settings? =
        db.get(key)

    override suspend fun init() {
        val result = repository.getSettings()
        db.insert(result.toSettings())
    }

    override suspend fun createSettings(settings: Settings) {
        db.insert(settings)
        val createOrUpdateSettingsBody = CreateOrUpdateSettingsBody(
            key = settings.key.keyInServer,
            value = settings.value
        )
        repository.createOrUpdateSettings(createOrUpdateSettingsBody)
    }
}