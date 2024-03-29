package com.orels.auth.domain.model

abstract class SignInStep {
    object ConfirmSignUp : SignInStep()
    object ConfirmSignInWithNewPassword : SignInStep()
    class Done(val user: User?) : SignInStep()
    class Error(val error: Exception) : SignInStep()
}