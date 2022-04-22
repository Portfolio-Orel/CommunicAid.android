package com.orelzman.mymessages.domain.service.PhoneCall

import android.content.Context
import com.orelzman.mymessages.data.dto.PhoneCall
import kotlinx.coroutines.flow.MutableStateFlow

interface PhoneCallManager {

    val callOnTheLine: MutableStateFlow<PhoneCall?>
    val callsBacklog: MutableStateFlow<List<PhoneCall>>

    fun onIdleState()
    fun onRingingState(number: String, context: Context)
    fun onOffHookState(number: String, context: Context)

}

enum class CallState {
    INCOMING,
    OUTGOING,
    WAITING,
    IDLE
}