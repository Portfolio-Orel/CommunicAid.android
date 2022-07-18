package com.orelzman.mymessages.presentation.components.confirmation_pop_up


data class ConfirmationState(
    val isLoading: Boolean = false,
    val isDismiss: Boolean = false,
    val code: String = "",
    val username: String = "",
    val exception: Exception? = null
)