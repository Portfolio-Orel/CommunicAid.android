package com.orels.auth.domain.util

class PasswordGenerator {
    companion object {
        fun generateStrongPassword(length: Int = 12): String {
            val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            val lower = "abcdefghijklmnopqrstuvwxyz"
            val digits = "0123456789"
            val special = "!@#$%^&*()_+-=[]{}|;:,.<>?"
            val allChars = upper + lower + digits + special

            val random = kotlin.random.Random.Default
            var password = ""

            // Add at least one character from each category
            password += upper[random.nextInt(upper.length)]
            password += lower[random.nextInt(lower.length)]
            password += digits[random.nextInt(digits.length)]
            password += special[random.nextInt(special.length)]

            // Add remaining characters
            repeat(length - 4) {
                password += allChars[random.nextInt(allChars.length)]
            }

            // Shuffle the password to ensure that the required characters are not in predictable positions
            password = password.toList().shuffled().joinToString("")

            return password
        }
    }
}