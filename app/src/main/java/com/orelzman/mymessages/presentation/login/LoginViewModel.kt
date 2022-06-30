package com.orelzman.mymessages.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.remote.repository.api.Repository
import com.orelzman.mymessages.data.remote.repository.dto.CreateUserBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val interactor: AuthInteractor,
    private val repository: Repository
) : ViewModel() {
    var state by mutableStateOf(LoginState())

    init {
        viewModelScope.launch {
            val user = interactor.getUser()
            if (user != null) {
                confirmUserCreated(user.userId)
            }
        }
    }

    fun onEvent(event: LoginEvents) {
        when (event) {
            is LoginEvents.UserLoggedInSuccessfully -> userLoggedInSuccessfully()
            is LoginEvents.UserRegisteredSuccessfully -> state =
                state.copy(showCodeConfirmation = true)
            is LoginEvents.ConfirmSignup -> confirmSignup(
                username = state.username,
                code = event.code
            )
        }
    }

    fun testRegistration() {
        state = state.copy(showCodeConfirmation = true)
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

    private fun userLoggedInSuccessfully() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userId = interactor.getUser()?.userId
                if (userId != null) {
                    confirmUserCreated(userId)
                } else {
                    state = state.copy(isAuthorized = false)
                }

            } catch (exception: Exception) {
                println()
            }
        }
    }

    private fun confirmSignup(username: String, code: String) {
        if (code.length != 6) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                interactor.confirmUser(username, "453432")
                val user = interactor.getUser()
                if (user?.userId != null) {
                    createUser(user.userId, user.email)
                    state = state.copy(isAuthorized = true)
                }
            } catch (exception: Exception) {
                println()
            }
        }
    }

    private suspend fun confirmUserCreated(userId: String) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val user = repository.getUser(userId)
                if (user?.userId == null) {
                    createUser(userId, user?.email ?: "")
                }
                state = state.copy(isAuthorized = true)
            }
        } catch (exception: Exception) {
            state = state.copy(isAuthorized = false)
        }
    }

    private suspend fun createUser(userId: String, email: String) {
        try {
            repository.createUser(
                CreateUserBody(
                    firstName = "Orel",
                    lastName = "Zilberman",
                    gender = "male",
                    email = email,
                    number = "0543056286",
                    userId = userId
                )
            )
        } catch (exception: Exception) {
            println()
        }
    }

}