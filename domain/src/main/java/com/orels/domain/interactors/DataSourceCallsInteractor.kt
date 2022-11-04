package com.orels.domain.interactors

import android.content.SharedPreferences
import com.orels.domain.managers.phonecall.CallState
import com.orels.domain.model.entities.PhoneCall
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
