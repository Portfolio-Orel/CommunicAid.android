package com.orelzman.mymessages.domain.interactors

import android.content.SharedPreferences
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.domain.managers.phonecall.CallState
import kotlinx.coroutines.flow.Flow


interface DataSourceCallsInteractor {
    fun callsPreferencesFlow(): Flow<CallPreferences>
    fun callsPrefrences(): SharedPreferences

    suspend fun init()

    suspend fun updateCallOnTheLine(callOnTheLine: PhoneCall?)
    suspend fun updateCallInTheBackground(callInTheBackground: PhoneCall?)
    suspend fun updateState(state: CallState?)

    fun getState(): CallState?
    fun getCallOnTheLine(): PhoneCall?
    fun getCallInTheBackground(): PhoneCall?
}

data class CallPreferences(
    val callOnTheLine: String?,
    val callInTheBackground: String?,
    val callState: String?
)
