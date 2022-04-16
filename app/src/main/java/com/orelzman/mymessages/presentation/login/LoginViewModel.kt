package com.orelzman.mymessages.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val interactor: AuthInteractor
): ViewModel() {
    var state by mutableStateOf(LoginState())


    fun onEvent(event: LoginEvents) {
        when(event) {
            is LoginEvents.AuthWithEmailAndPassowrd -> login(event.email, event.password)
        }
    }
    private fun login(email: String, password: String) {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val user = interactor.auth(email = email, password = password)
                state = state.copy(user = user, isLoading = false)
                savedStateHandle.set("uid", user.firebaseUser?.uid)
            } catch (exception: Exception) {
                state = state.copy(error = exception.message)
            }
        }
    }
}