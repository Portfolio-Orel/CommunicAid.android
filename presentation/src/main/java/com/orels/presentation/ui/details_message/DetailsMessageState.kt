package com.orels.presentation.ui.details_message

import androidx.annotation.StringRes
import com.orels.domain.model.entities.Folder
import com.orels.domain.model.entities.Loggable
import com.orels.domain.model.entities.Message
import com.orels.presentation.R

data class DetailsMessageState(
    val folders: List<Folder> = emptyList(),
    val title: String = "",
    val shortTitle: String = "",
    val body: String = "",
    val oldFolderId: String = "", // For Edit
    val selectedFolder: Folder? = null,
    val emptyFields: ArrayList<MessageFields> = ArrayList(),
    val isLoading: Boolean = false, // Message is being uploaded

    val isLoadingDelete: Boolean = false,
    val messageDeleted: Message? = null,
    val messageDeletedFolderId: String? = null,

    @StringRes val error: Int = R.string.empty_string,
    val eventMessage: EventsMessages? = null,
    val messageId: String? = null,
    val isEdit: Boolean = false,
) : Loggable {
    val isReadyForSave: Boolean =
        title.isNotBlank()
                && shortTitle.isNotBlank()
                && body.isNotBlank()
                && selectedFolder != null
                && selectedFolder.id.isNotBlank()
    override val data: Map<String, Any>
        get() = mapOf(
            "folders" to (folders.map { it.data }),
            "title" to title,
            "short_title" to shortTitle,
            "body" to body,
            "older_folder_id" to (selectedFolder?.id ?: ""),
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
    Saved,
    Updated,
    Deleted,
    Restored,
    Error;
}