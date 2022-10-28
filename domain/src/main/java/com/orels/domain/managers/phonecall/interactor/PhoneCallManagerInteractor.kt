package com.orels.domain.managers.phonecall.interactor

import com.orels.domain.interactors.CallPreferences
import kotlinx.coroutines.flow.Flow

interface PhoneCallManagerInteractor {
    val callsDataFlow: Flow<CallPreferences>
    val callsData: CallPreferences

    fun resetIfNoActiveCall()
    fun hangupCall()
}