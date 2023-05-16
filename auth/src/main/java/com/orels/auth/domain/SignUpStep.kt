package com.orels.auth.domain

abstract class SignUpStep(val userId: String?) {
    class ConfirmSignUpWithNewPassword(userId: String?) : SignUpStep(userId = userId)
    class ConfirmSignUpWithCode(userId: String?) : SignUpStep(userId = userId)
    class Done(val user: User?) : SignUpStep(userId = user?.userId)
    object Error : SignUpStep(userId = null)
}