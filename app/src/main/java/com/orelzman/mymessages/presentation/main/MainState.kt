package com.orelzman.mymessages.presentation.main

import com.orelzman.auth.domain.model.User
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.domain.model.entities.MessageInFolder
import com.orelzman.mymessages.domain.model.entities.PhoneCall

data class MainState(
    val messages: List<Message> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val messagesInFolders: List<MessageInFolder> = emptyList(),
    val user: User? = null,
    val selectedFolder: Folder = Folder(),

    val activeCall: PhoneCall? = null,
    val callOnTheLine: PhoneCall? = null,
    val callInBackground: PhoneCall? = null,

    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val maxMessagesInRow: Int = 6,
    val messageToEdit: Message? = null,
    val folderToEdit: Folder? = null,
    val screenToShow: MainScreens = MainScreens.Default
)

enum class MainScreens {
    DetailsMessage,
    DetailsFolder,
    Default;
}