package com.orels.presentation.ui.my_messages

import com.orels.auth.domain.interactor.UserState

data class MyMessagesState(
    val isLoading: Boolean = false,
    val authState: UserState = UserState.LoggedOut
)