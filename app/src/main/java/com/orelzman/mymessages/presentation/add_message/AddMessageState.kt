package com.orelzman.mymessages.presentation.add_message

import com.orelzman.mymessages.data.dto.Folder

data class AddMessageState(
    val folders: List<Folder> = emptyList(),
    val title: String = "",
    val shortTitle: String = "",
    val body: String = "",
    val folderId: String = "",
    val emptyFields: ArrayList<Fields> = ArrayList(),
    val isLoading: Boolean = false, // Message is being uploaded
    val error: String = "",
    val isMessageSaved: Boolean = false,
) {
    val isReadyForSave: Boolean =
        title.isNotBlank() && shortTitle.isNotBlank() && body.isNotBlank() && folderId.isNotBlank()
}

enum class Fields {
    Title,
    ShortTitle,
    Body,
    Folder
}