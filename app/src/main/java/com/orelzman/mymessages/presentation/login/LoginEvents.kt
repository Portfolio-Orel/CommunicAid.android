package com.orelzman.mymessages.presentation.login

sealed class LoginEvents {
    object UserRegisteredSuccessfully : LoginEvents()

    data class ConfirmSignup(val code: String): LoginEvents()

    data class OnLoginCompleted(
        val isAuthorized: Boolean,
        val exception: Exception?,
    ) : LoginEvents()
}