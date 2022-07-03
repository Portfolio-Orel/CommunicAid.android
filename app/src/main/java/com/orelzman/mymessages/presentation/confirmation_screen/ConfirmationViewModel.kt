package com.orelzman.mymessages.presentation.confirmation_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmationViewModel @Inject constructor(
    private val authInteractor: AuthInteractor
) : ViewModel() {

    var state by mutableStateOf(ConfirmationState())

    fun onCodeChange(
        value: String,
        username: String,
        onUserConfirmed: (String) -> Unit = {}
    ) {
        state = state.copy(code = value, username = username, exception = null)
        if (value.length == 6) {
            state = state.copy(isLoading = true)
            confirmCode(code = value, username = username, onUserConfirmed = onUserConfirmed)
        }
    }

    private fun confirmCode(
        username: String, code: String,
        onUserConfirmed: (String) -> Unit = {}
    ) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                authInteractor.confirmUser(username = username, code = code)
                onUserConfirmed(username)
                state = ConfirmationState()
            }
        } catch (exception: Exception) {
            state = state.copy(isLoading = false, exception = exception)
        }
    }
}