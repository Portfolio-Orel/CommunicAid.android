package com.orels.domain.util.common

import android.content.Context
import com.orels.domain.R
import com.orels.domain.util.extension.maxTime
import com.orels.domain.util.extension.resetTime
import com.orels.domain.util.extension.toDate
import java.util.*

object DateUtils {
    fun getStartOfDay(): Date {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DATE]
        calendar[year, month, day, 0, 0] = 0
        return calendar.time
    }

    fun getDayOfWeek(context: Context, day: Int): String? =
        when (day) {
            1 -> context.getString(R.string.sunday)
            2 -> context.getString(R.string.monday)
            3 -> context.getString(R.string.tuesday)
            4 -> context.getString(R.string.wednesday)
            5 -> context.getString(R.string.thursday)
            6 -> context.getString(R.string.friday)
            7 -> context.getString(R.string.saturday)
            else -> null
        }


    /**
     * Returns the date of the first day of [date]'s week.
     */
    fun getFirstDayOfWeek(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val daysAfterStartOfWeek = 1 - calendar.get(Calendar.DAY_OF_WEEK)
        calendar.add(Calendar.DAY_OF_WEEK, daysAfterStartOfWeek)
        calendar.resetTime()
        return calendar.toDate()
    }
// 3   2 , 6  5
    /**
     * Returns the date of the last day of [date]'s week.
     */
    fun getLastDayOfWeek(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_WEEK, 7 - calendar.get(Calendar.DAY_OF_WEEK))
        calendar.maxTime()
        return calendar.toDate()
    }

    /**
     * Returns the date of the first day of [date]'s month.
     */
    fun getFirstDayOfMonth(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.resetTime()
        return calendar.toDate()
    }

    /**
     * Returns the date of the last day of [date]'s month.
     */
    fun getLastDayOfMonth(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, -1)
        calendar.maxTime()
        return calendar.toDate()
    }

}