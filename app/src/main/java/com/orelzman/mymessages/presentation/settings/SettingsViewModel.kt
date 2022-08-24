package com.orelzman.mymessages.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.model.entities.SettingsKey
import com.orelzman.mymessages.domain.model.entities.SettingsType
import com.orelzman.mymessages.domain.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsInteractor: SettingsInteractor
) :
    ViewModel() {
    var state by mutableStateOf(SettingsState())

    init {
        initData()
        observeSettings()
    }

    private fun initData() {
        val settingsList = settingsInteractor.getAll()
        setSettings(settingsList)

        val fetchSettingsJob = viewModelScope.async { settingsInteractor.init() }
        CoroutineScope(SupervisorJob()).launch {
            try {
                fetchSettingsJob.await()
            } catch (e: Exception) {
                e.log()
            }
        }

    }

    private fun observeSettings() {
        viewModelScope.launch(SupervisorJob()) {
            settingsInteractor.getAllSettingsFlow().collectLatest { settingsList ->
                setSettings(settingsList = settingsList)
                confirmAllSettingsAreInDB(settingsList = settingsList)
            }
        }
    }

    private fun setSettings(settingsList: List<Settings>) {
        val sortedSettingsList = settingsList
            .sortedWith(compareBy({ it.key }, { it.key.name }))
        state = state.copy(
            settingsList = sortedSettingsList,
        )
    }

    private suspend fun confirmAllSettingsAreInDB(settingsList: List<Settings>) {
        val settingsListKeys = settingsList.map { it.key }
        SettingsKey.values().forEach { settingsKey ->
            if (!settingsListKeys.contains(settingsKey)) {
                settingsInteractor.createOrUpdate(
                    Settings(key = settingsKey)
                )
            }
        }
    }

    fun saveSettings() {
        state = state.copy(isLoading = true, eventSettings = null)
        if (!state.isUpdated) {
            state = state.copy(isLoading = false, eventSettings = EventsSettings.Unchanged)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                settingsInteractor.getAll()
                    .filter { it.key.defaultEnabled } // TODO: Make a list request.
                    .forEach { settings ->
                        state = try {
                            settingsInteractor.createOrUpdate(
                                settings = settings
                            )
                            state.copy(
                                isLoading = false,
                                eventSettings = EventsSettings.Saved,
                                isUpdated = false
                            )
                        } catch (e: Exception) {
                            e.log()
                            state.copy(isLoading = false, eventSettings = EventsSettings.Error)
                        }
                    }
            }
        }
    }

    fun settingsChanged(settings: Settings) {
        state = state.copy(isUpdated = true)
        when (settings.key.type) {
            SettingsType.Toggle -> settingsChecked(settings)
            else -> {}
        }
    }

    private fun settingsChecked(settings: Settings) {
        val prevChecked: Boolean = settings.getRealValue() ?: true
        settingsInteractor.saveSettings(
            Settings(settings.key, (!prevChecked).toString())
        )
    }

}