package com.orels.auth.domain.model

abstract class SignUpStep {
    object ConfirmSignUpWithNewPassword : SignUpStep()
    class Done(val user: User?) : SignUpStep()
    object Error : SignUpStep()
}