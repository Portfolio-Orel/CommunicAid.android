package com.orelzman.mymessages.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.model.entities.SettingsType
import com.orelzman.mymessages.domain.util.extension.Logger
import com.orelzman.mymessages.domain.util.extension.launchCatching
import com.orelzman.mymessages.domain.util.extension.log
import com.orelzman.mymessages.domain.util.extension.notEqualsTo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsInteractor: SettingsInteractor,
    private val authInteractor: AuthInteractor,
) :
    ViewModel() {
    var state by mutableStateOf(SettingsState())

    init {
        initData()
        observeSettings()
    }

    fun settingsChanged(settings: Settings) {
        when (settings.key.type) {
            SettingsType.Toggle -> settingsChecked(settings)
            else -> {}
        }
        val updatedSettings = ArrayList(state.updatedSettings)
        updatedSettings.add(settings)
        state = state.copy(updatedSettings = updatedSettings)
        Logger.v("updated settings: $updatedSettings")
    }

    fun refreshSettings() {
        setSettings(emptyList())
        setSettings(settingsInteractor.getAll())
    }

    fun signOut() {
        viewModelScope.launchCatching(Dispatchers.Main) {
            authInteractor.signOut()
        }
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
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch(SupervisorJob()) {
            settingsInteractor.getAllSettingsFlow().collectLatest { settingsList ->
                if (state.settingsList.notEqualsTo(settingsList) && state.updatedSettings.isEmpty()) {
                    Logger.v("Edited settings")
                    setSettings(settingsList = settingsList)
                }
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

    fun saveSettings() {
        state = state.copy(isLoading = true, eventSettings = null)
        if (state.updatedSettings.isEmpty()) {
            state = state.copy(isLoading = false, eventSettings = EventsSettings.Unchanged)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                val settings = state.updatedSettings
                    .filter { it.isEnabled() }
                try {
                    settingsInteractor.createOrUpdate(settings = settings)
                } catch (e: Exception) {
                    e.log()
                    state = state.copy(isLoading = false, eventSettings = EventsSettings.Error)
                } finally {
                    state = state.copy(isLoading = false)
                }

                state = state.copy(
                    isLoading = false,
                    eventSettings = EventsSettings.Saved,
                    updatedSettings = emptyList()
                )
            }
        }
    }

    private fun settingsChecked(settings: Settings) {
        val prevChecked: Boolean = settings.getRealValue() ?: true
        settings.value = (!prevChecked).toString()
        settingsInteractor.saveSettings(settings)
    }

}