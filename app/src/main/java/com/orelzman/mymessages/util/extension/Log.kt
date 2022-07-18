package com.orelzman.mymessages.util.extension

import android.util.Log

class Log {
    companion object {
        fun v(message: Any) {
            Log.v("MyMessages logs:", message.toString())
        }

        fun eCustom(message: String) {
            Log.e("MyMessages logs:", message)
        }
    }
}