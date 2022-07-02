package com.orelzman.mymessages.presentation.main

import com.orelzman.auth.domain.model.User
import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.data.dto.MessageInFolder

data class MainState(
    val messages: List<Message> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val messagesInFolders: List<MessageInFolder> = emptyList(),
    val user: User? = null,
    val selectedFolder: Folder = Folder(),
    val callOnTheLine: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val maxMessagesInRow: Int = 6,
    val messageToEdit: Message? = null,
    val folderToEdit: Folder? = null
)