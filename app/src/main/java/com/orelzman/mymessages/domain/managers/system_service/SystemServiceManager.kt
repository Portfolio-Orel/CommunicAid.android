package com.orelzman.mymessages.domain.managers.system_service

import com.orelzman.mymessages.di.annotation.Proximity
import javax.inject.Inject

/**
 * @author Orel Zilberman
 * 07/09/2022
 */

interface SystemServiceManager {
    fun onResume()
    fun onPause()
    fun onDestroy()
}

class SystemServiceManagerImpl @Inject constructor(
    @Proximity proximityManager: SystemService
) : SystemServiceManager {

    private val systemServicesList: ArrayList<SystemService> = ArrayList()

    init {
        systemServicesList.add(proximityManager)
    }

    override fun onResume() {
        systemServicesList.forEach {
            if (it.isSuspended()) {
                it.enable()
            }
        }
    }

    override fun onPause() {
        systemServicesList.forEach {
            if (it.isEnabled()) {
                it.suspend()
            }
        }
    }

    override fun onDestroy() {
        systemServicesList.forEach {
            it.suspend()
        }
    }
}