package com.orels.presentation.ui.login.forgot_password

import androidx.annotation.StringRes

data class ForgotPasswordState(
    val state: State = State.ForgotPassword(false),

    val codeField: Fields = Fields.Code(),
    val passwordField: Fields = Fields.Password(),
    val confirmPasswordField: Fields = Fields.ConfirmPassword(),
    val usernameField: Fields = Fields.Username(),

    val isLoading: Boolean = false,
    @StringRes val error: Int? = null,

    )

sealed class State(val isLoading: Boolean = false, @StringRes val error: Int? = null) {
    class ForgotPassword(isLoading: Boolean = false) : State(isLoading = isLoading)
    class ResetPassword(isLoading: Boolean = false) : State(isLoading = isLoading)
    object Done : State()
}

sealed class Fields(val isError: Boolean = false) {
    class Code(isError: Boolean = false) : Fields(isError = isError)
    class Password(isError: Boolean = false) : Fields(isError = isError)
    class ConfirmPassword(isError: Boolean = false) : Fields(isError = isError)
    class Username(isError: Boolean = false) : Fields(isError = isError)
}