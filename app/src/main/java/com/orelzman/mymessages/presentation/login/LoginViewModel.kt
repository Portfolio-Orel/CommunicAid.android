package com.orelzman.mymessages.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    var state by mutableStateOf(LoginState())

    fun onUsernameChange(value: String) {
        state = state.copy(username = value)
    }

    fun onPasswordChange(value: String) {
        state = state.copy(password = value)
    }


    fun onEvent(event: LoginEvents) {
        when (event) {
//            is LoginEvents.AuthWithEmailAndPassowrd -> login(event.email, event.password)
//            is LoginEvents.AuthWithGmail -> googleSignIn(event.signInAccount)
        }
    }

}