package com.orelzman.mymessages.presentation.add_folder

data class AddFolderState(
    val title: String = "",
    val isLoading: Boolean = false,
    val isFolderAdded: Boolean = false,
    val isReadyForSave: Boolean = title == "",
    val error: String = ""
)