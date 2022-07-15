package com.orelzman.mymessages.util.extension

import android.util.Log

class Log {
    companion object {
        fun vCustom(message: String) {
            Log.v("MyMessages logs:", message)
            println("MyMessages: $message")
        }

        fun eCustom(message: String) {
            Log.e("MyMessages logs:", message)
            println("MyMessages: $message")
        }
    }
}