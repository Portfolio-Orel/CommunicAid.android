package com.orelzman.mymessages.presentation.confirmation_screen


data class ConfirmationState(
    val isLoading: Boolean = false,
    val isDismiss: Boolean = false,
    val code: String = "",
    val username: String = "",
    val exception: Exception? = null
)