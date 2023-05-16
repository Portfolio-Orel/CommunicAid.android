package com.orels.domain.model

import com.orels.domain.model.entities.User

abstract class SignUpStep {
    object ConfirmSignUpWithNewPassword : SignUpStep()
    object ConfirmSignUpWithCode : SignUpStep()
    class Done(val user: User?) : SignUpStep()
    object Error : SignUpStep()
}