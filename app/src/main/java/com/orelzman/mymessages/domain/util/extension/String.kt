package com.orelzman.mymessages.domain.util.extension

import java.util.*

fun String.toUUIDOrNull(): UUID? {
    return try {
        UUID.fromString(this)
    } catch (e: Exception) {
        null
    }
}