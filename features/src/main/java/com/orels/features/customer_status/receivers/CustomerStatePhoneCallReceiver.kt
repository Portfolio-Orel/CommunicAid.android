package com.orels.features.customer_status.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.orels.domain.interactors.SettingsInteractor
import com.orels.features.customer_status.data.service.CustomerStatusService
import com.orels.features.customer_status.presentation.CustomerStateActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by Orel Zilberman on 30/03/2024.
 */

@AndroidEntryPoint
class CustomerStatePhoneCallReceiver : BroadcastReceiver() {
    @Inject
    lateinit var settingsInteractor: SettingsInteractor

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        val state = intent?.extras?.getString(TelephonyManager.EXTRA_STATE) ?: return
//        checkStartAppOnCallSettings(state = state, context = context)
        startCustomerStatusService(context)
//        showPopupActivity(context)
    }


    private fun startCustomerStatusService(context: Context) {
//        val serviceIntent = Intent(context, CustomerStatusService::class.java)
//        context.startForegroundService(serviceIntent)
        val serviceIntent = Intent(context, CustomerStatusService::class.java)
        context.startForegroundService(serviceIntent)
    }

    private fun showPopupActivity(context: Context) {
        val popupIntent = Intent(context, CustomerStateActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(popupIntent)
    }

//    private fun checkStartAppOnCallSettings(state: String, context: Context) {
//        val settings = settingsInteractor.getSettings(SettingsKey.ShowAppOnCall)
//        if (settings.getRealValue<Boolean>() == true && settings.getPermissionsNotGranted(context = context)
//                .isEmpty()
//        ) {
//            if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
//                showApp(context = context)
//            }
//        }
//    }

    private fun showApp(context: Context) {
//        val appIntent = Intent(context, MainActivity::class.java)
//        appIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//        context.startActivity(appIntent)
//        Logger.v("started activity")
    }
}