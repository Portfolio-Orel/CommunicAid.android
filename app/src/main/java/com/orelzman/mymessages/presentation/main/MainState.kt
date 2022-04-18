package com.orelzman.mymessages.presentation.main

import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.data.dto.Message

data class MainState(
    val messages: List<Message>,
    val folders: List<Folder>,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
)