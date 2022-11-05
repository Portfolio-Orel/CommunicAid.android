package com.orels.presentation.ui.login

import android.app.Activity
import android.security.keystore.UserNotAuthenticatedException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.domain.interactors.AuthInteractor
import com.orels.domain.interactors.GeneralInteractor
import com.orels.domain.model.dto.body.create.CreateUserBody
import com.orels.domain.model.exception.CodeMismatchException
import com.orels.domain.model.exception.UserNotConfirmedException
import com.orels.domain.model.exception.UserNotFoundException
import com.orels.domain.model.exception.WrongCredentialsException
import com.orels.domain.repository.Repository
import com.orels.domain.util.extension.log
import com.orels.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.security.InvalidParameterException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val interactor: AuthInteractor,
    private val repository: Repository,
    private val generalInteractor: GeneralInteractor
) : ViewModel() {
    var state by mutableStateOf(LoginState())

    init {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                if (interactor.isAuthorized(interactor.getUser(), "LoginViewModel init")) {
                    onUserAuthorizedSuccessfully()
                } else {
                    state = state.copy(isAuthorized = false, isLoading = false)
                }
            } catch (e: Exception) {
                when (e) {
                    is UserNotAuthenticatedException -> {/*User needs to login again-do it with saved credentials*/

                    }
                    is WrongCredentialsException -> {

                    }
                }
                e.log()
                state = state.copy(isLoading = false, isAuthorized = false)
            }
        }
    }

    fun onEvent(event: LoginEvents) {
        when (event) {
            is LoginEvents.OnLoginCompleted -> {
                if (event.isAuthorized) {
                    generalInteractor.clearAllDatabases()
                    onUserAuthorizedSuccessfully()
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

    fun googleAuth(activity: Activity) {
        val googleAuthJob = viewModelScope.async {
            interactor.googleAuth(activity = activity)
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                googleAuthJob.await()
                onEvent(LoginEvents.OnLoginCompleted(isAuthorized = true, exception = null))
            } catch (e: Exception) {
                onEvent(LoginEvents.OnLoginCompleted(isAuthorized = false, exception = e))
            }
        }
    }

    fun onLoginClick() {
        state = state.copy(error = null)
    }

    fun hideRegistrationConfirmation() {
        state = state.copy(showCodeConfirmation = false)
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
        state = when (exception) {
            is InvalidParameterException -> {
                state.copy(error = R.string.error_wrong_credentials_inserted)
            }
            is UserNotConfirmedException -> {
                state.copy(showCodeConfirmation = true)
            }
            is UserNotFoundException -> {
                state.copy(error = R.string.error_user_not_signed_in)
            }
            is WrongCredentialsException -> {
                state.copy(error = R.string.error_wrong_credentials_inserted)
            }
            else -> {
                if (exception != null) state.copy(error = R.string.error_unknown)
                else state.copy(error = null)
            }
        }
        state = state.copy(isLoading = false)
    }

    private fun onUserAuthorizedSuccessfully() {
        viewModelScope.launch(Dispatchers.Main) {
            state = try {
                val user = interactor.getUser()
                val isAuthorized = if(user != null && interactor.isAuthorized(user, "LoginViewModelUserAuth")) {
                    confirmUserCreated(user.userId, user.email)
                } else {
                    false
                }
                state.copy(
                    isAuthorized = isAuthorized,
                    error = if (!isAuthorized) R.string.error_unknown else R.string.empty_string
                )
            } catch (e: Exception) {
                e.log()
                state.copy(isLoading = false)
            }
        }.invokeOnCompletion {
            state = state.copy(isLoading = false)
        }
    }

    private fun confirmSignup(username: String, code: String) {
        if (code.length != 6) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                interactor.confirmUser(username, state.password, code)
                val user = interactor.getUser()
                if (user?.userId != null) {
                    onUserAuthorizedSuccessfully()
                }
                state = state.copy(showCodeConfirmation = false)
            } catch (e: Exception) {
                when (e) {
                    is CodeMismatchException -> {

                    }

                    else -> {}
                }
            }
        }
    }

    private suspend fun confirmUserCreated(userId: String, email: String): Boolean {
        return try {
            val user = repository.getUser()
            if (user?.userId == null) {
                createUser(userId, email)
            }
            true
        } catch (e: Exception) {
            e.log()
            false
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
        } catch (e: Exception) {
            e.log()
        }
    }

}