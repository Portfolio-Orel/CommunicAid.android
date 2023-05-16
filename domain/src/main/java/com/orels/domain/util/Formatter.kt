package com.orels.domain.util

import java.util.*


class Formatters {
    companion object {
        private const val MAX_DAY = 31
        private const val MAX_MONTH = 12
        private const val MIN_YEAR = 1900
        private val maxYear: Int
            get() = Calendar.getInstance().get(Calendar.YEAR)

        private fun String.validateDay(): String {
            return if ((this.toIntOrNull() ?: 0) > MAX_DAY) {
                MAX_DAY.toString()
            } else {
                this
            }
        }

        private fun String.validateMonth(): String {
            return if ((this.toIntOrNull() ?: 0) > MAX_MONTH) {
                MAX_MONTH.toString()
            } else {
                this
            }
        }

        fun toDateString(string: String): String {
            val regex = Regex("\\D")
            val digits = regex.replace(string, "")
            if (digits.length > 9) {
                return string
            }

            val day: String = digits.take(2).validateDay()

            val month: String = digits.takeIf {
                digits.length > 2
            }?.let {
                digits.takeLast(digits.length - 2).take(2).validateMonth()
            } ?: ""

            val year: String = digits.takeIf {
                digits.length > 4
            }?.let {
                digits.takeLast(digits.length - 4)
            } ?: ""

            var newString = ""
            if (day.isNotBlank()) {
                newString += if (day.length == 2) {
                    "$day/"
                } else {
                    day
                }
            }
            if (month.isNotBlank()) {
                newString += if (month.length == 2) {
                    "$month/"
                } else {
                    month
                }
            }
            if (year.isNotBlank()) {
                if (year.length == 4) {
                    when {
                        (year.toIntOrNull() ?: 0) < MIN_YEAR -> newString += MIN_YEAR
                        (year.toIntOrNull() ?: 0) > maxYear -> newString += maxYear
                        else -> newString += year
                    }
                } else {
                    newString += year
                }
            }
            return newString
        }

        fun toDayString(string: String): String {
            val regex = Regex("\\D")
            val digits = regex.replace(string, "")
            val day = if (digits.length >= 2) {
                digits.take(2)
            } else {
                digits
            }
            return if ((day.toIntOrNull() ?: 0) > MAX_DAY) {
                return MAX_DAY.toString()
            } else {
                day
            }
        }

        fun toMonthString(string: String): String {
            val regex = Regex("\\D")
            val digits = regex.replace(string, "")
            val month = if (digits.length >= 2) {
                digits.takeLast(2)
            } else {
                digits
            }
            return if ((month.toIntOrNull() ?: 0) > 12) {
                MAX_MONTH.toString()
            } else {
                digits
            }
        }

        fun toYearString(string: String): String {
            val regex = Regex("\\D")
            val digits = regex.replace(string, "")
            return digits.takeLast(4)
        }
    }
}