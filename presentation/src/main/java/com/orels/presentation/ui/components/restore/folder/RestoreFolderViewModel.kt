package com.orels.presentation.ui.components.restore.folder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.domain.interactors.FolderInteractor
import com.orels.domain.interactors.MessageInFolderInteractor
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
class RestoreFolderViewModel @Inject constructor(
    private val folderInteractor: FolderInteractor,
    private val messageInteractor: MessageInteractor,
    private val messageInFolderInteractor: MessageInFolderInteractor
) : ViewModel() {

    var state by mutableStateOf(RestoreFolderState())

    init {
        setFolders()
    }

    fun restore(folder: Folder) {
        state = state.copy(result = CRUDResult.Loading(data = folder))
        val restoreJob = viewModelScope.async {
            folder.isActive = true
            folderInteractor.update(folder)
        }
        viewModelScope.launch(Dispatchers.IO) {
            state = try {
                restoreJob.await()
                setFolders()
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

    private fun setFolders() {
        val folders = folderInteractor.getAllOnce(isActive = false)
            .sortedBy { it.title }
        state = state.copy(deletedFolders = folders)
    }
}