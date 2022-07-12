package com.orelzman.mymessages.domain.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManager
import com.orelzman.mymessages.util.extension.Log
import com.orelzman.mymessages.util.extension.log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PhonecallReceiver : BroadcastReceiver() {

    @Inject
    lateinit var phoneCallManager: PhoneCallManager

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            PHONE_STATE -> {
                val stateStr = intent.extras?.getString(TelephonyManager.EXTRA_STATE)
                if (stateStr == TelephonyManager.EXTRA_STATE_IDLE) {
                    startBackgroundService(context, ServiceState.UPLOAD_LOGS)
                } else {
                    startBackgroundService(context)
                }
                @Suppress("DEPRECATION") val number =
                    intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                        ?: return
                stateStr?.let {
                    phoneCallManager.onStateChanged(it, number, context)
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    private fun startBackgroundService(
        context: Context,
        state: ServiceState = ServiceState.ALIVE
    ) {
        // Avoid starting the service twice for the same purpose
        if (state == CallsService.currentState) {
            return
        }
        val intent = Intent(context, CallsService::class.java)
        intent.putExtra(CallsService.INTENT_STATE_VALUE, state)
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

    companion object {
        val PHONE_STATE: String
            get() = "android.intent.action.PHONE_STATE"
    }
}