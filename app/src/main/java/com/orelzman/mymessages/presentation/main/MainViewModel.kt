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
import com.orelzman.mymessages.domain.service.PhoneCall.PhoneCallInteractor
import com.orelzman.mymessages.util.Whatsapp.sendWhatsapp
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun sendMessage(message: Message, context: Context) {
        val phoneCall = phoneCallInteractor.numberOnTheLine.value ?: return
        phoneCallStatisticsInteractor.addMessageSent(
            phoneCall,
            message.id
        )
        context.sendWhatsapp(
            phoneCall.number,
            message.messageBody
        )
    }

    fun setSelectedFolder(folder: Folder) {
        state = state.copy(selectedFolder = folder)
    }

    private fun getMessages() {
        viewModelScope.launch {
            val messages = authInteractor.user?.let { messageInteractor.getMessages(it.uid) }
            if (messages != null) {
                state = state.copy(messages = messages)
            }
        }
    }

    private fun getFolders() {
        viewModelScope.launch {
            val folders = authInteractor.user?.let { folderInteractor.getFolders(it.uid) }
            if (folders != null) {
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