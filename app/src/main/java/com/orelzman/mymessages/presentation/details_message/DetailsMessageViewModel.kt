package com.orelzman.mymessages.presentation.details_message

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.domain.interactors.FolderInteractor
import com.orelzman.mymessages.domain.interactors.MessageInteractor
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
        viewModelScope.launch(Dispatchers.Main) {
            authInteractor.getUser()?.userId?.let {
                val folders = folderInteractor.getFolders(it)
                state = state.copy(folders = folders)
            }
        }
    }

    private fun setEditValues(message: Message, folder: Folder) {
        state = state.copy(
            title = message.title,
            body = message.body,
            shortTitle = message.shortTitle,
            messageId = message.id,
            oldFolderId = folder.id,
            isEdit = true
        )
    }

    private fun clearValues() {
        state = state.copy(title = "", shortTitle = "", body = "", currentFolderId = "")
    }

    fun setEdit(messageId: String?) {
        messageId?.let {
            viewModelScope.launch(Dispatchers.Main) {
                val message = messageInteractor.getMessage(messageId = messageId)
                val folder = folderInteractor.getFolderWithMessageId(messageId = messageId)
                setEditValues(message = message, folder = folder)
            }
        }
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
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    authInteractor.getUser()?.userId?.let {
                        if (state.isEdit) {
                            messageInteractor.updateMessage(
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
                    state = state.copy(isLoading = false)
                    clearValues()
                } catch (exception: Exception) {
                    exception.log(state)
                }
            }
        } else {
            val emptyFields = ArrayList<MessageFields>()
            if (state.title.isBlank()) emptyFields.add(MessageFields.Title)
            if (state.shortTitle.isBlank()) emptyFields.add(MessageFields.ShortTitle)
            if (state.body.isBlank()) emptyFields.add(MessageFields.Body)
            if (state.currentFolderId.isBlank()) emptyFields.add(MessageFields.Folder)
            state = state.copy(emptyFields = emptyFields)
        }
    }
}