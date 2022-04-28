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
        if (interactor.user != null) {
            state = state.copy(user = User(uid = interactor.user!!.uid))
        }
    }

    fun onEvent(event: LoginEvents) {
        when (event) {
            is LoginEvents.AuthWithEmailAndPassowrd -> login(event.email, event.password)
            is LoginEvents.AuthWithGmail -> googleSignIn()
        }
    }

    private fun googleSignIn() {

    }

    private fun login(email: String, password: String) {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            state = try {
                val user = interactor.auth(email = email, password = password)
                state.copy(user = User(uid = user?.uid ?: ""), isLoading = false)
            } catch (exception: Exception) {
                state.copy(error = exception.message, isLoading = false)
            }
        }
    }
}