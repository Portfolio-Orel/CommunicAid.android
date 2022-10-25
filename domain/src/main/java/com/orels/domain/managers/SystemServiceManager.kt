package com.orels.domain.managers

/**
 * @author Orel Zilberman
 * 04/10/2022
 */
interface SystemServiceManager {
    fun onResume()
    fun onPause()
    fun onDestroy()
}