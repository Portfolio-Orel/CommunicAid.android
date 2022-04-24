package com.orelzman.mymessages.domain.service.PhoneCall

import android.content.Context
import com.orelzman.mymessages.data.dto.PhoneCall
import kotlinx.coroutines.flow.MutableStateFlow

interface PhoneCallManager {

    val callOnTheLine: MutableStateFlow<PhoneCall?>

    fun onStateChanged(state: String, number: String, context: Context)
}

enum class CallState {
    INCOMING,
    OUTGOING,
    WAITING,
    IDLE
}