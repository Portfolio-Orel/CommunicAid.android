package com.orelzman.mymessages.presentation.add_message

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.data.local.interactors.folder.FolderInteractor
import com.orelzman.mymessages.data.local.interactors.message.MessageInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMessageViewModel @Inject constructor(
    private val messageInteractor: MessageInteractor,
    private val folderInteractor: FolderInteractor,
    private val authInteractor: AuthInteractor
): ViewModel() {
    var state by mutableStateOf(AddMessageState())

    init {
        viewModelScope.launch {
            authInteractor.user?.uid?.let {
                val folders = folderInteractor.getFolders(it)
                state = state.copy(folders = folders)
            }
        }
    }

    fun setTitle(value: String) {
        state = state.copy(title = value)
    }

    fun setShortTitle(value: String) {
        state = state.copy(shortTitle = value)
    }

    fun setBody(value: String) {
        state = state.copy(body = value)
    }
    fun setFolderId(value: String) {
        state = state.copy(folderId = value)
    }

    fun addMessage() {
        if(state.isReadyForSave) {
            state = state.copy(isLoading = true)
            viewModelScope.launch {
                authInteractor.user?.uid?.let {
                    messageInteractor.addMessage(
                        uid = it,
                        message = Message(
                            state.title,
                            state.shortTitle,
                            state.body,
                        ),
                        folderId = state.folderId
                    )
                    state = state.copy(isLoading = false, isMessageSaved = true)
                }
            }
        } else {
            val emptyFields = ArrayList<Fields>()
            if (state.title.isBlank()) emptyFields.add(Fields.Title)
            if (state.shortTitle.isBlank()) emptyFields.add(Fields.ShortTitle)
            if (state.body.isBlank()) emptyFields.add(Fields.Body)
            if (state.folderId.isBlank()) emptyFields.add(Fields.Folder)
            state = state.copy(emptyFields = emptyFields)
        }
    }
}