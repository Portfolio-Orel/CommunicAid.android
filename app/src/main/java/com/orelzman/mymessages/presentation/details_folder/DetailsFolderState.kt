package com.orelzman.mymessages.presentation.details_folder

import com.orelzman.mymessages.data.dto.Folder

data class DetailsFolderState(
    val title: String = "",
    val isLoading: Boolean = false,
    val isFolderAdded: Boolean = false,
    val folder: Folder? = null,
    val isEdit: Boolean = false,
    val isReadyForSave: Boolean = title == "",
    val error: String = ""
)