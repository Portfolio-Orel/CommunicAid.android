package com.orelzman.mymessages.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    ): ViewModel() {
    var state by mutableStateOf(MainState())

    init {
        state = if(savedStateHandle.get<String?>("uid") == null) {
            state.copy(isLoggedIn = false)
        } else {
            state.copy(isLoggedIn = true)
        }
    }
}