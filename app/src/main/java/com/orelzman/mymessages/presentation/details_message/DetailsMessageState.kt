package com.orelzman.mymessages.presentation.details_message

import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Loggable

data class DetailsMessageState(
    val folders: List<Folder> = emptyList(),
    val title: String = "",
    val shortTitle: String = "",
    val body: String = "",
    val oldFolderId: String = "", // For Edit
    val currentFolderId: String = "",
    val emptyFields: ArrayList<MessageFields> = ArrayList(),
    val isLoading: Boolean = false, // Message is being uploaded
    val error: String = "",
    val eventMessage: EventsMessages? = null,
    val messageId: String? = null,
    val isEdit: Boolean = false,
) : Loggable {
    val isReadyForSave: Boolean =
        title.isNotBlank() && shortTitle.isNotBlank() && body.isNotBlank() && currentFolderId.isNotBlank()
    override val data: Map<String, Any>
        get() = mapOf(
            "folders" to (folders.map { it.data }),
            "title" to title,
            "short_title" to shortTitle,
            "body" to body,
            "older_folder_id" to currentFolderId,
            "empty_fields" to emptyFields.map { it.name },
            "is_loading" to isLoading,
            "error" to error,
            "message_id" to (messageId ?: ""),
            "is_edit" to isEdit,
        )
}

enum class MessageFields {
    Title,
    ShortTitle,
    Body,
    Folder
}

enum class EventsMessages {
    MessageSaved
}