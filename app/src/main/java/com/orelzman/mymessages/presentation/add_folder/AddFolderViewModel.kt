package com.orelzman.mymessages.presentation.add_folder

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.orelzman.mymessages.data.local.interactors.folder.FolderInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddFolderViewModel @Inject constructor(
    private val folderInteractor: FolderInteractor
): ViewModel() {
    private var state = mutableStateOf(AddFolderState())
}