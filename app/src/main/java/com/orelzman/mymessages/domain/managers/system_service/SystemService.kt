package com.orelzman.mymessages.domain.managers.system_service

/**
 * In order for the derived classes to work smoothly with the app,
 * overriden functions should call super.
 * @author Orel Zilberman
 * 07/09/2022
 */
abstract class SystemService {
    open fun disable() {
        isSuspended = false
        isEnabled = false
    }
    open fun enable() {
        isSuspended = false
        isEnabled = true
    }
    open fun suspend() {
        disable()
        isSuspended = true
    }

    fun isSuspended() = isSuspended

    fun isEnabled() = isEnabled

    companion object {
        var isSuspended: Boolean = false
        var isEnabled: Boolean = false
    }
}