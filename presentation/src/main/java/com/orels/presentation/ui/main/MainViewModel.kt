package com.orels.presentation.ui.main

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.domain.interactors.AnalyticsIdentifiers
import com.orels.domain.interactors.AnalyticsInteractor
import com.orels.domain.interactors.FolderInteractor
import com.orels.domain.interactors.MessageInFolderInteractor
import com.orels.domain.interactors.MessageInteractor
import com.orels.domain.interactors.PhoneCallsInteractor
import com.orels.domain.interactors.WhatsappInteractor
import com.orels.domain.managers.phonecall.interactor.PhoneCallManagerInteractor
import com.orels.domain.model.entities.Folder
import com.orels.domain.model.entities.Message
import com.orels.domain.model.entities.MessageSent
import com.orels.domain.model.entities.PhoneCall
import com.orels.domain.model.entities.toPhoneCall
import com.orels.domain.util.common.Logger
import com.orels.domain.util.extension.copyToClipboard
import com.orels.domain.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val folderInteractor: FolderInteractor,
    private val messageInteractor: MessageInteractor,
    private val phoneCallManagerInteractor: PhoneCallManagerInteractor,
    private val phoneCallsInteractor: PhoneCallsInteractor,
    private val messageInFolderInteractor: MessageInFolderInteractor,
    private val whatsappInteractor: WhatsappInteractor,
    private val analyticsInteractor: AnalyticsInteractor,
) : ViewModel() {

    var state by mutableStateOf(MainState())
    private var callOnTheLineJob: Deferred<Unit>? = null
    private var callInTheBackgroundJob: Deferred<Unit>? = null

    private var fetchDataJob: Deferred<Unit> = viewModelScope.async {
        messageInteractor.initWithMessagesInFolders()
        folderInteractor.init()
    }

    /* States */
    init {
        state = state.copy(isLoading = true)
        analyticsInteractor.track(AnalyticsIdentifiers.ShowMainScreen)
        observeMessages()
        observeFolders()
        observeNumberOnTheLine()
        observeNumberInBackground()
        initData()
    }

    fun onResume() {
        initData()
    }

    /* States */

    fun navigated() {
        state = state.copy(screenToShow = MainScreens.Default)
    }

    fun onFoldersDropdownClick() {
        analyticsInteractor.track(AnalyticsIdentifiers.FoldersDropdownClick)
    }

    fun onFolderClick(folder: Folder) {
        analyticsInteractor.track(
            AnalyticsIdentifiers.SelectFolderClick,
            mapOf("title" to folder.title)
        )
        state = state.copy(
            selectedFolder = folder,
            selectedFoldersMessages = getFoldersMessages(folder.id)
        )
    }

    fun editFolder(folder: Folder) {
        analyticsInteractor.track(
            AnalyticsIdentifiers.EditFolderClick,
            mapOf("title" to folder.title)
        )
        goToEditFolder(folder)
    }

    fun onMessageClick(message: Message) {
        val phoneCall = state.activeCall
        if (phoneCall != null) {
            analyticsInteractor.track(
                AnalyticsIdentifiers.MessageClickOnCall,
                mapOf("title" to message.title)
            )
            phoneCallsInteractor.addMessageSent(
                phoneCall,
                MessageSent(sentAt = Date().time, messageId = message.id)
            )
            whatsappInteractor.sendMessage(
                number = phoneCall.number,
                message = message.body
            )
            val updateTimesUsedJob = viewModelScope.async {
                messageInteractor.increaseTimesUsed(message.id)
            }
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    updateTimesUsedJob.await()
                } catch (e: Exception) {
                    e.log()
                }
            }
        } else {
            analyticsInteractor.track(
                AnalyticsIdentifiers.MessageClickNotOnCall,
                mapOf("title" to message.title)
            )
            goToEditMessage(message = message)
        }
    }

    fun onMessageLongClick(message: Message, context: Context) {
        val phoneCall = state.activeCall
        context.copyToClipboard(label = message.title, value = message.body)
        if (phoneCall != null) {
            analyticsInteractor.track(
                AnalyticsIdentifiers.MessageLongClickOnCall,
                mapOf("title" to message.title)
            )
        } else {
            analyticsInteractor.track(
                AnalyticsIdentifiers.MessageLongNotOnCall,
                mapOf("title" to message.title)
            )

        }
    }

    private fun getFoldersMessages(folderId: String): List<Message> {
        val messageIds = messageInFolderInteractor.getMessagesInFoldersOnce()
            .filter { it.folderId == folderId }
            .map { it.messageId }

        return messageInteractor.getAllOnce(isActive = true)
            .filter { messageIds.contains(it.id) }
            .sortedByDescending { it.timesUsed }
    }

    private fun goToEditFolder(folder: Folder) {
        state = state.copy(folderToEdit = folder)
        navigateTo(MainScreens.DetailsFolder)
    }

    private fun goToEditMessage(message: Message) {
        state = state.copy(messageToEdit = message)
        navigateTo(MainScreens.DetailsMessage)
    }

    /**
     * Init messages and folders to avoid first long loading.
     */
    private fun initData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                fetchDataJob.await()
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    e.log()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    setState(newState = state.copy(isLoading = false))
                }
            }
        }
        state = state.copy(isLoading = true)
        val messages =
            messageInteractor.getAllOnce(isActive = true).sortedByDescending { it.timesUsed }
        val folders =
            folderInteractor.getAllOnce(isActive = true).sortedByDescending { it.timesUsed }
        val selectedFolder = if (
            state.selectedFolder == null || folders.none { it.id == state.selectedFolder?.id }
        ) folders.firstOrNull() else state.selectedFolder
        state = state.copy(
            isLoading = messages.isEmpty() && folders.isEmpty(),
            messages = messages,
            folders = folders,
            selectedFolder = selectedFolder,
            selectedFoldersMessages = getFoldersMessages(selectedFolder?.id ?: "")
        )
    }

    private fun setMessagesAndSelectedFolder() {
        val selectedFolder = if (
            state.selectedFolder == null || state.folders.none { folder -> folder.id == state.selectedFolder?.id }
        ) state.folders.maxByOrNull { it.timesUsed } else state.selectedFolder
        viewModelScope.launch {
            setState(
                newState = state.copy(
                    messages = state.messages.sortedByDescending { it.timesUsed },
                    folders = state.folders.sortedByDescending { it.timesUsed },
                    selectedFolder = selectedFolder,
                    selectedFoldersMessages = getFoldersMessages(selectedFolder?.id ?: "")
                )
            )
        }
    }

    private fun navigateTo(screen: MainScreens) {
        state = state.copy(screenToShow = screen)
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
                        setState(newState = state.copy(callInBackground = call))
                    }
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        e.log()
                        Logger.e("observeNumberInBackground stopped")
                    }
                }
            }
        viewModelScope.launch(SupervisorJob()) {
            try {
                callInTheBackgroundJob?.await()
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    e.log()
                }
            }
        }
    }

    private fun observeFolders() {
        viewModelScope.launch {
            folderInteractor.getFolders(isActive = true).collectLatest {
                val activeFolders = it.filter { folder -> folder.isActive }
                setState(newState = state.copy(folders = activeFolders))
                setMessagesAndSelectedFolder()
            }
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            messageInteractor.getMessages(isActive = true).collectLatest {
                val activeMessages = it.filter { message -> message.isActive }
                    .sortedByDescending { activeMessage -> activeMessage.timesUsed }
                setState(newState = state.copy(messages = activeMessages))
                setMessagesAndSelectedFolder()
            }
        }
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
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    e.log()
                }
            }
        }
    }

    private suspend fun setState(newState: MainState) {
        val setStateJob = viewModelScope.async {
            state = newState
        }
        withContext(Dispatchers.Main) {
            try {
                setStateJob.await()
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    e.log()
                }
            }
        }
    }

    private fun selectActiveCall(phoneCall: PhoneCall?) {
        state = state.copy(activeCall = phoneCall)
    }

    private fun setCallOnTheLineActive() {
        selectActiveCall(state.callOnTheLine)
    }
}