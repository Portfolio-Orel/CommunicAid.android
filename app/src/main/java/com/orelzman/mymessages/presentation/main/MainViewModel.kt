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
import com.orelzman.mymessages.data.local.interactors.folder.FolderInteractor
import com.orelzman.mymessages.data.local.interactors.message.MessageInteractor
import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallStatisticsInteractor
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallInteractor
import com.orelzman.mymessages.util.Whatsapp.sendWhatsapp
import com.orelzman.mymessages.util.extension.Log
import com.orelzman.mymessages.util.extension.copyToClipboard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val folderInteractor: FolderInteractor,
    private val messageInteractor: MessageInteractor,
    private val authInteractor: AuthInteractor,
    private val phoneCallInteractor: PhoneCallInteractor,
    private val phoneCallStatisticsInteractor: PhoneCallStatisticsInteractor,
) : ViewModel() {

    var state by mutableStateOf(MainState(folders = emptyList(), messages = emptyList()))

    init {
        getMessages()
        getFolders()
        observeNumberOnTheLine()
    }

    fun onMessageClick(message: Message, context: Context) {
        Log.vCustom(message.toString())
        val phoneCall = phoneCallInteractor.numberOnTheLine.value
        if (phoneCall != null) {
            phoneCallStatisticsInteractor.addMessageSent(
                phoneCall,
                message.id
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
        val phoneCall = phoneCallInteractor.numberOnTheLine.value
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

    private fun getMessages() {
        CoroutineScope(Dispatchers.Main).launch {
            val messages = authInteractor.user?.let { messageInteractor.getMessagesWithFolders(it.userId) }
            if (messages != null) {
                state = state.copy(messages = messages)
            }
        }
    }

    private fun getFolders() {
        CoroutineScope(Dispatchers.Main).launch {
            val folders = authInteractor.user?.let { folderInteractor.getFolders(it.userId) }
            if (folders != null && folders.isNotEmpty()) {
                state = state.copy(folders = folders, selectedFolder = folders[0])
            }
        }
    }

    private fun observeNumberOnTheLine() {
        viewModelScope.launch {
            phoneCallInteractor.numberOnTheLine.collectLatest {
                state = state.copy(callOnTheLine = it?.number ?: "אין שיחה")
            }
        }
    }
}