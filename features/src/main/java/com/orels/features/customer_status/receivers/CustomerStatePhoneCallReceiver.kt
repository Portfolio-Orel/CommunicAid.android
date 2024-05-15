package com.orels.features.customer_status.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.orels.domain.interactors.SettingsInteractor
import com.orels.domain.model.entities.SettingsKey
import com.orels.domain.util.common.Logger
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
        if (settingsInteractor.getSettings(SettingsKey.ShowCustomerStateOnCall)
                .getRealValue<Boolean>() == false
        ) return
        val state = intent?.extras?.getString(TelephonyManager.EXTRA_STATE)
        val intent = Intent(context, CustomerStateActivity::class.java)
        if (state == TelephonyManager.EXTRA_STATE_RINGING) {
            Logger.i("CustomerStatePhoneCallReceiver onReceive")
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            context.startActivity(intent)
        }
    }
}