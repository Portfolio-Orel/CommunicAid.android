package com.orelzman.mymessages.presentation.login

import com.orelzman.auth.domain.model.User

data class LoginState(
    var isLoading: Boolean = false,
    var username: String = "",
    var password: String = "",
    var email: String = "",
    var isRegister: Boolean = false,
    var showCodeConfirmation: Boolean = false,
    var user: User? = null,
    var error: String? = null,
)