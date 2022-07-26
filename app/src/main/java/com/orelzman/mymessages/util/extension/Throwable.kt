package com.orelzman.mymessages.util.extension

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.orelzman.mymessages.domain.model.entities.Loggable

fun Throwable.log(values: Map<String, String> = emptyMap()) {
    val crashlytics = Firebase.crashlytics
    for ((value, key) in values) {
        crashlytics.setCustomKey(key, value)
    }
    crashlytics.log(stackTraceToString())
    Log.e("Crash logged", this, values)
    throw this
}

fun Throwable.log(loggable: Loggable) {
    val crashlytics = Firebase.crashlytics
    for ((key, value) in loggable.data) {
        crashlytics.setCustomKey(key, value.toString())
    }
    crashlytics.log(stackTraceToString())
}

fun Throwable.log(loggableList: List<Loggable> = emptyList(), message: String = "") {
    val crashlytics = Firebase.crashlytics
    loggableList.forEach { loggable ->
        for ((key, value) in loggable.data) {
            crashlytics.setCustomKey(key, value.toString())
        }
    }
    crashlytics.log(stackTraceToString())

}