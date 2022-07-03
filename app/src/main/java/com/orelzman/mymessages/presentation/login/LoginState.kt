package com.orelzman.mymessages.presentation.login

data class LoginState(
    var isLoading: Boolean = false,
    var username: String = "",
    var password: String = "",
    var email: String = "",
    var isRegister: Boolean = false,
    var isCheckingAuth: Boolean = true,
    var showCodeConfirmation: Boolean = false,
    var isAuthorized: Boolean = false,
    var error: String? = null,
)