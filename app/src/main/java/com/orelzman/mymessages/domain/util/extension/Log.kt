package com.orelzman.mymessages.domain.util.extension

import android.util.Log
import com.datadog.android.log.Logger

class Log {
    companion object {
        private const val TAG = ":::MyMessages:::"
        private val logger = Logger.Builder()
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

        fun i(
            message: String,
            throwable: Throwable? = null,
            attributes: Map<String, Any?> = emptyMap()
        ) {
            Log.i(TAG, "$message\n$attributes")
            logger.i(
                message = message,
                throwable = throwable,
                attributes = attributes
            )
        }

        fun e(
            message: String,
            throwable: Throwable? = null,
            attributes: Map<String, Any?> = emptyMap()
        ) {
            Log.e(TAG, "$message\n$throwable\n${attributes}")
            logger.e(
                message = message,
                throwable = throwable,
                attributes = attributes
            )
        }
    }
}