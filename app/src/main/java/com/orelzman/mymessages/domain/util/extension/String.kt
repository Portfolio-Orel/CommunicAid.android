package com.orelzman.mymessages.domain.util.extension

fun String.withoutPrefix(): String =
    if (length > 10) {
        "0${substring(4, length)}"
    } else {
        this
    }
