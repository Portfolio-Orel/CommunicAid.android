package com.orelzman.mymessages.presentation.login_button

import com.orelzman.auth.domain.model.User

data class LoginButtonState(
    val isLoading: Boolean = false,
    val isAuthorized: Boolean = false,
    val isSignInAttempt: Boolean = false,
    val user: User? = null
)