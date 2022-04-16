package com.orelzman.mymessages.presentation.login

sealed class LoginEvents {
    data class AuthWithEmailAndPassowrd(
        val email: String = "1@2.com",
        val password: String = "o123456"
    ) : LoginEvents()
}