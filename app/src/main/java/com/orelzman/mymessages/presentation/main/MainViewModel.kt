package com.orelzman.mymessages.presentation.main

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.mymessages.domain.interactors.*
import com.orelzman.mymessages.domain.managers.phonecall.interactor.PhoneCallManagerInteractor
import com.orelzman.mymessages.domain.model.entities.*
import com.orelzman.mymessages.domain.util.extension.Logger
import com.orelzman.mymessages.domain.util.extension.copyToClipboard
import com.orelzman.mymessages.domain.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val folderInteractor: FolderInteractor,
    private val messageInteractor: MessageInteractor,
    private val phoneCallManagerInteractor: PhoneCallManagerInteractor,
    private val phoneCallsInteractor: PhoneCallsInteractor,
    private val messageInFolderInteractor: MessageInFolderInteractor,
    private val whatsappInteractor: WhatsappInteractor
) : ViewModel() {

    var state by mutableStateOf(MainState())
    private var callOnTheLineJob: Deferred<Unit>? = null
    private var callInTheBackgroundJob: Deferred<Unit>? = null

    fun init() {
        initData()
        observeNumberOnTheLine()
        observeNumberInBackground()
    }

    fun onResume() {
        init()
    }

    fun getFoldersMessages(): List<Message> {
        val messageIds = state.messagesInFolders
            .filter { it.folderId == state.selectedFolder?.id }
            .map { it.messageId }

        return state.messages
            .filter { messageIds.contains(it.id) }
            .sortedByDescending { it.timesUsed }
    }

    fun onMessageClick(message: Message) {
        val phoneCall = state.activeCall
        if (phoneCall != null) {
            val sendMessageJob = viewModelScope.async {
                phoneCallsInteractor.addMessageSent(
                    phoneCall,
                    MessageSent(sentAt = Date().time, messageId = message.id)
                )
                whatsappInteractor.sendMessage(
                    number = phoneCall.number,
                    message = message.body
                )
            }
            val updateTimesUsedJob = viewModelScope.async {
                messageInteractor.increaseTimesUsed(message.id)
            }
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    sendMessageJob.await()
                    updateTimesUsedJob.await()
                } catch (e: Exception) {
                    e.log()
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

    fun onFolderClick(folder: Folder) {
        state = state.copy(selectedFolder = folder)
    }

    fun onFolderLongClick(folder: Folder) = goToEditFolder(folder)

    fun navigated() {
        state = state.copy(screenToShow = MainScreens.Default)
    }

    /**
     * Init messages and folders to avoid first long loading.
     */
    private fun initData() {
        state = state.copy(isLoading = true)
        val messages = messageInteractor.getAllOnce().sortedByDescending { it.timesUsed }
        val folders = folderInteractor.getAllOnce().sortedByDescending { it.timesUsed }
        val messagesInFolders = messageInFolderInteractor.getMessagesInFoldersOnce()
        state = state.copy(
            isLoading = false,
            messages = messages,
            folders = folders,
            messagesInFolders = messagesInFolders,
            selectedFolder = folders.firstOrNull()
        )
    }

    private fun setCallOnTheLineActive() {
        selectActiveCall(state.callOnTheLine)
    }

    private fun navigateTo(screen: MainScreens) {
        state = state.copy(screenToShow = screen)
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
        val callOnTheLine = phoneCallManagerInteractor.callsData.callOnTheLine?.toPhoneCall()
        state = state.copy(callOnTheLine = callOnTheLine)
        callOnTheLineJob = viewModelScope.async {
            phoneCallManagerInteractor.callsDataFlow.collectLatest {
                val call = it.callOnTheLine?.toPhoneCall()
                state = state.copy(callOnTheLine = call)
                setCallOnTheLineActive()
            }
        }
        viewModelScope.launch(SupervisorJob()) {
            try {
                callOnTheLineJob?.await()
            } catch (e: CancellationException) {

            } catch (e: Exception) {
                e.log()
            }
        }
    }

    private fun observeNumberInBackground() {
        val callInTheBackground =
            phoneCallManagerInteractor.callsData.callInTheBackground?.toPhoneCall()
        state = state.copy(callOnTheLine = callInTheBackground)
        callInTheBackgroundJob = viewModelScope
            .async {
                try {
                    phoneCallManagerInteractor.callsDataFlow.collectLatest {
                        val call = it.callOnTheLine?.toPhoneCall()
                        state = state.copy(callInBackground = call)
                    }
                } catch (e: Exception) {
                    e.log()
                    Logger.e("observeNumberInBackground stopped")
                }
            }
        viewModelScope.launch(SupervisorJob()) {
            try {
                callInTheBackgroundJob?.await()
            } catch (e: CancellationException) {

            } catch (e: Exception) {
                e.log()
            }
        }
    }
}