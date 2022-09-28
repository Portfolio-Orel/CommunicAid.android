package com.orelzman.mymessages.presentation.settings.components.send_sms_settings

/**
 * @author Orel Zilberman
 * 28/08/2022
 */
data class SendSMSSettingsState(
    val isLoading: Boolean = false,
    val smsText: String = "",
)