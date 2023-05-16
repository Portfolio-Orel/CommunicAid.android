package com.orels.presentation.ui.my_messages

import com.orels.domain.model.entities.UserState

data class MyMessagesState(
    val isLoading: Boolean = false,
    val authState: UserState = UserState.NotAuthorized
)