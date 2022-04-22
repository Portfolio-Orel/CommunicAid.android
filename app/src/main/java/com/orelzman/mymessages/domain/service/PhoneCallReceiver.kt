package com.orelzman.mymessages.domain.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.orelzman.mymessages.domain.service.PhoneCall.PhoneCallManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PhonecallReceiver @Inject constructor(private val phoneCallManager: PhoneCallManager) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        try {
            if (intent != null) {
                val stateStr = intent.extras?.getString(TelephonyManager.EXTRA_STATE)
                @Suppress("DEPRECATION") val number =
                    intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                        ?: return
                when (stateStr) {
                    TelephonyManager.EXTRA_STATE_IDLE -> phoneCallManager.onIdleState()
                    TelephonyManager.EXTRA_STATE_RINGING -> phoneCallManager.onRingingState(number, context)
                    TelephonyManager.EXTRA_STATE_OFFHOOK -> phoneCallManager.onOffHookState(number, context)
                }
            }
        } catch (ex: Exception) {
        }
    }


}