package com.orelzman.mymessages.util.extension

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

fun Throwable.log(values: Map<String, String> = emptyMap()) {
    val crashlytics = Firebase.crashlytics
    for((value, key) in values) {
        crashlytics.setCustomKey(key, value)
    }
    crashlytics.log(stackTraceToString())
}