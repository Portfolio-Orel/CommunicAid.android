package com.orels.domain.util

import androidx.annotation.StringRes
import com.orels.domain.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Main : Screen("main", R.string.main)
    object Login : Screen("login", R.string.login)
    object UnhandledCalls : Screen("unhandled_calls", R.string.unhandled_calls)
    object Statistics : Screen("statistics", R.string.statistics)
    object DetailsMessage : Screen("details_message", R.string.details_message)
    object DetailsFolder : Screen("details_folder", R.string.details_folder)
    object Settings : Screen("settings", R.string.settings)

    fun withArgs(vararg args: String?): String =
        buildString {
            append(route)
            args.forEach { arg ->
                append("/${arg ?: ""}")
            }
        }
}
