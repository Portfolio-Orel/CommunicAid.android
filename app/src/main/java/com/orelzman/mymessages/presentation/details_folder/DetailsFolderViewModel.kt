package com.orelzman.mymessages.presentation.details_folder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.data.local.interactors.folder.FolderInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsFolderViewModel @Inject constructor(
    private val folderInteractor: FolderInteractor,
    private val authInteractor: AuthInteractor
) : ViewModel() {
    var state by mutableStateOf(DetailsFolderState())

    fun setTitle(value: String) {
        state = state.copy(title = value)
    }

    fun saveFolder() {
        if (state.isReadyForSave) {
            try {
                state = state.copy(isLoading = true)
                viewModelScope.launch {
                    authInteractor.getUser()?.userId?.let {
                        folderInteractor.addFolder(
                            userId = it,
                            folder = Folder(title = state.title)
                        )
                        state = state.copy(isLoading = false, isFolderAdded = true)
                    }
                }
            } catch (exception: Exception) {
                println()
            }
        }
    }
}