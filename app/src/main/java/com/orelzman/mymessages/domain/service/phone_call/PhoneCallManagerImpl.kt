package com.orelzman.mymessages.domain.service.phone_call

import android.telephony.TelephonyManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.mymessages.domain.interactors.AnalyticsInteractor
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.domain.service.phone_call.exceptions.WaitingThenRingingException
import com.orelzman.mymessages.util.extension.Log
import com.orelzman.mymessages.util.extension.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject


@Suppress("MoveVariableDeclarationIntoWhen")
@ExperimentalPermissionsApi
class PhoneCallManagerImpl @Inject constructor(
    private val phoneCallInteractor: PhoneCallsInteractor,
    private val analyticsInteractor: AnalyticsInteractor?
) : PhoneCallManager {

    private val _callInTheBackground: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)
    private val _callOnTheLine: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)
    private val _state: MutableStateFlow<CallState> = MutableStateFlow(CallState.IDLE)

    override val callOnTheLine = _callOnTheLine.asStateFlow()
    override val state = _state.asStateFlow()
    override val callInBackground = _callInTheBackground.asStateFlow()

    override fun onStateChanged(state: String, number: String) {
        Log.vCustom("state: $state \n number: $number")
        analyticsInteractor?.track("Call Status", mapOf("status" to state))
        when (state) {
            TelephonyManager.EXTRA_STATE_IDLE -> onIdleState()
            TelephonyManager.EXTRA_STATE_RINGING -> onRingingState(number)
            TelephonyManager.EXTRA_STATE_OFFHOOK -> onOffHookState(number)
        }
    }

    private fun onIdleState() {
        resetStates()
    }

    private fun onRingingState(number: String) {
        val previousState = state.value
        when (previousState) {
            CallState.IDLE -> {
                incomingCall(number = number)
            }
            CallState.INCOMING, CallState.OUTGOING -> {
                waitingCall(number = number)
            }
            else -> WaitingThenRingingException.log()
        }
    }

    private fun onOffHookState(number: String) {
        val previousState = state.value
        when (previousState) {
            CallState.WAITING -> {
                if (callOnTheLine.value?.number != number) { // Waiting answered.
                    waitingCallAnswered()
                } else {
                    waitingCallRejected()
                }
            }
            CallState.IDLE -> {
                outgoingCall(number = number)
            }
            CallState.INCOMING -> {
                incomingAnswered()
            }
            else -> throw Exception("Weird exception - onOffHookState: $number ${state.value}")
        }
    }

    private fun setBackgroundCall(phoneCall: PhoneCall?) {
        _callInTheBackground.value = phoneCall
    }

    private fun setCallOnLine(phoneCall: PhoneCall?) {
        _callOnTheLine.value = phoneCall
    }

    private fun setStateValue(callState: CallState) {
        _state.value = callState
    }

    private fun outgoingCall(number: String) {
        setStateValue(CallState.OUTGOING)
        val phoneCall = PhoneCall.outgoing(number = number)
        setCallOnLine(phoneCall)
        addToBacklog(phoneCall = phoneCall)
    }

    private fun incomingCall(number: String) {
        setStateValue(CallState.INCOMING)
        val phoneCall = PhoneCall.incoming(number = number)
        setCallOnLine(phoneCall)
        addToBacklog(phoneCall = phoneCall)
    }

    private fun incomingAnswered() {
        // ToDo if needed
    }

    private fun waitingCall(number: String) {
        setStateValue(CallState.WAITING)
        val phoneCall = PhoneCall.waiting(number = number)
        setBackgroundCall(PhoneCall.waiting(number = number))
        addToBacklog(phoneCall = phoneCall)
    }


    private fun waitingCallAnswered() {
        val backgroundCallHolder = _callInTheBackground.value
        setStateValue(CallState.INCOMING)
        setBackgroundCall(callOnTheLine.value)
        setCallOnLine(backgroundCallHolder)
    }

    private fun waitingCallRejected() {
        _callInTheBackground.value = null
        setStateValue(CallState.INCOMING)
    }

    private fun addToBacklog(phoneCall: PhoneCall?) {
        if (phoneCall == null) return
        val id = UUID.randomUUID()
        phoneCall.id = id.toString()
        if (phoneCallInteractor.getAll().any { it.startDate == phoneCall.startDate }) {
            analyticsInteractor?.track("Call Cached", "value" to "Double add attempt")
        }
        phoneCallInteractor.cachePhoneCall(phoneCall = phoneCall)
        analyticsInteractor?.track("Call Cached", mapOf("call" to phoneCall.number))
    }

    private fun resetStates() {
        setStateValue(CallState.IDLE)
        _callOnTheLine.value = null
        _callInTheBackground.value = null
    }
}