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
import kotlinx.coroutines.flow.collectLatest
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
                folderInteractor.getFolders().collectLatest {
                    state = state.copy(folders = it)
                }
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
            selectedFolder = folder,
            isEdit = true
        )
    }

    private fun clearValues() {
        state = state.copy(title = "", shortTitle = "", body = "", selectedFolder = null)
    }

    fun setEdit(messageId: String?) {
        messageId?.let { id ->
            viewModelScope.launch(Dispatchers.Main) {
                val folder = folderInteractor.getFolderWithMessageId(messageId = messageId)
                messageInteractor.getMessage(messageId = id)?.let { message ->
                    setEditValues(message = message, folder = folder)
                }
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

    fun setSelectedFolder(value: Folder) {
        state = state.copy(selectedFolder = value)
    }

    fun saveMessage() {
        if (state.isLoading) return
        if (state.isReadyForSave) {
            state = state.copy(isLoading = true, eventMessage = null)
            val message = Message(
                title = state.title,
                shortTitle = state.shortTitle,
                body = state.body,
                id = state.messageId ?: ""
            )
            viewModelScope.launch(Dispatchers.Main) {
                try {
                    authInteractor.getUser()?.userId?.let {
                        if (state.isEdit) {
                            messageInteractor.updateMessage(
                                message = message,
                                newFolderId = state.selectedFolder?.id,
                                oldFolderId = state.oldFolderId
                            )
                        } else {
                            state.selectedFolder?.id?.let { folderId ->
                                messageInteractor.createMessage(
                                    userId = it,
                                    message = message,
                                    folderId = folderId
                                )
                            }
                        }
                    }
                    state =
                        state.copy(isLoading = false, eventMessage = EventsMessages.MessageSaved)
                    clearValues()
                } catch (exception: Exception) {
                    exception.log(state)
                    state =
                        state.copy(isLoading = false, eventMessage = EventsMessages.MessageSaved)
                }
            }
        } else {
            val emptyFields = ArrayList<MessageFields>()
            if (state.title.isBlank()) emptyFields.add(MessageFields.Title)
            if (state.shortTitle.isBlank()) emptyFields.add(MessageFields.ShortTitle)
            if (state.body.isBlank()) emptyFields.add(MessageFields.Body)
            if (state.selectedFolder == null) emptyFields.add(MessageFields.Folder)
            state = state.copy(emptyFields = emptyFields)
        }
    }
}