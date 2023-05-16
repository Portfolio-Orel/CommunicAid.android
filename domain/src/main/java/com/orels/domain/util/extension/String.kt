package com.orels.domain.util.extension

import java.text.SimpleDateFormat
import java.util.*

fun String.withoutPrefix(): String =
    if (length > 10) {
        "0${substring(4, length)}"
    } else {
        this
    }

fun String.compareNumberTo(otherNumber: String) =
    withoutPrefix() == otherNumber.withoutPrefix()

fun String.toRegisterDateLong(): Long =
    try {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(this)?.time ?: throw Exception()
    } catch (e: Exception) {
        // TODO: Log that
        0L
    }

fun String?.isDateValid(): Boolean = this != null && try {
    if (this.length != 10) {
        false
    } else {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(this)
        true
    }
} catch (e: Exception) {
    false
}

fun String.takeOrEmpty(n: Int): String = if (length >= n) take(n) else ""

fun String.toRegisterDate(): Date? =
    try {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }


fun String.takeLastOrEmpty(n: Int = 1): String? {
    return if (this.length >= n) {
        this.takeLast(n)
    } else {
        null
    }
}