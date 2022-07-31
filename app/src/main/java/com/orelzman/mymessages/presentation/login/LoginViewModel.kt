package com.orelzman.mymessages.presentation.login

import android.security.keystore.UserNotAuthenticatedException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.exception.CodeMismatchException
import com.orelzman.auth.domain.exception.UserNotConfirmedException
import com.orelzman.auth.domain.exception.UserNotFoundException
import com.orelzman.auth.domain.exception.WrongCredentialsException
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.R
import com.orelzman.mymessages.data.remote.AuthConfigFile
import com.orelzman.mymessages.domain.interactors.GeneralInteractor
import com.orelzman.mymessages.domain.managers.worker.WorkerManager
import com.orelzman.mymessages.domain.managers.worker.WorkerType
import com.orelzman.mymessages.domain.model.dto.body.create.CreateUserBody
import com.orelzman.mymessages.domain.repository.Repository
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
    private val generalInteractor: GeneralInteractor,
    private val workerManager: WorkerManager,
    @AuthConfigFile private val authConfigFile: Int,
) : ViewModel() {
    var state by mutableStateOf(LoginState())

    init {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                interactor.init(authConfigFile)
                var isAuthorized = false
                val user = interactor.getUser()

                if (user != null) {
                    isAuthorized = true
                } else {
                    generalInteractor.clearAllDatabases()
                }
                state = state.copy(isAuthorized = isAuthorized, isLoading = false)
                if (isAuthorized) {
                    onUserAuthorizedSuccessfully()
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
                    onUserAuthorizedSuccessfully(true)
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

    fun onLoginClick() {
        state = state.copy(error = null)
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

    private fun initData() {
        viewModelScope.launch(Dispatchers.Main) {
            generalInteractor.initData()
        }
    }

    private fun loginFailed(exception: Exception?) {
        state = when (exception) {
            is InvalidParameterException -> {
                state.copy(error = R.string.error_wrong_credentials_inserted)
            }
            is UserNotConfirmedException -> {
                state.copy(error = R.string.error_user_not_confirmed)
            }
            is UserNotFoundException -> {
                state.copy(error = R.string.error_user_not_signed_in)
            }
            is WrongCredentialsException -> {
                state.copy(error = R.string.error_wrong_credentials_inserted)
            }
            else -> {
                state.copy(error = R.string.error_unknown)
            }
        }
        state = state.copy(isLoading = false)
    }

    private fun onUserAuthorizedSuccessfully(initData: Boolean = false) {
        viewModelScope.launch(Dispatchers.Main) {
            state = try {
                val userId = interactor.getUser()?.userId
                val isAuthorized = if (userId != null && userId != "") {
                    confirmUserCreated(userId, state.email)
                } else {
                    false
                }
                if (isAuthorized) {
                    workerManager.startWorker(
                        type = WorkerType.UploadCalls
                    )
                    if (initData) {
                        initData()
                    }
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
                interactor.confirmUser(username, code)
                val user = interactor.getUser()
                if (user?.userId != null) {
                    onUserAuthorizedSuccessfully()
                }
            } catch (e: Exception) {
                when (e) {
                    is CodeMismatchException -> {

                    }

                    else -> {}
                }
            }
        }
    }

    private suspend fun confirmUserCreated(userId: String, email: String = ""): Boolean {
        return try {
            val user = repository.getUser(userId)
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