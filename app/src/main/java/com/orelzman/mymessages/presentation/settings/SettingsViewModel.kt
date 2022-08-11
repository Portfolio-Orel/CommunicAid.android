package com.orelzman.mymessages.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.model.entities.SettingsKey
import com.orelzman.mymessages.domain.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
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

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                settingsInteractor.getAllSettings().forEach { settings ->
                    try {
                        settingsInteractor.createOrUpdate(
                            settings = settings
                        )
                    } catch (e: Exception) {
                        e.log()
                    }
                }
            }
        }
    }

    private fun initData() {
        val settingsList = settingsInteractor.getAllSettings()
        setSettings(settingsList)
    }

    private fun observeSettings() {
        viewModelScope.launch(SupervisorJob()) {
            settingsInteractor.getAllSettingsFlow().collectLatest { settingsList ->
                setSettings(settingsList)
                confirmAllSettingsAreInDB(settingsList = settingsList)
            }
        }
    }

    private fun setSettings(settingsList: List<Settings>) {
        state = state.copy(
            settingsList = settingsList
                .sortedWith(compareBy({ it.key }, { it.key.name }))
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

    fun settingsChecked(settings: Settings) {
        val prevChecked: Boolean = settings.getRealValue() ?: true
        settingsInteractor.saveSettings(
            Settings(settings.key, (!prevChecked).toString())
        )
    }

}