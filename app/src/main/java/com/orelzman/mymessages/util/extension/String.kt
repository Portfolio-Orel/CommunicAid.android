package com.orelzman.mymessages.util.extension

import java.util.*

fun String.toUUIDOrNull(): UUID? {
    return try {
        UUID.fromString(this)
    } catch (e: Exception) {
        null
    }
}