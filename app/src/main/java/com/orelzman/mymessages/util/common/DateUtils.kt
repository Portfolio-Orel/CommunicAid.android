package com.orelzman.mymessages.util.common

import android.content.Context
import com.orelzman.mymessages.R
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
    fun Date.getDayFormatted(context: Context): String {
        val todayCal = Calendar.getInstance()
        val cal = Calendar.getInstance()
        val dateString: String
        cal.time = this
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)

        dateString = "${dayOfMonth}/${month + 1}/$year"

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
                        return getDayOfWeek(context, dayOfWeek) ?: dateString
                    }
                }
            }
        }
        return dateString
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
    

}