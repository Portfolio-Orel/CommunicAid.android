package com.orels.data.interactor

import com.orels.data.local.LocalDatabase
import com.orels.domain.interactors.SettingsInteractor
import com.orels.domain.model.dto.body.create.CreateOrUpdateSettingsBody
import com.orels.domain.model.dto.response.toSettings
import com.orels.domain.model.entities.Settings
import com.orels.domain.model.entities.SettingsKey
import com.orels.domain.model.entities.UploadState
import com.orels.domain.repository.Repository
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
        try {
            repository.createOrUpdateSettings(createOrUpdateSettingsBody)
        } catch (e: Exception) {
        }
        settings.forEach { it.setUploadState(UploadState.Uploaded) }
        db.insert(settings)
    }

    override suspend fun createOrUpdate(settings: Settings) =
        createOrUpdate(listOf(settings))

}