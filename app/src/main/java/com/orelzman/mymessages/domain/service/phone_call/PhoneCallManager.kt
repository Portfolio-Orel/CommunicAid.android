package com.orelzman.mymessages.domain.service.phone_call

import android.content.Context
import com.orelzman.mymessages.data.dto.PhoneCall
import kotlinx.coroutines.flow.StateFlow

interface PhoneCallManager {

    val callOnTheLine: StateFlow<PhoneCall?>

    fun onStateChanged(state: String, number: String, context: Context)
}

enum class CallState {
    INCOMING,
    OUTGOING,
    WAITING,
    IDLE
}