package com.orelzman.mymessages.data.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.remote.dto.body.create.CreateOrUpdateSettingsBody
import com.orelzman.mymessages.data.remote.dto.response.toSettings
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orels.domain.model.entities.Settings
import com.orels.domain.model.entities.SettingsKey
import com.orelzman.mymessages.domain.model.entities.UploadState
import com.orelzman.mymessages.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : SettingsInteractor {
    val db = database.settingsDao

    override fun getSettings(key: SettingsKey): Settings {
        val settings = db.get(key)
        return settings ?: Settings(key, key.defaultValue)
    }

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
        val settings = ArrayList(result.toSettings())
        val settingsKeys = settings.map{it.key}
        SettingsKey.values().forEach {
            if(!settingsKeys.contains(it)) {
                settings.add(it.settings())
            }
        }
        db.insert(settings)
    }

    override suspend fun createOrUpdate(settings: List<Settings>) {
        settings.forEach { it.setUploadState(UploadState.BeingUploaded) }
        db.insert(settings)
        val createOrUpdateSettingsBody = settings.map {
            CreateOrUpdateSettingsBody(
                key = it.key.keyInServer,
                value = it.value
            )
        }
        repository.createOrUpdateSettings(createOrUpdateSettingsBody)
        settings.forEach { it.setUploadState(UploadState.Uploaded) }
        db.insert(settings)
    }

    override suspend fun createOrUpdate(settings: Settings) =
        createOrUpdate(listOf(settings))

}