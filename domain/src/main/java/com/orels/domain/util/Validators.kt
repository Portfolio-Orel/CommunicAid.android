package com.orels.domain.util

class Validators {
    companion object {
        private val emailPattern = Regex("[a-zA-Z\\d._%+-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,}")
        private val phoneNumberPattern = Regex("\\d{10}")

        fun isEmailValid(email: String): Boolean = emailPattern.matches(email)

        fun isPhoneNumberValid(number: String): Boolean = phoneNumberPattern.matches(number)

    }
}