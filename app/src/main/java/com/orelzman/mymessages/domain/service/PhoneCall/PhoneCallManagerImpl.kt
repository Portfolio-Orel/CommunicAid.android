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
import com.orelzman.mymessages.domain.service.PhoneCall.CallState
import com.orelzman.mymessages.domain.service.PhoneCall.PhoneCallManager
import com.orelzman.mymessages.util.extension.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalPermissionsApi
class PhoneCallManagerImpl @Inject constructor(
    private val phoneCallStatisticsInteractor: PhoneCallStatisticsInteractor,
) : PhoneCallManager {
    override val callOnTheLine: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)
    private val callInTheBackground: MutableStateFlow<PhoneCall?> = MutableStateFlow(null)

    val state = MutableStateFlow(CallState.IDLE) // ToDo add _state to be viewd by other classes

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

    private fun onOffHookState(number: String) {
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
        state.value = CallState.IDLE
        callOnTheLine.value = null
        callInTheBackground.value = null
    }
}