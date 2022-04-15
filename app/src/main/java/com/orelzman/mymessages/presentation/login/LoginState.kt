package com.orelzman.mymessages.presentation.login

import com.orelzman.auth.domain.model.User

data class LoginState(
    var isLoadingLogin: Boolean = false,
    var user: User? = null,
)