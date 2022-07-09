package com.orelzman.mymessages.presentation.main

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.domain.interactors.*
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.domain.model.entities.MessageSent
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManagerInteractor
import com.orelzman.mymessages.util.Whatsapp.sendWhatsapp
import com.orelzman.mymessages.util.extension.copyToClipboard
import com.orelzman.mymessages.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val folderInteractor: FolderInteractor,
    private val messageInteractor: MessageInteractor,
    private val authInteractor: AuthInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val phoneCallManagerInteractor: PhoneCallManagerInteractor,
    private val phoneCallsInteractor: PhoneCallsInteractor,
    private val messageInFolderInteractor: MessageInFolderInteractor,
) : ViewModel() {

    var state by mutableStateOf(MainState())

    fun init() {
        state = state.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            getMessages()
            getFolders()
            getMessagesInFolder()
            getUser()
            getAllSettings()
            observeNumberOnTheLine()
            observeNumberInBackground()
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

    private suspend fun getAllSettings() {
        try {
            authInteractor.getUser()?.let {
                settingsInteractor.getAllSettings(it.userId)
            }
        } catch (exception: HttpException) {
            println()
        }
    }

    fun getFoldersMessages(): List<Message> {
        val messageIds = state.messagesInFolders
            .filter { it.folderId == state.selectedFolder.id }
            .map { it.messageId }
        return state.messages.filter { messageIds.contains(it.id) }
    }

    fun onMessageClick(message: Message, context: Context) {
        val phoneCall =
            if (state.activeCall?.number == phoneCallManagerInteractor.callInBackground.value?.number) {
                phoneCallManagerInteractor.callInBackground.value
            } else {
                phoneCallManagerInteractor.numberOnTheLine.value
            }
        if (phoneCall != null) {
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    phoneCallsInteractor.addMessageSent(
                        phoneCall,
                        MessageSent(sentAt = Date().time, messageId = message.id)
                    )
                    context.sendWhatsapp(
                        phoneCall.number,
                        message.body
                    )
                }
            } catch (exception: Exception) {
                exception.log()
            }
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

    fun navigated() {
        state = state.copy(screenToShow = MainScreens.Default)
    }

    private fun selectActiveCall(phoneCall: PhoneCall?) {
        state = state.copy(activeCall = phoneCall)
    }

    private fun goToEditMessage(message: Message) {
        state = state.copy(messageToEdit = message, screenToShow = MainScreens.DetailsMessage)
    }

    private fun goToEditFolder(folder: Folder) {
        state = state.copy(folderToEdit = folder, screenToShow = MainScreens.DetailsFolder)
    }

    private fun observeNumberOnTheLine() {
        viewModelScope.launch(Dispatchers.Main) {
            phoneCallManagerInteractor.numberOnTheLine.collectLatest {
                state = state.copy(callOnTheLine = it)
                setCallOnTheLineActive()
            }
        }
    }

    private fun observeNumberInBackground() {
        viewModelScope.launch(Dispatchers.Main) {
            phoneCallManagerInteractor.callInBackground.collectLatest {
                state = state.copy(callInBackground = it)
            }
        }
    }
}