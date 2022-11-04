package com.orels.presentation.ui.components.register_button

data class RegisterButtonState(
    var isLoading: Boolean = false,
    var isRegisterAttempt: Boolean = false,
    var isRegisterCompleted: Boolean = false
)