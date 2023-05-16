package com.orels.auth.domain

abstract class SignInStep {
    object ConfirmSignUp : SignInStep()
    object ConfirmSignInWithNewPassword : SignInStep()
    class Done(val user: User?) : SignInStep()
    class Error(val error: Exception) : SignInStep()
}