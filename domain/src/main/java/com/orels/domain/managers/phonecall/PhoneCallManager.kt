package com.orelzman.mymessages.domain.managers.phonecall

import android.content.Context
import com.orelzman.mymessages.domain.interactors.CallPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Implementation must not have any async tasks.
 */
interface PhoneCallManager {
    val callsDataFlow: Flow<CallPreferences>
    val callsData: CallPreferences

    fun onStateChanged(state: String, number: String, context: Context? = null)
    fun hangupCall(context: Context)
    fun resetIfNoActiveCall()
}

enum class CallState(val value: String) {
    OnCall("OnCall"),
    Waiting("Waiting"),
    Ringing("Ringing"),
    Idle("Idle");

    companion object {
        fun fromString(value: String?): CallState? {
            values().forEach {
                if (it.value == value) return it
            }
            return null
        }
    }
}

fun String?.isCallStateIdle(): Boolean =
    CallState.fromString(this) == CallState.Idle

fun String?.isCallStateWaiting(): Boolean =
    CallState.fromString(this) == CallState.Waiting

fun String.toState(): CallState? = CallState.fromString(this)