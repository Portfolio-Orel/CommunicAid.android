package com.orelzman.mymessages.presentation.main

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.mymessages.domain.interactors.FolderInteractor
import com.orelzman.mymessages.domain.interactors.MessageInFolderInteractor
import com.orelzman.mymessages.domain.interactors.MessageInteractor
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.model.entities.*
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManagerInteractor
import com.orelzman.mymessages.util.Whatsapp.sendWhatsapp
import com.orelzman.mymessages.util.extension.copyToClipboard
import com.orelzman.mymessages.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val folderInteractor: FolderInteractor,
    private val messageInteractor: MessageInteractor,
    private val phoneCallManagerInteractor: PhoneCallManagerInteractor,
    private val phoneCallsInteractor: PhoneCallsInteractor,
    private val messageInFolderInteractor: MessageInFolderInteractor,
) : ViewModel() {

    var state by mutableStateOf(MainState())

    fun init() {
        state = state.copy(isLoading = true)
        getMessages()
        getFolders()
        getMessagesInFolder()
        observeNumberOnTheLine()
        observeNumberInBackground()
        state = state.copy(isLoading = false)
    }

    private fun getMessagesInFolder() {
        viewModelScope.launch(Dispatchers.Main) {
            messageInFolderInteractor.getMessagesInFolders().collectLatest {
                state = state.copy(messagesInFolders = it)
            }
        }
    }

    private fun getMessages() {
        viewModelScope.launch(Dispatchers.Main) {
            messageInteractor.getMessages().collectLatest {
                state = state.copy(messages = it)
            }
        }
    }

    private fun getFolders() {
        viewModelScope.launch(Dispatchers.Main) {
            folderInteractor.getFolders().collectLatest {
                if (state.selectedFolder == null && it.isNotEmpty()) {
                    state = state.copy(folders = it, selectedFolder = it.first())
                }
            }
        }
    }

    fun getFoldersMessages(): List<Message> {
        val messageIds = state.messagesInFolders
            .filter { it.folderId == state.selectedFolder?.id }
            .map { it.messageId }
        return state.messages.filter { messageIds.contains(it.id) }
    }

    fun onMessageClick(message: Message, context: Context) {
        val phoneCall = state.activeCall
        if (phoneCall != null) {
                val sendMessageJob = viewModelScope.async {
                    phoneCallsInteractor.addMessageSent(
                        phoneCall,
                        MessageSent(sentAt = Date().time, messageId = message.id)
                    )
                    context.sendWhatsapp(
                        phoneCall.number,
                        message.body
                    )
                }
            val updateTimesUsedJob = viewModelScope.async {
                messageInteractor.increaseTimesUsed(message.id)
            }
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    sendMessageJob.await()
                    updateTimesUsedJob.await()
                } catch (exception: Exception) {
                    exception.log()
                }
            }
        } else {
            goToEditMessage(message = message)
        }
    }

    fun onMessageLongClick(message: Message, context: Context) {
        val phoneCall = state.activeCall
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

    fun setBackgroundCallActive() {
        if (state.callInBackground != null) {
            selectActiveCall(state.callInBackground)
        } else {
            selectActiveCall(state.callOnTheLine)
        }
    }

    fun setCallOnTheLineActive() {
        selectActiveCall(state.callOnTheLine)
    }

    private fun navigateTo(screen: MainScreens) {
        state = state.copy(screenToShow = screen)
    }

    fun navigated() {
        state = state.copy(screenToShow = MainScreens.Default)
    }

    private fun selectActiveCall(phoneCall: PhoneCall?) {
        state = state.copy(activeCall = phoneCall)
    }

    private fun goToEditMessage(message: Message) {
        state = state.copy(messageToEdit = message)
        navigateTo(MainScreens.DetailsMessage)
    }

    private fun goToEditFolder(folder: Folder) {
        state = state.copy(folderToEdit = folder)
        navigateTo(MainScreens.DetailsFolder)
    }

    private fun observeNumberOnTheLine() {
        viewModelScope.launch(Dispatchers.Main) {
            phoneCallManagerInteractor.callsDataFlow.collectLatest {
                state = state.copy(callOnTheLine = it.callOnTheLine?.toPhoneCall())
                setCallOnTheLineActive()
            }
        }
    }

    private fun observeNumberInBackground() {
        viewModelScope.launch(Dispatchers.Main) {
            phoneCallManagerInteractor.callsDataFlow.collectLatest {
                state = state.copy(callInBackground = it.callInTheBackground?.toPhoneCall())
            }
        }
    }
}