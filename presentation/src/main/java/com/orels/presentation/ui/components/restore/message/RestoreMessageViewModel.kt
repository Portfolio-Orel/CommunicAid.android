package com.orels.presentation.ui.components.restore.message

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.domain.interactors.FolderInteractor
import com.orels.domain.interactors.MessageInteractor
import com.orels.domain.model.entities.Folder
import com.orels.domain.model.entities.Message
import com.orels.domain.util.extension.log
import com.orels.presentation.R
import com.orels.presentation.ui.components.util.CRUDResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author Orel Zilberman
 * 08/09/2022
 */

@HiltViewModel
class RestoreMessageViewModel @Inject constructor(
    private val messageInteractor: MessageInteractor,
    private val folderInteractor: FolderInteractor
) : ViewModel() {
    var state by mutableStateOf(RestoreMessageState())

    init {
        setMessages()
    }

    fun restore(message: Message) {
        state = state.copy(result = CRUDResult.Loading(data = message))
        val restoreJob = viewModelScope.async {
            message.isActive = true
            messageInteractor.updateMessage(message)
        }
        viewModelScope.launch(Dispatchers.IO) {
            state = try {
                restoreJob.await()
                setMessages()
                state.copy(result = CRUDResult.Success(data = message))
            } catch (e: Exception) {
                e.log()
                state.copy(
                    result = CRUDResult.Error(
                        message = R.string.restore_folder_failed,
                        data = message
                    )
                )
            }
        }
    }

    fun getMessageFolder(messageId: String): Folder? =
        folderInteractor.getFolderWithMessageId(messageId = messageId)


    private fun setMessages() {
        val messages = messageInteractor.getAllOnce(isActive = false)
            .filter {
                val folder = getMessageFolder(messageId = it.id)
                return@filter folder?.isActive == true
            }
            .sortedBy { it.title }
        state = state.copy(deletedMessages = messages)
    }

}