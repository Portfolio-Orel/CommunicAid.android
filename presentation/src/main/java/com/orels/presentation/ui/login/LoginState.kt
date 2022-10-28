package com.orels.presentation.ui.login

import androidx.annotation.StringRes

data class LoginState(
    var isLoading: Boolean = true,
    var username: String = "",
    var password: String = "",
    var email: String = "",
    var isRegister: Boolean = false,
    var showCodeConfirmation: Boolean = false,
    var isAuthorized: Boolean = false,
    @StringRes var error: Int? = null,
)