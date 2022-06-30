package com.orelzman.mymessages.presentation.login

import com.orelzman.auth.domain.model.User

sealed class LoginEvents {
    object UserRegisteredSuccessfully : LoginEvents()

    data class ConfirmSignup(val code: String): LoginEvents()

    data class UserLoggedInSuccessfully(
        val user: User?
    ) : LoginEvents()
}