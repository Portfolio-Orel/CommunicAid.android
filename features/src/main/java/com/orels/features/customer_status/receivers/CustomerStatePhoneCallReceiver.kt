package com.orels.features.customer_status.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.orels.domain.interactors.SettingsInteractor
import com.orels.features.customer_status.data.service.CustomerStatusService
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
        startCustomerStatusService(context)
    }


    private fun startCustomerStatusService(context: Context) {
        val serviceIntent = Intent(context, CustomerStatusService::class.java)
        context.startForegroundService(serviceIntent)
    }
}