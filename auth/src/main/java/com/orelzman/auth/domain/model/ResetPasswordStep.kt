package com.orelzman.auth.domain.model

/**
 * @author Orel Zilberman
 * 29/09/2022
 */

enum class ResetPasswordStep {
    ConfirmResetPasswordWithCode,
    Done,
    Error
}