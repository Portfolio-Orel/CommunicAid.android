package com.orelzman.mymessages.presentation.login

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

sealed class LoginEvents {
    data class AuthWithEmailAndPassowrd(
        val email: String = "1@2.com",
        val password: String = "o123456"
    ) : LoginEvents()

    data class AuthWithGmail constructor(val signInAccount: GoogleSignInAccount) : LoginEvents()
}