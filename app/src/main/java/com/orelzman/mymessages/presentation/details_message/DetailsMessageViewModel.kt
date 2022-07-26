package com.orelzman.mymessages.presentation.details_message

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.domain.interactors.FolderInteractor
import com.orelzman.mymessages.domain.interactors.MessageInFolderInteractor
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
    private val messageInFolderInteractor: MessageInFolderInteractor,
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
                if (folder == null) {
                    state = state.copy(isEdit = false)
                    return@launch
                }
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

    fun deleteMessage() {
        if (state.isLoadingDelete) return
        if (state.isEdit && state.messageId != null) {
            state = state.copy(isLoadingDelete = true, eventMessage = null)
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val folderId = messageInFolderInteractor.getMessageFolderId(state.messageId!!)
                    val message = buildMessage()
                    messageInteractor.deleteMessage(message = message)
                    state = state.copy(
                        isLoadingDelete = false,
                        eventMessage = EventsMessages.MessageDeleted,
                        messageDeleted = message,
                        messageDeletedFolderId = folderId
                    )
                } catch (e: Exception) {
                    e.log()
                }
            }
        }
    }

    fun undoDelete() {
        state.messageDeleted?.let {
            if (state.messageDeletedFolderId == null) return@let
            viewModelScope.launch(Dispatchers.IO) {
                state = try {
                    messageInteractor.createMessage(
                        message = it, folderId = state.messageDeletedFolderId!!
                    )
                    state.copy(
                        isLoading = false,
                        messageId = it.id,
                        eventMessage = EventsMessages.MessageRestored
                    )
                } catch (e: Exception) {
                    e.log(state)
                    state.copy(isLoading = false, eventMessage = EventsMessages.MessageRestored)
                }
            }
        }
    }

    fun saveMessage() {
        if (state.isLoading) return
        if (state.isReadyForSave) {
            state = state.copy(isLoading = true, eventMessage = null)
            val message = buildMessage()
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    if (state.isEdit) {
                        messageInteractor.updateMessage(
                            message = message,
                            newFolderId = state.selectedFolder?.id,
                            oldFolderId = state.oldFolderId
                        )
                        state =
                            state.copy(
                                isLoading = false,
                                eventMessage = EventsMessages.MessageUpdated
                            )
                    } else {
                        state.selectedFolder?.id?.let { folderId ->
                            messageInteractor.createMessage(
                                message = message,
                                folderId = folderId
                            )
                            state =
                                state.copy(
                                    isLoading = false,
                                    eventMessage = EventsMessages.MessageSaved
                                )
                        }
                    }
                    clearValues()
                } catch (e: Exception) {
                    e.log(state)
                    state =
                        state.copy(
                            isLoading = false,
                            eventMessage = EventsMessages.MessageSaved
                        )
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

    private fun buildMessage(): Message =
        Message(
            title = state.title,
            shortTitle = state.shortTitle,
            body = state.body,
            id = state.messageId ?: ""
        )
}