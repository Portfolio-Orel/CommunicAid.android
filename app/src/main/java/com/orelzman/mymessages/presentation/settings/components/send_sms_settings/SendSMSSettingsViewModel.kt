package com.orelzman.mymessages.presentation.settings.components.send_sms_settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.model.entities.SettingsKey
import com.orelzman.mymessages.domain.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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

    init {
        val smsText: String =
            settingsInteractor.getSettings(SettingsKey.SMSToSendToBackgroundCall).getRealValue()
                ?: ""
        state = state.copy(smsText = smsText)
    }

    fun onSMSTextChange(value: String) {
        state = state.copy(smsText = value)
    }

    fun saveSMSText(onSaveDone: () -> Unit = {}) {
        val saveJob = viewModelScope.async {
            settingsInteractor.createOrUpdate(
                settings = SettingsKey.SMSToSendToBackgroundCall.settings(value = state.smsText),
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                saveJob.await()
            } catch (e: Exception) {
                e.log()
            } finally {
                onSaveDone()
            }
        }
    }
}