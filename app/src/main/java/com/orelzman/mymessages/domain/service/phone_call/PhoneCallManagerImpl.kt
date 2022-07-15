package com.orelzman.mymessages.domain.service.phone_call

import android.content.Context
import android.telephony.TelephonyManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.mymessages.domain.interactors.AnalyticsInteractor
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.model.entities.CallLogEntity
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.domain.service.phone_call.exceptions.WaitingThenRingingException
import com.orelzman.mymessages.domain.common.DataSourceCalls
import com.orelzman.mymessages.util.common.CallUtils
import com.orelzman.mymessages.util.common.Constants.TIME_TO_ADD_CALL_TO_CALL_LOG
import com.orelzman.mymessages.util.extension.Log
import com.orelzman.mymessages.util.extension.inSeconds
import com.orelzman.mymessages.util.extension.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@Suppress("MoveVariableDeclarationIntoWhen")
@ExperimentalPermissionsApi
class PhoneCallManagerImpl @Inject constructor(
    private val phoneCallInteractor: PhoneCallsInteractor,
    private val analyticsInteractor: AnalyticsInteractor?,
    private val dataSource: DataSourceCalls
) : PhoneCallManager {

    private val _callInTheBackground: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)
    private val _callOnTheLine: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)
    private val _state: MutableStateFlow<CallState> = MutableStateFlow(CallState.Idle)

    override val callOnTheLine = _callOnTheLine.asStateFlow()
    override val state = _state.asStateFlow()
    override val callInBackground = _callInTheBackground.asStateFlow()

    var context: Context? = null

    override fun onStateChanged(state: String, number: String, context: Context?) {
        Log.vCustom("state: $state \n number: $number")
        this.context = context
        analyticsInteractor?.track("Call Status", mapOf("status" to state))
        when (state) {
            TelephonyManager.EXTRA_STATE_IDLE -> onIdleState()
            TelephonyManager.EXTRA_STATE_RINGING -> onRingingState(number)
            TelephonyManager.EXTRA_STATE_OFFHOOK -> onOffHookState(number)
        }
    }

    private fun onIdleState() {
        reset()
    }

    private fun onRingingState(number: String) {
        val previousState = state.value
        when (previousState) {
            CallState.Idle -> {
                incomingCall(number = number)
            }
            CallState.OnCall -> {
                waitingCall(number = number)
            }
            else -> WaitingThenRingingException.log()
        }
    }

    private fun onOffHookState(number: String) {
        val previousState = state.value
        when (previousState) {
            CallState.Waiting -> {
                checkWaitingCallState(context)
            }
            CallState.Idle -> {
                outgoingCall(number = number)
            }
            CallState.Ringing -> {
                incomingAnswered()
            }
            else -> throw Exception("Weird exception - onOffHookState: $number ${state.value}")
        }
    }

    private fun setBackgroundCall(phoneCall: PhoneCall?) {
        CoroutineScope(Dispatchers.Main).launch {
            dataSource.updateCallInTheBackground(phoneCall?.number)
        }
    }

    private fun setCallOnLine(phoneCall: PhoneCall?) {
        _callOnTheLine.value = phoneCall
        CoroutineScope(Dispatchers.Main).launch {
            dataSource.updateCallOnTheLine(phoneCall?.number)
        }
    }

    private fun setStateValue(callState: CallState) {
        _state.value = callState
        CoroutineScope(Dispatchers.Main).launch {
            dataSource.updateState(callState.value)
        }
    }

    private fun outgoingCall(number: String) {
        setStateValue(CallState.OnCall)
        val phoneCall = PhoneCall.outgoing(number = number)
        setCallOnLine(phoneCall)
        addToBacklog(phoneCall = phoneCall)
    }

    private fun incomingCall(number: String) {
        setStateValue(CallState.Ringing)
        val phoneCall = PhoneCall.incoming(number = number)
        setCallOnLine(phoneCall)
        addToBacklog(phoneCall = phoneCall)
    }

    private fun incomingAnswered() {
        setStateValue(CallState.OnCall)
    }

    private fun waitingCall(number: String) {
        setStateValue(CallState.Waiting)
        val phoneCall = PhoneCall.waiting(number = number)
        setBackgroundCall(PhoneCall.waiting(number = number))
        addToBacklog(phoneCall = phoneCall)
    }

    private fun checkWaitingCallState(context: Context?) {
        setStateValue(CallState.OnCall)
        CoroutineScope(Dispatchers.Main).launch {
            val callLog = CallUtils.getLastCallLog(context, withDelay = TIME_TO_ADD_CALL_TO_CALL_LOG)
            if(_callInTheBackground.value != null && _callInTheBackground.value!!.isEqualsToCallLog(callLog)) {
                waitingCallNotAnswered()
            } else {
                waitingCallAnswered()
            }
        }
    }

    private fun waitingCallAnswered() {
        val backgroundCallHolder = _callInTheBackground.value
        setBackgroundCall(callOnTheLine.value)
        setCallOnLine(backgroundCallHolder)
    }

    private fun waitingCallNotAnswered() {
        _callInTheBackground.value = null
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

    private fun reset() {
        setStateValue(CallState.Idle)
        setCallOnLine(null)
        setBackgroundCall(null)
    }
}

fun PhoneCall.isEqualsToCallLog(callLog: CallLogEntity?): Boolean =
    callLog != null &&
            callLog.number == number
            && (
            callLog.time.inSeconds < startDate.time.inSeconds + 6 && callLog.time.inSeconds > startDate.time.inSeconds - 6
            )
