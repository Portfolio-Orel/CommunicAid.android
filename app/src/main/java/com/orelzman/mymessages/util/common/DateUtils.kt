package com.orelzman.mymessages.util.common

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
}