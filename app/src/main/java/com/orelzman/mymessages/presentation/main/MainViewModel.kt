package com.orelzman.mymessages.presentation.main

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val folderInteractor: FolderInteractor,
    private val messageInteractor: MessageInteractor,
    private val authInteractor: AuthInteractor
    ): ViewModel() {
    private var state by mutableStateOf(MainState(folders = emptyList(), messages = emptyList()))

    fun getMessages() {
        viewModelScope.launch {
            val messages = authInteractor.user?.let { messageInteractor.getMessages(it.uid) }
            if(messages != null) {
                copyState(state.folders, messages)
            }
        }
    }

    fun getFolders() {
        viewModelScope.launch {
            val folders = authInteractor.user?.let { folderInteractor.getFolders(it.uid) }
            if(folders != null) {
                copyState(folders, state.messages)
            }
        }
    }

    private fun copyState(folders: List<Folder>, messages: List<Message>) {
        state = state.copy(messages = messages, folders = folders)
    }
}