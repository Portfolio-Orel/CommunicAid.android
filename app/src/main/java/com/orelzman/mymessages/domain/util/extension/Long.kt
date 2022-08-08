package com.orelzman.mymessages.domain.util.extension

import java.util.*

val Long.inSeconds: Long
    get() =
        if ("$this".length > 10) {
            this / 1000
        } else {
            this
        }

val Long.inMilliseconds: Long
    get() =
        if ("$this".length > 10) {
            this
        } else {
            this * 1000
        }

fun Long.toDate(): Date =
    if ("$this".length > 10) {
        Date(this)
    } else {
        Date(this * 1000)
    }