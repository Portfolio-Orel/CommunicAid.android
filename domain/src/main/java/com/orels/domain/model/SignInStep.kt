package com.orels.domain.model

import com.orels.domain.model.entities.User

abstract class SignInStep {
    object ConfirmSignUp : SignInStep()
    object ConfirmSignInWithNewPassword : SignInStep()
    class Done(val user: User?) : SignInStep()
    class Error(val error: Exception) : SignInStep()
}