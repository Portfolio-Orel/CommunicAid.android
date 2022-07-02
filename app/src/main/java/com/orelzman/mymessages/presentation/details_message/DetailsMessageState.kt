package com.orelzman.mymessages.presentation.details_message

import com.orelzman.mymessages.data.dto.Folder

data class DetailsMessageState(
    val folders: List<Folder> = emptyList(),
    val title: String = "",
    val shortTitle: String = "",
    val body: String = "",
    val oldFolderId: String = "", // For Edit
    val currentFolderId: String = "",
    val emptyFields: ArrayList<Fields> = ArrayList(),
    val isLoading: Boolean = false, // Message is being uploaded
    val error: String = "",
    val isMessageSaved: Boolean = false,
    val messageId: String? = null,
    val isEdit: Boolean = false,
) {
    val isReadyForSave: Boolean =
        title.isNotBlank() && shortTitle.isNotBlank() && body.isNotBlank() && currentFolderId.isNotBlank()
}

enum class Fields {
    Title,
    ShortTitle,
    Body,
    Folder
}