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
            observeNumberOnTheLine()
        }.invokeOnCompletion {
            if (it != null) {
                // Error
            }
            state = state.copy(isLoading = false)
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

    private fun getMessagesInFolder() {
        viewModelScope.launch(Dispatchers.IO) {
            val messagesInFolders = messageInFolderInteractor.getMessagesInFolders()
            state = state.copy(messagesInFolders = messagesInFolders)
        }
    }

    private fun getMessages() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val messages = authInteractor.getUser()
                    ?.let { messageInteractor.getMessagesWithFolders(it.userId) }
                if (messages != null) {
                    state = state.copy(messages = messages)
                }
            } catch (ex: Exception) {
                println(ex)
            }
        }
    }

    private fun getFolders() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val folders =
                    authInteractor.getUser()?.let { folderInteractor.getFolders(it.userId) }
                if (folders != null && folders.isNotEmpty()) {
                    state = state.copy(folders = folders, selectedFolder = folders[0])
                }
            } catch (ex: Exception) {
                println(ex)
            }
        }
    }


    private fun observeNumberOnTheLine() {
        viewModelScope.launch {
            phoneCallManagerInteractor.numberOnTheLine.collectLatest {
                state = state.copy(callOnTheLine = it?.number ?: "אין שיחה")
            }
        }
    }
}