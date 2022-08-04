package com.orelzman.mymessages.presentation.details_folder

import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Loggable

data class DetailsFolderState(
    val title: String = "",
    val isLoading: Boolean = false,
    val isLoadingDelete: Boolean = false,

    val eventFolder: EventsFolder? = null,

    val folder: Folder? = null,
    val isEdit: Boolean = false,
    val emptyFields: List<FolderFields> = emptyList(),
    val error: String = ""
) : Loggable {
    val isReadyForSave: Boolean = title.isNotBlank()

    override val data: Map<String, Any>
        get() = mapOf(
            "title" to title,
            "is_loading" to isLoading,
            "event_folder" to (eventFolder?.name ?: "event_folder"),
            "folder" to (folder?.data ?: emptyMap()),
            "is_edit" to isEdit,
            "is_ready_for_save" to isReadyForSave,
            "error" to error,
        )
}

enum class FolderFields {
    Title
}

enum class EventsFolder {
        FolderSaved,
        FolderUpdated,
        FolderDeleted,
        FolderRestored,
        Error;
}