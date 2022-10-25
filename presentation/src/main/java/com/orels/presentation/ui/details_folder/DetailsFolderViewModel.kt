package com.orels.presentation.ui.details_folder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.domain.interactors.FolderInteractor
import com.orels.domain.model.entities.Folder
import com.orels.domain.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsFolderViewModel @Inject constructor(
    private val folderInteractor: FolderInteractor
) : ViewModel() {
    var state by mutableStateOf(DetailsFolderState())

    fun setEdit(folderId: String?) {
        folderId?.let {
            val folder = folderInteractor.getFolder(folderId = folderId)
            folder?.let {
                state = state.copy(folder = it, isEdit = true, title = it.title)
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
                    state = state.copy(isLoadingDelete = true, eventFolder = null)
                    folderInteractor.deleteFolder(id = id)
                    state = state.copy(
                        isLoadingDelete = false,
                        eventFolder = EventsFolder.Deleted
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
                    state = state.copy(isLoadingDelete = true, eventFolder = null)
                    val folder = Folder(
                        title = title,
                        isActive = true,
                        timesUsed = timesUsed,
                        position = position,
                        id = id
                    )
                    folderInteractor.update(folder)
                    state = state.copy(eventFolder = EventsFolder.Restored)
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
            val saveJob = viewModelScope.async {
                if (state.isEdit) {
                    folderInteractor.update(folder = folder)
                } else {
                    folderInteractor.createFolder(
                        folder = Folder(title = state.title)
                    )
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                state = try {
                    saveJob.await()
                    state.copy(isLoading = false, eventFolder = EventsFolder.Saved)
                } catch (e: Exception) {
                    e.log(state)
                    state.copy(isLoading = false, eventFolder = EventsFolder.Error)
                }
            }
        } else {
            val emptyFields = ArrayList<FolderFields>()
            if (state.title.isBlank()) emptyFields.add(FolderFields.Title)
            state = state.copy(emptyFields = emptyFields)
        }
    }
}