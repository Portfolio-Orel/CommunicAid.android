package com.orelzman.mymessages.domain.service

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface PhoneCallManager {

    val state: Flow<CallState>

    fun onIdleState(number: String, context: Context)
    fun onRingingState(number: String, context: Context)
    fun onOffHookState(number: String, context: Context)

}

enum class CallState {
    INCOMING,
    INCOMING_CONNECTED,
    INCOMING_DISCONNECTED,
    OUTGOING,
    OUTGOING_CONNECTED,
    OUTGOING_DISCONNECTED,
    WAITING,
    WAITING_CONNECTED,
    WAITING_DISCONNECTED,
    IDLE
}