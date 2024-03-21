package com.orels.data.managers.system_service

import com.orels.domain.annotation.Proximity
import com.orels.domain.managers.SystemServiceManager
import com.orels.domain.managers.system_service.SystemService
import javax.inject.Inject

/**
 * @author Orel Zilberman
 * 07/09/2022
 */

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