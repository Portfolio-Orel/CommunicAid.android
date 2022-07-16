package com.orelzman.mymessages.domain.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManager
import com.orelzman.mymessages.domain.workers.UploadWorker
import com.orelzman.mymessages.util.extension.Log
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
                if (stateStr == TelephonyManager.EXTRA_STATE_IDLE) {
                    startUploadCallsWorker(context)
                }
                stateStr?.let {
                    phoneCallManager.onStateChanged(it, number, context)
                }
            }
        }
    }

    private fun startUploadCallsWorker(context: Context) {
        Log.v("About to start upload worker")
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadWorker>()
                .build()
        WorkManager.getInstance(context)
            .enqueue(uploadWorkRequest)
    }

    companion object {
        val PHONE_STATE: String
            get() = "android.intent.action.PHONE_STATE"
    }
}