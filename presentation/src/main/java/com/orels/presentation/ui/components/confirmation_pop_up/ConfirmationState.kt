package com.orels.presentation.ui.components.confirmation_pop_up

import androidx.annotation.StringRes


data class ConfirmationState(
    val isLoading: Boolean = false,
    val isDismiss: Boolean = false,
    val code: String = "",
    val username: String = "",
    @StringRes val error: Int? = null
)