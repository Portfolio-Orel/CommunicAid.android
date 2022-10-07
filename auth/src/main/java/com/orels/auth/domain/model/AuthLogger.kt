package com.orels.auth.domain.model

/**
 * @author Orel Zilberman
 * 01/10/2022
 */
interface AuthLogger {
    fun info(tag: String, data: Any)
    fun error(tag: String, data: Any, e: Throwable)
}