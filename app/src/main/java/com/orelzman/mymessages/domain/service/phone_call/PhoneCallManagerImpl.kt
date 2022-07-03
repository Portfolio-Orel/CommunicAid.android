package com.orelzman.mymessages.domain.service.phone_call

import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.mymessages.data.dto.PhoneCall
import com.orelzman.mymessages.data.local.interactors.analytics.AnalyticsInteractor
import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallsInteractor
import com.orelzman.mymessages.domain.service.CallsService
import com.orelzman.mymessages.domain.service.CallsService.Companion.INTENT_STATE_VALUE
import com.orelzman.mymessages.domain.service.ServiceState
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

    private val callInTheBackground: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)
    private val _callOnTheLine: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)
    private val _state: MutableStateFlow<CallState> = MutableStateFlow(CallState.IDLE)

    override val callOnTheLine = _callOnTheLine.asStateFlow()
    val state = _state.asStateFlow()

    override fun onStateChanged(state: String, number: String, context: Context) {
        Log.vCustom("state: $state \n number: $number")
        analyticsInteractor?.track("Call Status", mapOf("status" to state))
        when (state) {
            TelephonyManager.EXTRA_STATE_IDLE -> onIdleState(context)
            TelephonyManager.EXTRA_STATE_RINGING -> onRingingState(number)
            TelephonyManager.EXTRA_STATE_OFFHOOK -> onOffHookState(number)
        }
        startBackgroundService(context)
    }

    private fun onIdleState(context: Context) {
        startBackgroundService(context, ServiceState.UPLOAD_LOGS)
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
        callInTheBackground.value = phoneCall
        addToBacklog(phoneCall = phoneCall)
    }

    private fun setCallOnLine(phoneCall: PhoneCall?) {
        _callOnTheLine.value = phoneCall
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

    private fun incomingAnswered() {
        // ToDo if needed
    }

    private fun waitingCall(number: String) {
        setStateValue(CallState.WAITING)
        setBackgroundCall(PhoneCall.waiting(number = number))
    }


    private fun waitingCallAnswered() {
        val backgroundCallHolder = callInTheBackground.value
        setBackgroundCall(callOnTheLine.value)
        setCallOnLine(backgroundCallHolder)
    }

    private fun waitingCallRejected() {
        callInTheBackground.value = null
        setStateValue(CallState.INCOMING)
    }

    private fun addToBacklog(phoneCall: PhoneCall?) {
        if (phoneCall == null) return
        val id = UUID.randomUUID()
        phoneCall.id = id.toString()
        phoneCallInteractor.cachePhoneCall(phoneCall = phoneCall)
        analyticsInteractor?.track("Call Cached", mapOf("call" to phoneCall.number))
    }

    private fun startBackgroundService(
        context: Context,
        state: ServiceState = ServiceState.ALIVE
    ) {
        // Avoid starting the service twice for the same purpose
        if (state == CallsService.currentState) {
            return
        }
        val intent = Intent(context, CallsService::class.java)
        intent.putExtra(INTENT_STATE_VALUE, state)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.vCustom("Starting the service! context: $context")
                context.startForegroundService(intent)
            } else {
                Log.vCustom("Starting the service pre O! context: $context")
                context.startService(intent)
            }
        } catch (exception: Exception) { // ForegroundServiceStartNotAllowedException
            Log.vCustom("Error... ${exception.message}")
            exception.log()
        }
    }

    private fun resetStates() {
        setStateValue(CallState.IDLE)
        _callOnTheLine.value = null
        callInTheBackground.value = null
    }
}