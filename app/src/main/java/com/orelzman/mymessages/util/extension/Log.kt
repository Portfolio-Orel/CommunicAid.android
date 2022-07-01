package com.orelzman.mymessages.util.extension

import android.util.Log
import java.io.File

class Log {
    companion object {
        fun vCustom(message: String) {
            Log.v("MyMessages logs:", message)
            println("MyMessages: $message")
        }
    }

    fun Log.printVCustom() {
        val filename = "MyMessagesLogs.txt"
        val file = File(filename)
        if (!file.exists()) {
            return
        }
        Log.v("MyMessages", "Logs: \n ${file.readLines()}")
    }

    private fun writeToLogFile(message: String) {
        val filename = "MyMessagesLogs.txt"
        val file = File(filename)
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(message + "\n")
    }
}