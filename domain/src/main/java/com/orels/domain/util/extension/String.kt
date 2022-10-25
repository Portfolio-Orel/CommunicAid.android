package com.orels.domain.util.extension

fun String.withoutPrefix(): String =
    if (length > 10) {
        "0${substring(4, length)}"
    } else {
        this
    }

fun String.compareNumberTo(otherNumber: String) =
    withoutPrefix() == otherNumber.withoutPrefix()
