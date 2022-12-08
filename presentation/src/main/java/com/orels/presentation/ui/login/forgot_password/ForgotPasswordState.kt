package com.orels.presentation.ui.login.forgot_password

import androidx.annotation.StringRes

data class ForgotPasswordState(
    val state: State = State.ForgotPassword(false),
    val isLoading: Boolean = false,
    @StringRes val error: Int? = null
)

sealed class State(val isLoading: Boolean = false, @StringRes val error: Int? = null) {
    class ForgotPassword(isLoading: Boolean = false) : State(isLoading = isLoading)
    class ResetPassword(isLoading: Boolean = false) : State(isLoading = isLoading)
    object Done : State()
    object Back : State()
}