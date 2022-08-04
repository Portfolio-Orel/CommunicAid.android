package com.orelzman.mymessages.presentation.details_folder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.domain.interactors.FolderInteractor
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsFolderViewModel @Inject constructor(
    private val folderInteractor: FolderInteractor,
    private val authInteractor: AuthInteractor
) : ViewModel() {
    var state by mutableStateOf(DetailsFolderState())

    fun setEdit(folderId: String?) {
        folderId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val folder = folderInteractor.getFolder(folderId = folderId)
                state = state.copy(folder = folder, isEdit = true, title = folder.title)
            }
        }
    }

    fun setTitle(value: String) {
        state = state.copy(title = value)
    }

    fun deleteFolder() {
        state.folder?.let {
            with(it) {
                val deleteFolderJob = viewModelScope.async {
                    state = state.copy(isLoadingDelete = true)
                    val folder = Folder(
                        title = title,
                        isActive = false,
                        timesUsed = timesUsed,
                        position = position,
                        id = id
                    )
                    folderInteractor.deleteFolder(folder)
                    state = state.copy(
                        isLoadingDelete = false,
                        eventFolder = EventsFolder.FolderDeleted
                    )
                }
                viewModelScope.launch(Dispatchers.Main) {
                    try {
                        deleteFolderJob.await()
                    } catch (e: Exception) {
                        e.log()
                        state = state.copy(
                            isLoadingDelete = false,
                            eventFolder = EventsFolder.Error
                        )
                    }
                }
            }
        }
    }

    fun undoDelete() {
        state.folder?.let {
            with(it) {
                val undoDeleteJob = viewModelScope.async {
                    state = state.copy(isLoadingDelete = true)
                    val folder = Folder(
                        title = title,
                        isActive = true,
                        timesUsed = timesUsed,
                        position = position,
                        id = id
                    )
                    folderInteractor.updateFolder(folder)
                    state = state.copy(eventFolder = EventsFolder.FolderRestored)
                }
                viewModelScope.launch(Dispatchers.Main) {
                    try {
                        undoDeleteJob.await()
                    } catch (e: Exception) {
                        e.log()
                    }
                }
            }
        }
    }

    fun onSaveClick() {
        if (state.isLoading) return
        if (state.isEdit) {
            state.folder?.let {
                with(it) {
                    val folder = Folder(
                        title = state.title,
                        isActive = isActive,
                        timesUsed = timesUsed,
                        position = position,
                        id = id
                    )
                    saveFolder(folder = folder)
                }
            }
        } else {
            saveFolder()
        }
    }

    private fun saveFolder(folder: Folder = Folder()) {
        if (state.isReadyForSave) {
            state = state.copy(isLoading = true)
            try {
                state = state.copy(isLoading = true)
                viewModelScope.launch(Dispatchers.IO) {
                    if (state.isEdit) {
                        folderInteractor.updateFolder(folder = folder)
                    } else {
                        folderInteractor.createFolder(
                            folder = Folder(title = state.title)
                        )
                    }
                }.invokeOnCompletion {
                    if (it != null) {
                        state = state.copy(
                            isLoading = false,
                            eventFolder = if (state.isEdit) EventsFolder.FolderUpdated else EventsFolder.FolderSaved
                        )
                    } else {
                        it?.log()
                    }
                }
            } catch (e: Exception) {
                e.log(state)
                state = state.copy(isLoading = false, eventFolder = EventsFolder.Error)
            }
        } else {
            val emptyFields = ArrayList<FolderFields>()
            if (state.title.isBlank()) emptyFields.add(FolderFields.Title)
            state = state.copy(emptyFields = emptyFields)
        }
    }
}