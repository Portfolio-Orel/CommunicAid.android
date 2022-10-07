package com.orels.presentation.ui.login.components.forgot_password

import androidx.annotation.StringRes

/**
 * @author Orel Zilberman
 * 28/09/2022
 */
data class ForgotPassswordState(
    val isLoading: Boolean = false,
    val username: String = "",

    val event: ForgotPasswordEvent = ForgotPasswordEvent.Default,
    val errorFields: List<ForgotPasswordFields> = emptyList(),
    @StringRes val error: Int? = null
)

enum class ForgotPasswordFields {
    Username,
    Code,
    Password,
    ConfirmPassword
}

enum class ForgotPasswordEvent {
    Default,
    InsertUsername,
    InsertCodeAndPassword,
    PasswordResetSuccessfully
}