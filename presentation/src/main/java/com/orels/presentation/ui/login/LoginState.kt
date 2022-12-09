package com.orels.presentation.ui.login

import androidx.annotation.StringRes
import com.orels.auth.domain.model.ResetPasswordStep
import com.orels.auth.domain.model.SignInStep
import com.orels.auth.domain.model.SignUpStep

data class LoginState(
    val signInStep: SignInStep? = null,
    val signUpStep: SignUpStep? = null,
    val resetPasswordStep: ResetPasswordStep? = null,

    val username: String = "",
    val password: String = "",

    val isLoading: Boolean = false,
    @StringRes val error: Int? = null,
)