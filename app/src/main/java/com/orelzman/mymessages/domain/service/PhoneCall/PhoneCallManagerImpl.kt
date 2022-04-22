package com.orelzman.mymessages.domain.service.PhoneCall

import android.content.Context
import android.provider.CallLog
import com.orelzman.mymessages.data.dto.PhoneCall
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*


class PhoneCallManagerImpl : PhoneCallManager {
    override val callOnTheLine: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)

    // These are calls that went to the background while answering other calls.
    override val callsBacklog: MutableStateFlow<List<PhoneCall>> = MutableStateFlow(emptyList())

    val state = MutableStateFlow(CallState.IDLE)
    private val backgroundCall: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)
    private var _callsBacklog: MutableStateFlow<List<PhoneCall>> = MutableStateFlow(ArrayList())

    override fun onIdleState(context: Context) {
        sendBacklog(context = context)
        resetStates()
    }

    override fun onRingingState(number: String, context: Context) {
        when (state.value) {
            CallState.IDLE -> {
                state.value = CallState.INCOMING
                setCallOnLine(PhoneCall.incoming(number = number))

            }
            CallState.INCOMING -> {
                state.value = CallState.WAITING
                setBackgroundCall(PhoneCall.waiting(number = number))
            }
            else -> throw Exception("Weird exception - onRingingState: $number ${state.value}")
        }
    }

    override fun onOffHookState(number: String, context: Context) {
        when (state.value) {
            CallState.WAITING -> {
                if (callOnTheLine.value?.number != number) { // Waiting answered.
                    waitingAnswered()
                } else {
                    state.value = CallState.INCOMING
                }
            }
            CallState.IDLE -> {
                outgoingCall(number = number)
            }
            else -> throw Exception("Weird exception - onOffHookState: $number ${state.value}")
        }
    }

    private fun setBackgroundCall(phoneCall: PhoneCall?) {
        backgroundCall.value = phoneCall
        addToBacklog(phoneCall = phoneCall)
    }

    private fun setCallOnLine(phoneCall: PhoneCall?) {
        callOnTheLine.value = phoneCall
        addToBacklog(phoneCall = phoneCall)
    }

    private fun outgoingCall(number: String) {
        state.value = CallState.OUTGOING
        setCallOnLine(PhoneCall.outgoing(number = number))
    }

    private fun waitingAnswered() {
        val backgroundCallHolder = backgroundCall.value
        setBackgroundCall(callOnTheLine.value)
        setCallOnLine(backgroundCallHolder)
    }

    private fun addToBacklog(phoneCall: PhoneCall?) {
        if (phoneCall == null) return
        if (_callsBacklog.value.any { it.startDate == phoneCall.startDate }) return // Contains the call

        val backlog = _callsBacklog.value as ArrayList
        backlog.add(phoneCall)
        _callsBacklog.value = backlog
    }

    private fun sendBacklog(context: Context) {
        // Start background and send it callsBacklog -> It will be started from the broadcast receiver
        callsBacklog.value.map { it.update(context) }
        callsBacklog.value = _callsBacklog.value
    }

    private fun resetStates() {
        state.value = CallState.IDLE
        callsBacklog.value = emptyList()
        callOnTheLine.value = null
        backgroundCall.value = null
        _callsBacklog.value = emptyList()
    }
}

/**
 * Updates values according to the call log
 * *** Test call in background, removed and called again to see if the backlog catches both from the calllog
 */
private fun PhoneCall.update(context: Context) {
    val details = arrayOf(
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DURATION,
        CallLog.Calls.CACHED_NAME,
        CallLog.Calls.DATE
    )
    context.contentResolver
        .query(
            CallLog.Calls.CONTENT_URI,
            details,
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )
        ?.use {
            while (it.moveToNext()) {
                if (number == it.getString(0)) {
                    val type = it.getString(1)
                    val duration = it.getString(2) as? Long ?: 0
                    name = it.getString(3) ?: ""
                    startDate = it.getString(4) as? Date ?: Date()
                    endDate = Date(startDate.time + duration)
                    when (type.toInt()) {
                        CallLog.Calls.MISSED_TYPE -> missed()
                        CallLog.Calls.REJECTED_TYPE -> rejected()
                    }
                }
            }
        }
}