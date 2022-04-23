package com.orelzman.mymessages.domain.service.PhoneCall

import android.content.Context
import android.provider.CallLog
import com.orelzman.mymessages.data.dto.PhoneCall
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue


class PhoneCallManagerImpl @Inject constructor() : PhoneCallManager {
    override val callOnTheLine: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)

    // These are calls that went to the background while answering other calls.
    override val callsBacklog: MutableStateFlow<List<PhoneCall>> = MutableStateFlow(emptyList())

    val state = MutableStateFlow(CallState.IDLE)
    val callInTheBackground: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)
    private var _callsBacklog: MutableStateFlow<List<PhoneCall>> = MutableStateFlow(ArrayList())

    override fun onIdleState(context: Context) {
        sendBacklog(context = context)
        resetStates()
    }

    override fun onRingingState(number: String, context: Context) {
        when (state.value) {
            CallState.IDLE -> {
                incomingCall(number = number)
            }
            CallState.INCOMING -> {
                waiting(number = number)
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
            CallState.INCOMING -> { // incoming answered
//                incomingCall(number = number)
            }
            else -> throw Exception("Weird exception - onOffHookState: $number ${state.value}")
        }
    }

    private fun setBackgroundCall(phoneCall: PhoneCall?) {
        callInTheBackground.value = phoneCall
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

    private fun incomingCall(number: String) {
        state.value = CallState.INCOMING
        setCallOnLine(PhoneCall.incoming(number = number))
    }

    private fun waitingAnswered() {
        val backgroundCallHolder = callInTheBackground.value
        setBackgroundCall(callOnTheLine.value)
        setCallOnLine(backgroundCallHolder)
    }

    private fun waiting(number: String) {
        state.value = CallState.WAITING
        setBackgroundCall(PhoneCall.waiting(number = number))
    }

    private fun addToBacklog(phoneCall: PhoneCall?) {
        if (phoneCall == null) return
        if (_callsBacklog.value.any { it.startDate == phoneCall.startDate }) return // Contains the call

        val backlog = ArrayList(_callsBacklog.value)
        backlog.add(phoneCall)
        _callsBacklog.value = ArrayList(backlog)
    }

    private fun sendBacklog(context: Context) {
        // Start background and send it callsBacklog -> It will be started from the broadcast receiver
        callsBacklog.value = _callsBacklog.value
            .sortedByDescending { it.startDate }
            .map { it.update(context) }
    }

    private fun resetStates() {
        state.value = CallState.IDLE
        // Testing: to test the backlog comment this line
//        callsBacklog.value = emptyList()
        callOnTheLine.value = null
        callInTheBackground.value = null
//        _callsBacklog.value = emptyList()
    }
}

/**
 * Updates values according to the call log
 * *** Test call in background, removed and called again to see if the backlog catches both from the calllog
 * This has to go to the service because the log is added async.
 */
private fun PhoneCall.update(context: Context): PhoneCall {
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
                val logStartDate = Date(it.getString(4).toLong())
                if (
                    number != it.getString(0)
                    || logStartDate.notEquals(startDate)
                ) continue
                val type = it.getString(1)
                val duration = it.getString(2).toLong()
                name = it.getString(3) ?: ""
                endDate = Date(startDate.time.inSeconds + duration)
                when (type.toInt()) {
                    CallLog.Calls.MISSED_TYPE -> missed()
                    CallLog.Calls.REJECTED_TYPE -> rejected()
                }
                return@use
            }
        }
    return this
}

val Long.inSeconds: Long
    get() =
        if ("$this".length > 10) {
            this / 1000
        } else {
            this
        }

fun Date.notEquals(date: Date, maxDifferenceInSeconds: Long = 5): Boolean =
    (time.inSeconds - date.time.inSeconds).absoluteValue >= maxDifferenceInSeconds
