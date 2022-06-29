package com.orelzman.mymessages.presentation.details_message

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.data.local.interactors.folder.FolderInteractor
import com.orelzman.mymessages.data.local.interactors.message.MessageInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsMessageViewModel @Inject constructor(
    private val messageInteractor: MessageInteractor,
    private val folderInteractor: FolderInteractor,
    private val authInteractor: AuthInteractor
) : ViewModel() {
    var state by mutableStateOf(DetailsMessageState())

    init {
        viewModelScope.launch {
            authInteractor.user?.userId?.let {
                val folders = folderInteractor.getFolders(it)
                state = state.copy(folders = folders)
            }
        }
    }

    fun setEdit(messageId: String?) {
        if (messageId == null) {
            return
        }
        viewModelScope.launch {
            val message = messageInteractor.getMessage(messageId = messageId)
            val folder = folderInteractor.getFolderWithMessageId(messageId = messageId)
            setEditValues(message = message, folder = folder)
        }
    }

    private fun setEditValues(message: Message, folder: Folder) {
        state = state.copy(
            title = message.title,
            body = message.body,
            shortTitle = message.shortTitle,
            messageId = message.id,
            oldFolderId = folder.id
        )
    }

    fun setTitle(value: String) {
        state = state.copy(title = value)
    }

    fun setShortTitle(value: String) {
        if (value.length > 3) return // ToDo set error
        state = state.copy(shortTitle = value)
    }

    fun setBody(value: String) {
        state = state.copy(body = value)
    }

    fun setFolderId(value: String) {
        state = state.copy(currentFolderId = value)
    }

    fun saveMessage() {
        if (state.isReadyForSave) {
            state = state.copy(isLoading = true)
            val message = Message(
                title = state.title,
                shortTitle = state.shortTitle,
                body = state.body,
                id = state.messageId ?: ""
            )
            viewModelScope.launch {
                authInteractor.user?.userId?.let {
                    if (state.messageId != null && state.messageId != "") {
                        messageInteractor.editMessage(
                            userId = it,
                            message = message,
                            newFolderId = state.currentFolderId,
                            oldFolderId = state.oldFolderId
                        )
                    } else {
                        messageInteractor.createMessage(
                            userId = it,
                            message = message,
                            folderId = state.currentFolderId
                        )
                    }
                }
                state = state.copy(isLoading = false, isMessageSaved = true)
            }
        } else {
            val emptyFields = ArrayList<Fields>()
            if (state.title.isBlank()) emptyFields.add(Fields.Title)
            if (state.shortTitle.isBlank()) emptyFields.add(Fields.ShortTitle)
            if (state.body.isBlank()) emptyFields.add(Fields.Body)
            if (state.currentFolderId.isBlank()) emptyFields.add(Fields.Folder)
            state = state.copy(emptyFields = emptyFields)
        }
    }
}