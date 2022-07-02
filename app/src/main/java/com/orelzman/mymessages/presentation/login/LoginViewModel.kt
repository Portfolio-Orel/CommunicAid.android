package com.orelzman.mymessages.presentation.login

import android.security.keystore.UserNotAuthenticatedException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.exception.CodeMismatchException
import com.orelzman.auth.domain.exception.UserNotConfirmedException
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.local.interactors.database.DatabaseInteractor
import com.orelzman.mymessages.data.remote.repository.api.Repository
import com.orelzman.mymessages.data.remote.repository.dto.body.create.CreateUserBody
import com.orelzman.mymessages.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.InvalidParameterException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val interactor: AuthInteractor,
    private val repository: Repository,
    private val databaseInteractor: DatabaseInteractor,
) : ViewModel() {
    var state by mutableStateOf(LoginState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                interactor.initAWS()
                val user = interactor.getUser()
                if (user != null) {
                    confirmUserCreated(user.userId)
                } else {
                    databaseInteractor.clear()
                }
            } catch (exception: Exception) {
                state = state.copy(isCheckingAuth = false)
                exception.log()
                when (exception) {
                    is UserNotAuthenticatedException -> {/*User needs to login again-do it with saved credentials*/
                    }
                }
            }
        }
    }

    fun onEvent(event: LoginEvents) {
        when (event) {
            is LoginEvents.OnLoginCompleted -> {
                if (event.isAuthorized) {
                    userLoggedInSuccessfully()
                } else {
                    loginFailed(event.exception)
                }
            }
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

    fun hideRegistrationConfirmation() {
        state = state.copy(showCodeConfirmation = false)
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

    private fun loginFailed(exception: Exception?) {
        when (exception) {
            is InvalidParameterException -> { /*Wrong credentials.*/
            }
            is UserNotConfirmedException -> {
                state = state.copy(showCodeConfirmation = true)
            }
            else -> {/*Unknown*/
            }
        }
    }

    private fun userLoggedInSuccessfully() {
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                interactor.confirmUser(username, code)
                val user = interactor.getUser()
                if (user?.userId != null) {
                    state = state.copy(isAuthorized = true)
                }
            } catch (exception: Exception) {
                when (exception) {
                    is CodeMismatchException -> {

                    }
                    else -> {}
                }
            }
        }
    }

    private suspend fun confirmUserCreated(userId: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val user = repository.getUser(userId)
                if (user?.userId == null) {
                    createUser(userId, user?.email ?: "")
                }
                state = state.copy(isAuthorized = true, isCheckingAuth = false)
            }
        } catch (exception: Exception) {
            state = state.copy(isAuthorized = false, isCheckingAuth = false)
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
            exception.log()
            println()
        }
    }

}