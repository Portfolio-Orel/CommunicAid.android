package com.orelzman.mymessages.presentation.login

import com.orelzman.auth.domain.model.User

data class LoginState(
    var user: User? = null,
)