package com.orelzman.mymessages.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val authInteractor: AuthInteractor
    ): ViewModel() {
    var state by mutableStateOf(MainState())

    init {

    }

    fun getFolders() {
        viewModelScope.launch {
            val x = authInteractor.user?.let { repository.getFolders(it.uid) }
            val y = 4
        }
    }
}