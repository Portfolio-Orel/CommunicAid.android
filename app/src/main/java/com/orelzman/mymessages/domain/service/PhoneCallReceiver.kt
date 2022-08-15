package com.orelzman.mymessages.domain.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.orelzman.mymessages.domain.managers.phonecall.PhoneCallManager
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
                stateStr?.let {
                    phoneCallManager.onStateChanged(it, number, context)
                }
            }
        }
    }

    companion object {
        val PHONE_STATE: String
            get() = "android.intent.action.PHONE_STATE"
    }
}