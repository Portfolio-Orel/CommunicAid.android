package com.orels.domain.managers.phonecall.interactor

import com.orels.domain.interactors.CallPreferences
import com.orels.domain.model.entities.PhoneCall
import kotlinx.coroutines.flow.Flow

interface PhoneCallManagerInteractor {
    val callsDataFlow: Flow<CallPreferences>
    val callsData: CallPreferences

    fun setCallAfterWaiting(call: PhoneCall)
    fun resetIfNoActiveCall()
    fun hangupCall()
}