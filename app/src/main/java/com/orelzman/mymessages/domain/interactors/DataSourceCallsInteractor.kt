package com.orelzman.mymessages.domain.interactors

import android.content.SharedPreferences
import com.orelzman.mymessages.domain.managers.phonecall.CallState
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import kotlinx.coroutines.flow.Flow


interface DataSourceCallsInteractor {
    fun callsPreferencesFlow(): Flow<CallPreferences>
    fun callStateFlow(): Flow<CallState?>
    fun callsPrefrences(): SharedPreferences

    fun init()

    fun updateCallOnTheLine(callOnTheLine: PhoneCall?)
    fun updateCallInTheBackground(callInTheBackground: PhoneCall?)
    fun updateState(state: CallState?)

    fun getState(): CallState?
    fun getCallOnTheLine(): PhoneCall?
    fun getCallInTheBackground(): PhoneCall?
}

data class CallPreferences(
    val callOnTheLine: String?,
    val callInTheBackground: String?,
    val callState: String?
)
