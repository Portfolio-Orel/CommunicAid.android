package com.orelzman.mymessages.data.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.model.dto.body.create.CreateOrUpdateSettingsBody
import com.orelzman.mymessages.domain.model.dto.response.toSettings
import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.model.entities.SettingsKey
import com.orelzman.mymessages.domain.model.entities.UploadState
import com.orelzman.mymessages.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : SettingsInteractor {
    val db = database.settingsDao

    override fun getSettings(key: SettingsKey): Settings? =
        db.get(key)

    override suspend fun getAllSettingsFlow(): Flow<List<Settings>> {
        val settings = repository.getAllSettings()
        db.insert(settings.toSettings())
        return db.getAllFlow()
    }

    override fun getAll(): List<Settings> = db.getAll()

    override fun saveSettings(settings: Settings) =
        db.insert(settings)


    override suspend fun init() {
        val result = repository.getSettings()
        db.insert(result.toSettings())
    }

    override suspend fun createOrUpdate(settings: Settings) {
        settings.setUploadState(UploadState.BeingUploaded)
        db.insert(settings)
        val createOrUpdateSettingsBody = CreateOrUpdateSettingsBody(
            key = settings.key.keyInServer,
            value = settings.value
        )
        repository.createOrUpdateSettings(createOrUpdateSettingsBody)
        settings.setUploadState(UploadState.Uploaded)
    }
}