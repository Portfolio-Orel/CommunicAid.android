package com.orelzman.mymessages.domain.service.PhoneCall

import android.content.Context
import com.orelzman.mymessages.data.dto.PhoneCall
import kotlinx.coroutines.flow.MutableStateFlow


class PhoneCallManagerImpl : PhoneCallManager {
    override val callOnTheLine: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)

    // These are calls that went to the background while answering other calls.
    override val callsBacklog: MutableStateFlow<List<PhoneCall>> = MutableStateFlow(emptyList())

    val state = MutableStateFlow(CallState.IDLE)
    private val backgroundCall: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)
    private var _callsBacklog: MutableStateFlow<List<PhoneCall>> = MutableStateFlow(ArrayList())

    override fun onIdleState() {
        sendBacklog()
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

    private fun sendBacklog() {
        // Start background and send it callsBacklog -> It will be started from the broadcast receiver
        // callsBacklog.value = _callsBacklog.value
    }

    private fun resetStates() {
        state.value = CallState.IDLE
        callsBacklog.value = emptyList()
        callOnTheLine.value = null
        backgroundCall.value = null
        _callsBacklog.value = emptyList()
    }
}