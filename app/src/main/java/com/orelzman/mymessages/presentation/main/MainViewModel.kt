package com.orelzman.mymessages.presentation.main

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.data.dto.MessageSent
import com.orelzman.mymessages.data.local.interactors.folder.FolderInteractor
import com.orelzman.mymessages.data.local.interactors.message.MessageInteractor
import com.orelzman.mymessages.data.local.interactors.message_in_folder.MessageInFolderInteractor
import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallsInteractor
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManagerInteractor
import com.orelzman.mymessages.util.Whatsapp.sendWhatsapp
import com.orelzman.mymessages.util.extension.Log
import com.orelzman.mymessages.util.extension.copyToClipboard
import com.orelzman.mymessages.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val folderInteractor: FolderInteractor,
    private val messageInteractor: MessageInteractor,
    private val authInteractor: AuthInteractor,
    private val phoneCallManagerInteractor: PhoneCallManagerInteractor,
    private val phoneCallStatisticsInteractor: PhoneCallsInteractor,
    private val messageInFolderInteractor: MessageInFolderInteractor
) : ViewModel() {

    var state by mutableStateOf(MainState())

    fun init() {
        state = state.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            getMessages()
            getFolders()
            getMessagesInFolder()
            getUser()
            observeNumberOnTheLine()
        }.invokeOnCompletion {
            it?.log()
            state = state.copy(isLoading = false)
        }
    }

    private suspend fun getUser() {
        val user = authInteractor.getUser()
        state = state.copy(user = user)
    }

    private suspend fun getMessagesInFolder() {
        val messagesInFolders = messageInFolderInteractor.getMessagesInFolders()
        state = state.copy(messagesInFolders = messagesInFolders)
    }

    private suspend fun getMessages() {
        val messages = authInteractor.getUser()
            ?.let { messageInteractor.getMessagesWithFolders(it.userId) }
        if (messages != null) {
            state = state.copy(messages = messages)
        }
    }

    private suspend fun getFolders() {
        val folders =
            authInteractor.getUser()?.let { folderInteractor.getFolders(it.userId) }
        if (folders != null && folders.isNotEmpty()) {
            state = state.copy(folders = folders, selectedFolder = folders[0])
        }
    }

    fun getFoldersMessages(): List<Message> {
        val messageIds = state.messagesInFolders
            .filter { it.folderId == state.selectedFolder.id }
            .map { it.messageId }
        return state.messages.filter { messageIds.contains(it.id) }
    }

    fun onMessageClick(message: Message, context: Context) {
        Log.vCustom(message.toString())
        val phoneCall = phoneCallManagerInteractor.numberOnTheLine.value
        if (phoneCall != null) {
            phoneCallStatisticsInteractor.addMessageSent(
                phoneCall,
                MessageSent(sentAt = Date().time, messageId = message.id)
            )
            context.sendWhatsapp(
                phoneCall.number,
                message.body
            )
        } else {
            goToEditMessage(message = message)
        }
    }

    fun onMessageLongClick(message: Message, context: Context) {
        val phoneCall = phoneCallManagerInteractor.numberOnTheLine.value
        if (phoneCall != null) {
            goToEditMessage(message)
        } else {
            context.copyToClipboard(label = message.title, value = message.body)
        }
    }

    fun setSelectedFolder(folder: Folder) {
        state = state.copy(selectedFolder = folder)
    }

    fun onFolderLongClick(folder: Folder) = goToEditFolder(folder)

    private fun goToEditMessage(message: Message) {
        state = state.copy(messageToEdit = message)
    }

    private fun goToEditFolder(folder: Folder) {
        state = state.copy(folderToEdit = folder)
    }

    private fun observeNumberOnTheLine() {
        viewModelScope.launch(Dispatchers.IO) {
            phoneCallManagerInteractor.numberOnTheLine.collectLatest {
                state = state.copy(callOnTheLine = it?.number ?: "אין שיחה")
            }
        }
    }
}