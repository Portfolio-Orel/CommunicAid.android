package com.orelzman.mymessages.domain.service.phone_call

import com.orelzman.mymessages.domain.model.entities.PhoneCall
import kotlinx.coroutines.flow.StateFlow

/**
 * Implementation must not have any async tasks.
 */
interface PhoneCallManager {

    val callOnTheLine: StateFlow<PhoneCall?>
    val state: StateFlow<CallState>
    fun onStateChanged(state: String, number: String)
}

enum class CallState {
    INCOMING,
    OUTGOING,
    WAITING,
    IDLE
}