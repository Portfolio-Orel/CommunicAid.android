package com.orelzman.mymessages.presentation.details_folder

data class DetailsFolderState(
    val title: String = "",
    val isLoading: Boolean = false,
    val isFolderAdded: Boolean = false,
    val isReadyForSave: Boolean = title == "",
    val error: String = ""
)