package com.orelzman.mymessages.domain.manager.PhoneCall

import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.mymessages.data.dto.PhoneCall
import com.orelzman.mymessages.data.dto.PhoneCallStatistics
import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallStatisticsInteractor
import com.orelzman.mymessages.domain.service.CallsService
import com.orelzman.mymessages.domain.service.phone_call.CallState
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManager
import com.orelzman.mymessages.util.extension.log
import com.orelzman.mymessages.util.extension.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@Suppress("MoveVariableDeclarationIntoWhen")
@ExperimentalPermissionsApi
class PhoneCallManagerImpl @Inject constructor(
    private val phoneCallStatisticsInteractor: PhoneCallStatisticsInteractor,
) : PhoneCallManager {

    private val callInTheBackground: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)
    private val _callOnTheLine: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)
    private val _state: MutableStateFlow<CallState> = MutableStateFlow(CallState.IDLE)

    override val callOnTheLine = _callOnTheLine.asStateFlow()
    val state = _state.asStateFlow()

    override fun onStateChanged(state: String, number: String, context: Context) {
        when (state) {
            TelephonyManager.EXTRA_STATE_IDLE -> onIdleState(context)
            TelephonyManager.EXTRA_STATE_RINGING -> onRingingState(number)
            TelephonyManager.EXTRA_STATE_OFFHOOK -> onOffHookState(number)
        }
        startBackgroundService(context)
    }

    private fun onIdleState(context: Context) {
        startBackgroundService(context)
        resetStates()
    }

    private fun onRingingState(number: String) {
        val previousState = state.value
        when (previousState) {
            CallState.IDLE -> {
                incomingCall(number = number)
            }
            CallState.INCOMING -> {
                waiting(number = number)
            }
            else -> throw Exception("Weird exception - onRingingState: $number ${state.value}")
        }
    }

    private fun onOffHookState(number: String) {
        val previousState = state.value
        when (previousState) {
            CallState.WAITING -> {
                if (callOnTheLine.value?.number != number) { // Waiting answered.
                    waitingAnswered()
                } else {
                    waitingDenied()
                }
            }
            CallState.IDLE -> {
                outgoingCall(number = number)
            }
            CallState.INCOMING -> { // incoming answered

            }
            else -> throw Exception("Weird exception - onOffHookState: $number ${state.value}")
        }
    }

    private fun setBackgroundCall(phoneCall: PhoneCall?) {
        callInTheBackground.value = phoneCall
        addToBacklog(phoneCall = phoneCall)
    }

    private fun setCallOnLine(phoneCall: PhoneCall?) {
        _callOnTheLine.update { it?.copy(phoneCall = phoneCall) }
        addToBacklog(phoneCall = phoneCall)
    }

    private fun setStateValue(callState: CallState) {
        _state.value = callState
    }

    private fun outgoingCall(number: String) {
        setStateValue(CallState.OUTGOING)
        setCallOnLine(PhoneCall.outgoing(number = number))
    }

    private fun incomingCall(number: String) {
        setStateValue(CallState.INCOMING)
        setCallOnLine(PhoneCall.incoming(number = number))
    }

    private fun waitingAnswered() {
        val backgroundCallHolder = callInTheBackground.value
        setBackgroundCall(callOnTheLine.value)
        setCallOnLine(backgroundCallHolder)
    }

    private fun waitingDenied() {
        setStateValue(CallState.INCOMING)
    }

    private fun waiting(number: String) {
        setStateValue(CallState.WAITING)
        setBackgroundCall(PhoneCall.waiting(number = number))
    }

    private fun addToBacklog(phoneCall: PhoneCall?) {
        if (phoneCall == null) return
        CoroutineScope(Dispatchers.IO).launch {
            phoneCallStatisticsInteractor.cachePhoneCall(PhoneCallStatistics(phoneCall = phoneCall))
        }
    }

    private fun startBackgroundService(context: Context) {
        val intent = Intent(context, CallsService::class.java)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } catch (exception: Exception) { // ForegroundServiceStartNotAllowedException
            exception.log()
        }
    }

    private fun resetStates() {
        setStateValue(CallState.IDLE)
        _callOnTheLine.update { it?.copy(phoneCall = null) }
        callInTheBackground.value = null
    }
}