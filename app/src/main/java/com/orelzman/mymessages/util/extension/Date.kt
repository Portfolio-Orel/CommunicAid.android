package com.orelzman.mymessages.util.extension

import java.util.*

fun Date.compareToBallPark(date: Date): Boolean = time.inSeconds > date.time.inSeconds - 10
        && time.inSeconds < date.time.inSeconds + 10

fun Date.seconds(): Int {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal.get(Calendar.SECOND)
}

fun Date.minutes(): Int {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal.get(Calendar.MINUTE)
}

fun Date.hours(): Int {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal.get(Calendar.HOUR_OF_DAY)
}