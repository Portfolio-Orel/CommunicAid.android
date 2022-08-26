package com.orelzman.mymessages.domain.system.phone_call

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.mymessages.MainActivity
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.managers.phonecall.PhoneCallManager
import com.orelzman.mymessages.domain.model.entities.SettingsKey
import com.orelzman.mymessages.domain.util.extension.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @author Orel Zilberman
 * 25/08/2022
 */

/**
 * This receiver MUST come first before any other receiver.
 */
@AndroidEntryPoint
class SettingsPhoneCallReceiver : BroadcastReceiver() {

    @Inject
    lateinit var phoneCallManager: PhoneCallManager

    @Inject
    lateinit var settingsInteractor: SettingsInteractor

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            PHONE_STATE -> {
                if (debounceExtra(intent)) return
                val state = intent.extras?.getString(TelephonyManager.EXTRA_STATE) ?: return
                checkStartAppOnCallSettings(state = state, context = context)
            }
        }
    }

    /**
     * The receiver receives data twice for each state.
     * One with a number and one without.
     */
    @Suppress("DEPRECATION")
    private fun debounceExtra(intent: Intent): Boolean =
        intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER) == null

    private fun checkStartAppOnCallSettings(state: String, context: Context) {
        val settings = settingsInteractor.getSettings(SettingsKey.ShowAppOnCall)
        if (settings.getRealValue<Boolean>() == true && settings.arePermissionsGranted(context = context)
                .isEmpty()) {
            if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                showApp(context = context)
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    private fun showApp(context: Context) {
        val appIntent = Intent(context, MainActivity::class.java)
        appIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(appIntent)
        Logger.v("started activity")
    }

    companion object {
        val PHONE_STATE: String
            get() = "android.intent.action.PHONE_STATE"

        fun enable(context: Context) {
            val pm: PackageManager = context.packageManager
            val componentName =
                ComponentName(context, SettingsPhoneCallReceiver::class.java)
            pm.setComponentEnabledSetting(
                componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }

        fun disable(context: Context) {
            val pm: PackageManager = context.packageManager
            val componentName =
                ComponentName(context, SettingsPhoneCallReceiver::class.java)
            pm.setComponentEnabledSetting(
                componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }
}