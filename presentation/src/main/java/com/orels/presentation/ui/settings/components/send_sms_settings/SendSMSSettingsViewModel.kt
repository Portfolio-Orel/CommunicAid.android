package com.orels.presentation.ui.settings.components.send_sms_settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.domain.interactors.SettingsInteractor
import com.orels.domain.model.entities.SettingsKey
import com.orels.domain.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * @author Orel Zilberman
 * 28/08/2022
 */

@HiltViewModel
class SendSMSSettingsViewModel
@Inject constructor(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    var state by mutableStateOf(SendSMSSettingsState())
    private var save: Job? = null

    init {
        val smsText: String =
            settingsInteractor.getSettings(SettingsKey.SMSToSendToBackgroundCall).getRealValue()
                ?: ""
        state = state.copy(smsText = smsText)
    }

    fun onSMSTextChange(value: String) {
        state = state.copy(smsText = value, isLoading = true)
        save?.cancel()
        save = viewModelScope.launch(SupervisorJob()) {
            try {
                delay(timeMillis = TIME_TO_SAVE_SMS_MILLIS)
                settingsInteractor.createOrUpdate(SettingsKey.SMSToSendToBackgroundCall.settings(state.smsText))
                state = state.copy(isLoading = false)
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    e.log()
                }
            }
        }
    }

    companion object {
        private const val TIME_TO_SAVE_SMS_MILLIS = 2000L
    }
}