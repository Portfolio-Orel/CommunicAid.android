package com.orelzman.mymessages.domain.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.orelzman.mymessages.domain.service.PhoneCall.PhoneCallManager
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
                @Suppress("DEPRECATION") val number =
                    intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                        ?: return
                when (stateStr) {
                    TelephonyManager.EXTRA_STATE_IDLE -> phoneCallManager.onIdleState(
                        context = context
                    )
                    TelephonyManager.EXTRA_STATE_RINGING -> phoneCallManager.onRingingState(
                        number = number,
                        context = context
                    )
                    TelephonyManager.EXTRA_STATE_OFFHOOK -> phoneCallManager.onOffHookState(
                        number = number,
                        context = context
                    )
                }
            }
        }
    }

    companion object {
        val PHONE_STATE: String
            get() = "android.intent.action.PHONE_STATE"
    }
}