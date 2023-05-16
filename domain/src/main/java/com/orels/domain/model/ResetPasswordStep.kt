package com.orels.domain.model

import com.orels.domain.model.entities.User

/**
 * @author Orel Zilberman
 * 29/09/2022
 */

abstract class ResetPasswordStep {
    object ConfirmSignUpWithNewPassword : ResetPasswordStep()
    class Done(val user: User?) : ResetPasswordStep()
    object Error : ResetPasswordStep()
}