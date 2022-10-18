package com.orels.domain.util.common

import android.util.Log
import com.orels.domain.interactors.AuthLogger

/**
 * @author Orel Zilberman
 * 04/10/2022
 */

class Logger : AuthLogger {
    companion object {
        private const val TAG = ":::MyMessages:::"
        private val logger = com.datadog.android.log.Logger.Builder()
            .setNetworkInfoEnabled(true)
            .setLogcatLogsEnabled(true)
            .setDatadogLogsEnabled(true)
            .setLoggerName("MyMessages Logger")
            .build()

        fun v(
            message: String,
            throwable: Throwable? = null,
            attributes: Map<String, Any?> = emptyMap()
        ) {
            Log.v(TAG, "$message\n$attributes")
            logger.v(
                message = message,
                throwable = throwable,
                attributes = attributes
            )
        }

        @Suppress("unused")
        fun vNoRemoteLogging(
            message: String
        ) {
            Log.v(TAG, message)
        }

        fun i(
            message: String,
            attributes: Map<String, Any?> = emptyMap(),
            tag: String? = null
        ) {
            Log.i(tag ?: TAG, "$message\n$attributes")
            logger.i(
                message = message,
                attributes = attributes
            )
        }

        fun e(
            message: String,
            throwable: Throwable? = null,
            attributes: Map<String, Any?> = emptyMap(),
            tag: String? = null
        ) {
            Log.e(tag ?: TAG, "$message\n$throwable\n${attributes}")
            logger.e(
                message = message,
                throwable = throwable,
                attributes = attributes
            )
        }
    }

    override fun info(tag: String, data: Any) {
        i(message = "$data", tag = tag)
    }

    override fun error(tag: String, data: Any, e: Throwable) {
        e(
            message = "$data",
            tag = tag,
            throwable = e
        )
    }
}