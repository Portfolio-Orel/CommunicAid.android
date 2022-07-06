package com.orelzman.mymessages.domain.service.phone_call

import android.content.Context
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import kotlinx.coroutines.flow.StateFlow

/**
 * Implementation must not have any async tasks.
 */
interface PhoneCallManager {

    val callInBackground: StateFlow<PhoneCall?>
    val callOnTheLine: StateFlow<PhoneCall?>
    val state: StateFlow<CallState>

    fun onStateChanged(state: String, number: String, context: Context? = null)
}

enum class CallState {
    INCOMING,
    OUTGOING,
    WAITING,
    IDLE
}