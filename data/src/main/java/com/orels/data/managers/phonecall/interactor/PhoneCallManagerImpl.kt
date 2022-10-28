package com.orels.data.managers.phonecall.interactor

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.*
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.orels.domain.interactors.CallLogInteractor
import com.orels.domain.interactors.CallPreferences
import com.orels.domain.interactors.DataSourceCallsInteractor
import com.orels.domain.interactors.PhoneCallsInteractor
import com.orels.domain.managers.phonecall.CallState
import com.orels.domain.managers.phonecall.PhoneCallManager
import com.orels.domain.model.entities.CallLogEntity
import com.orels.domain.model.entities.PhoneCall
import com.orels.domain.model.entities.UploadState
import com.orels.domain.util.common.Constants.TIME_TO_ADD_CALL_TO_CALL_LOG
import com.orels.domain.util.common.Logger
import com.orels.domain.util.extension.compareNumberTo
import com.orels.domain.util.extension.epochTimeInSeconds
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@Suppress("MoveVariableDeclarationIntoWhen")
class PhoneCallManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val phoneCallInteractor: PhoneCallsInteractor,
    private val dataSource: DataSourceCallsInteractor,
    private val callLogInteractor: CallLogInteractor
) : PhoneCallManager {

    override val callsDataFlow
        get() = dataSource.callsPreferencesFlow()
    override val callsData: CallPreferences
        get() = CallPreferences(
            callOnTheLine = dataSource.getCallOnTheLine()?.number,
            callInTheBackground = dataSource.getCallInTheBackground()?.number,
            callState = dataSource.getState()?.value
        )

    override fun onStateChanged(state: String, number: String, context: Context?) {
        Logger.i("state: $state \n number: $number, audio manager state: ${getAudioManagerMode()}")
        when (state) {
            TelephonyManager.EXTRA_STATE_IDLE -> onIdleState()
            TelephonyManager.EXTRA_STATE_RINGING -> onRingingState(number)
            TelephonyManager.EXTRA_STATE_OFFHOOK -> onOffHookState(number)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun hangupCall(context: Context) {
//        val mgr: TelecomManager? = context.getSystemService(TELECOM_SERVICE) as TelecomManager?
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ANSWER_PHONE_CALLS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
    }

    private fun onIdleState() {
        updateActualEndDate()
        resetIfNoActiveCall()
    }

    private fun updateActualEndDate() {
        phoneCallInteractor.getAll().filter { it.uploadState == UploadState.NotUploaded }.forEach {
            it.actualEndDate = Date()
        }
    }

    private fun onRingingState(number: String) {
        val previousState = dataSource.getState()
        when (previousState) {
            CallState.Idle -> {
                incomingCall(number = number)
            }
            CallState.OnCall -> {
                waitingCall(number = number)
            }
            else -> { // This state should not happen, but if it did it's an incoming call
                incomingCall(number = number)
            }
        }
    }

    private fun onOffHookState(number: String) {
        val previousState = dataSource.getState()
        when (previousState) {
            CallState.Waiting -> {
                checkWaitingCallState()
            }
            CallState.Idle -> {
                outgoingCall(number = number)
            }
            CallState.Ringing -> {
                incomingAnswered()
            }
            else -> { // This state should not happen, but if it did it's an outgoing call
                outgoingCall(number = number)
            }
        }
    }

    private fun setBackgroundCall(phoneCall: PhoneCall?) {
        Logger.i("Set call in the background: ${phoneCall?.number}")
        CoroutineScope(Dispatchers.Main).launch {
            dataSource.updateCallInTheBackground(phoneCall)
        }
    }

    private fun setCallOnLine(phoneCall: PhoneCall?) {
        Logger.i("Set call on line: ${phoneCall?.number}")
        CoroutineScope(Dispatchers.Main).launch {
            dataSource.updateCallOnTheLine(phoneCall)
        }
    }

    private fun setStateValue(callState: CallState) {
        Logger.i("Set state: ${callState.value}")
        CoroutineScope(Dispatchers.Main).launch {
            dataSource.updateState(callState)
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

    private fun checkWaitingCallState() {
        setStateValue(CallState.OnCall)
        CoroutineScope(Dispatchers.Main).launch {
            val callLog =
                callLogInteractor.getLastCallLog(delay = TIME_TO_ADD_CALL_TO_CALL_LOG)
            val callInBackground = dataSource.getCallInTheBackground()
            if (callInBackground != null && callInBackground.isEqualsToCallLog(callLog)) {
                waitingCallNotAnswered()
            } else {
                waitingCallAnswered()
            }
        }
    }

    private fun waitingCallAnswered() {
        val backgroundCallHolder = dataSource.getCallInTheBackground()
        setBackgroundCall(dataSource.getCallOnTheLine())
        setCallOnLine(backgroundCallHolder)
    }

    private fun waitingCallNotAnswered() {
        CoroutineScope(Dispatchers.Main).launch {
            dataSource.updateCallInTheBackground(null)
        }
    }

    private fun addToBacklog(phoneCall: PhoneCall?) {
        if (phoneCall == null) return
        phoneCallInteractor.cachePhoneCall(phoneCall = phoneCall)
    }

    @SuppressLint("SwitchIntDef")
    private fun getAudioManagerMode(): String {
        val manager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return when (manager.mode) {
            MODE_CALL_SCREENING -> "MODE_CALL_SCREENING"
            MODE_CURRENT -> "MODE_CURRENT"
            MODE_INVALID -> "MODE_INVALID"
            MODE_IN_CALL -> "MODE_IN_CALL"
            MODE_IN_COMMUNICATION -> "MODE_IN_COMMUNICATION"
            MODE_NORMAL -> "MODE_NORMAL"
            MODE_RINGTONE -> "MODE_RINGTONE"
            else -> ""
        }

    }

    override fun resetIfNoActiveCall() {
        Logger.i("reset phonecall manager")
        setStateValue(CallState.Idle)
        setCallOnLine(null)
        setBackgroundCall(null)
    }
}

fun PhoneCall.isEqualsToCallLog(callLog: CallLogEntity?): Boolean =
    callLog != null &&
            callLog.number.compareNumberTo(number)
            && (
            callLog.time.epochTimeInSeconds < startDate.time.epochTimeInSeconds + 6 && callLog.time.epochTimeInSeconds > startDate.time.epochTimeInSeconds - 6
            )
