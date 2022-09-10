package com.orelzman.mymessages.presentation.main

import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.domain.model.entities.PhoneCall

data class MainState(
    val messages: List<Message> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val selectedFolder: Folder? = null,
    val selectedFoldersMessages: List<Message> = emptyList(),

    val activeCall: PhoneCall? = null,
    val callOnTheLine: PhoneCall? = null,
    val callInBackground: PhoneCall? = null,

    val isLoading: Boolean = true,
    val messageToEdit: Message? = null,
    val folderToEdit: Folder? = null,

    val screenToShow: MainScreens = MainScreens.Default
)

enum class MainScreens {
    DetailsMessage,
    DetailsFolder,
    Default;
}