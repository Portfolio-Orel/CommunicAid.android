package com.orelzman.mymessages.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.auth.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val interactor: AuthInteractor,

    ) : ViewModel() {
    var state by mutableStateOf(LoginState())

    init {
        viewModelScope.launch {
            if (interactor.getUser() != null) {
                state = state.copy(user = User(userId = interactor.getUser()!!.userId))
            }
        }
    }

    fun onEvent(event: LoginEvents) {
        state = when (event) {
            is LoginEvents.UserLoggedInSuccessfully -> state.copy(user = event.user)
            is LoginEvents.UserRegisteredSuccessfully -> state.copy(showCodeConfirmation = true)

        }
    }

    fun onRegisterClick() {
        state = state.copy(isRegister = true)
    }

    fun onPasswordChange(value: String) {
        state = state.copy(password = value)
    }

    fun onEmailChange(value: String) {
        state = state.copy(email = value)
    }

    fun onUsernameChange(value: String) {
        state = state.copy(username = value)
    }

}