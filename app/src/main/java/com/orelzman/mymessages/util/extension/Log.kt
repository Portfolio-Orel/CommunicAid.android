package com.orelzman.mymessages.util.extension

import android.util.Log

class Log {
    companion object {
        private const val TAG = ":::MyMessages:::"
        
        fun v(message: Any) {
            Log.v(TAG, message.toString())
        }

        fun eCustom(message: String) {
            Log.e(TAG, message)
        }
    }
}