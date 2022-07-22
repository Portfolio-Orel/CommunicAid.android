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
                val folder = Folder(
                    title = title,
                    isActive = false,
                    timesUsed = timesUsed,
                    position = position,
                    id = id
                )
                saveFolder(folder = folder)
            }
        }
    }

    fun onSaveClick() {
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
                    authInteractor.getUser()?.userId?.let {
                        if (state.isEdit) {
                            folderInteractor.updateFolder(folder = folder)
                        } else {
                            folderInteractor.createFolder(
                                userId = it,
                                folder = Folder(title = state.title)
                            )
                        }
                    }
                }.invokeOnCompletion {
                    state = state.copy(isLoading = false, isFolderAdded = it == null)
                    it?.log()
                }
            } catch (exception: Exception) {
                exception.log(state)
                state = state.copy(isLoading = false, isFolderAdded = false)
            }
        } else {
            val emptyFields = ArrayList<FolderFields>()
            if (state.title.isBlank()) emptyFields.add(FolderFields.Title)
            state = state.copy(emptyFields = emptyFields)
        }
    }
}