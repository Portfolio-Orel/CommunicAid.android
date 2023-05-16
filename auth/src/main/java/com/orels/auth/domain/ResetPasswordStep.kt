package com.orels.auth.domain

/**
 * @author Orel Zilberman
 * 29/09/2022
 */

abstract class ResetPasswordStep {
    object ConfirmSignUpWithNewPassword : ResetPasswordStep()
    class Done(val user: User?) : ResetPasswordStep()
    object Error : ResetPasswordStep()
}