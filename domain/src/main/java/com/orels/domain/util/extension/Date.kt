@file:Suppress("unused")

package com.orels.domain.util.extension

import android.content.Context
import com.orels.domain.R
import com.orels.domain.util.common.DateUtils
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

fun Date.dayOfMonth(): Int {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal.get(Calendar.DAY_OF_MONTH)
}

fun Date.month(): Int {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal.get(Calendar.MONTH)
}

fun Date.year(): Int {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal.get(Calendar.YEAR)
}


/**
 * Returns the hour of [this] in the format of: HH:MM
 */
fun Date.getHourHHMM(): String {
    var formattedHour = ""
    val cal = Calendar.getInstance()
    cal.time = this
    val hours = cal.get(Calendar.HOUR_OF_DAY)
    val minutes = cal.get(Calendar.MINUTE)
    if (hours < 10) formattedHour += "0"
    formattedHour += "$hours:"
    if (minutes < 10) formattedHour += "0"
    formattedHour += "$minutes"
    return formattedHour
}

/**
 * Returns the day of [this] in the following format:
 * If [this] is today -> todayString
 * If [this] is yesterday -> yesterdayString
 * If [this] is a day in this week -> day of the week string
 * else DD/MM/YYYY
 */
fun Date.getDayFormatted(context: Context? = null, withYear: Boolean = true): String {
    val todayCal = Calendar.getInstance()
    val cal = Calendar.getInstance()
    val dateString: String
    cal.time = this
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
    val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
    val month = cal.get(Calendar.MONTH)
    val year = cal.get(Calendar.YEAR)

    dateString = "$dayOfMonth/${month + 1}${if (withYear) "/$year" else ""}"
    if (context == null) return dateString

    if (year == todayCal.get(Calendar.YEAR)) { // Same year
        if (month == todayCal.get(Calendar.MONTH)) { // Same month
            when {
                dayOfYear == todayCal.get(Calendar.DAY_OF_YEAR) -> { // Today
                    return context.getString(R.string.today)
                }
                dayOfYear == todayCal.get(Calendar.DAY_OF_YEAR) + 1 -> { // Yesterday
                    return context.getString(R.string.yesterday)
                }
                dayOfWeek > todayCal.get(Calendar.DAY_OF_WEEK) -> { // A previous day of the week
                    return DateUtils.getDayOfWeek(context, dayOfWeek) ?: dateString
                }
            }
        }
    }
    return dateString
}

fun Calendar.toDate(): Date = Date(timeInMillis)

/**
 * Returns date(today, yesterday, tomorrow or date) • HH:MM
 */
fun Date.formatDayAndHours(context: Context? = null): String =
    "${getDayFormatted(context)} • ${getHourHHMM()}"

fun Calendar.resetTime() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
}

fun Calendar.maxTime() {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
}