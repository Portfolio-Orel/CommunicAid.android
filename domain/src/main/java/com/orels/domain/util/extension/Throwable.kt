package com.orels.domain.util.extension

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.orels.domain.model.entities.Loggable
import com.orels.domain.util.common.Logger

fun Throwable.log(values: Map<String, String> = emptyMap()) {
    val crashlytics = Firebase.crashlytics
    for ((value, key) in values) {
        crashlytics.setCustomKey(key, value)
    }
    crashlytics.log(stackTraceToString())
    Logger.e("Exception caught ${this.message}", this, values)
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