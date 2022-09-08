package com.orelzman.mymessages.presentation.components.restore.folder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.interactors.FolderInteractor
import com.orelzman.mymessages.domain.interactors.MessageInFolderInteractor
import com.orelzman.mymessages.domain.interactors.MessageInteractor
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.domain.util.extension.log
import com.orelzman.mymessages.presentation.components.util.CRUDResult
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
class RestoreFolderViewModel @Inject constructor(
    private val folderInteractor: FolderInteractor,
    private val messageInteractor: MessageInteractor,
    private val messageInFolderInteractor: MessageInFolderInteractor
) : ViewModel() {

    var state by mutableStateOf(RestoreFolderState())

    init {
        val deletedFolders = folderInteractor.getAllOnce(isActive = false)
        state = state.copy(deletedFolders = deletedFolders)
    }

    fun restore(folder: Folder) {
        state = state.copy(result = CRUDResult.Loading())
        val restoreJob = viewModelScope.async {
            folder.isActive = true
            folderInteractor.updateFolder(folder)
        }
        viewModelScope.launch(Dispatchers.IO) {
            state = try {
                restoreJob.await()
                state.copy(result = CRUDResult.Success(data = folder))
            } catch (e: Exception) {
                e.log()
                state.copy(
                    result = CRUDResult.Error(
                        message = R.string.restore_folder_failed,
                        data = folder
                    )
                )
            }
        }
    }

    fun getFoldersMessages(folderId: String): List<Message> =
        messageInFolderInteractor.getMessagesInFoldersOnce()
            .filter { it.folderId == folderId }
            .mapNotNull { messageInteractor.getMessage((it.messageId)) }
}