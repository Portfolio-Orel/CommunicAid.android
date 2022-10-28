package com.orels.domain.system.proximity

import android.content.Context
import android.os.PowerManager
import com.orels.domain.managers.system_service.SystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * @author Orel Zilberman
 * 07/09/2022
 */
class ProximityManagerImpl @Inject constructor(@ApplicationContext private val context: Context) :
    SystemService() {

    private var powerManager: PowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private var lock: PowerManager.WakeLock =
        powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,PROXIMITY_WAKELOCK_TAG)

    override fun disable() {
        super.disable()
        if(lock.isHeld) lock.release()
    }

    override fun enable() {
        super.enable()
        if(!lock.isHeld) lock.acquire(ACQUIRE_WAKELOCK_TIME)
    }

    companion object {
        const val PROXIMITY_WAKELOCK_TAG = "MyMessages:proximity_wakelock"
        const val ACQUIRE_WAKELOCK_TIME = 10*60*1000L // 10 minutes
    }

}