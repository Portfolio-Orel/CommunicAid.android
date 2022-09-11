package com.orelzman.mymessages.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.domain.interactors.AnalyticsIdentifiers
import com.orelzman.mymessages.domain.interactors.AnalyticsInteractor
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.model.entities.SettingsType
import com.orelzman.mymessages.domain.util.extension.Logger
import com.orelzman.mymessages.domain.util.extension.launchCatching
import com.orelzman.mymessages.domain.util.extension.log
import com.orelzman.mymessages.domain.util.extension.notEqualsTo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsInteractor: SettingsInteractor,
    private val authInteractor: AuthInteractor,
    private val analyticsInteractor: AnalyticsInteractor
) :
    ViewModel() {
    var state by mutableStateOf(SettingsState())

    init {
        analyticsInteractor.track(AnalyticsIdentifiers.SettingsScreenShow)
        initData()
        observeSettings()
    }

    fun onDestroy() {
        if(isSettingsChanges()) {
            analyticsInteractor.track(AnalyticsIdentifiers.SettingsScreenLeftWithoutSave)
        }
    }

    fun signOut() {
        viewModelScope.launchCatching(Dispatchers.Main) {
            authInteractor.signOut()
        }
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

    private fun initData() {
        val settingsList = settingsInteractor.getAll()
        setSettings(settingsList)

        val fetchSettingsJob = viewModelScope.async { settingsInteractor.init() }
        viewModelScope.launch(SupervisorJob()) {
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

    private fun isSettingsChanges(): Boolean = state.updatedSettings.isNotEmpty()

    private fun settingsChecked(settings: Settings) {
        val prevChecked: Boolean = settings.getRealValue() ?: true
        settings.value = (!prevChecked).toString()
        var loadingSettings = state.loadingSettings + settings.key
        state = state.copy(loadingSettings = loadingSettings)
        viewModelScope.launch(SupervisorJob()) {
            settingsInteractor.createOrUpdate(settings)
            loadingSettings = loadingSettings - settings.key
            state = state.copy(loadingSettings = loadingSettings)
        }
    }

}