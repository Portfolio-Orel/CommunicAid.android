package com.orels.domain.util.extension

import java.text.SimpleDateFormat
import java.util.*

val Long.epochTimeInSeconds: Long
    get() =
        if ("$this".length > 10) {
            this / 1000
        } else {
            this
        }

val Long.epochTimeInMilliseconds: Long
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

fun Long.toRegisterDate(): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(this))
