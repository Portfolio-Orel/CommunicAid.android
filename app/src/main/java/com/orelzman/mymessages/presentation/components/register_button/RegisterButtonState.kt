package com.orelzman.mymessages.presentation.components.register_button

data class RegisterButtonState(
    var isLoading: Boolean = false,
    var isRegisterAttempt: Boolean = false,
    var isRegisterCompleted: Boolean = false
)