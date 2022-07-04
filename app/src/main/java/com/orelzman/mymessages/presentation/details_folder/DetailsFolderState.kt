package com.orelzman.mymessages.presentation.details_folder

import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.data.dto.Loggable

data class DetailsFolderState(
    val title: String = "",
    val isLoading: Boolean = false,
    val isFolderAdded: Boolean = false,
    val folder: Folder? = null,
    val isEdit: Boolean = false,
    val isReadyForSave: Boolean = title == "",
    val error: String = ""
) : Loggable {
    override val data: Map<String, Any>
        get() = mapOf(
            "title" to title,
            "is_loading" to isLoading,
            "is_folder_added" to isFolderAdded,
            "folder" to (folder?.data ?: emptyMap()),
            "is_edit" to isEdit,
            "is_ready_for_save" to isReadyForSave,
            "error" to error,
        )

}