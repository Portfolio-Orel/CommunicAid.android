package com.orelzman.mymessages.presentation.login

import android.security.keystore.UserNotAuthenticatedException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.exception.*
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.remote.AuthConfigFile
import com.orelzman.mymessages.domain.interactors.DatabaseInteractor
import com.orelzman.mymessages.domain.interactors.FolderInteractor
import com.orelzman.mymessages.domain.interactors.MessageInteractor
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.model.dto.body.create.CreateUserBody
import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.model.entities.SettingsKeys
import com.orelzman.mymessages.domain.repository.Repository
import com.orelzman.mymessages.util.extension.Log
import com.orelzman.mymessages.util.extension.hours
import com.orelzman.mymessages.util.extension.log
import com.orelzman.mymessages.util.extension.minutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.InvalidParameterException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val interactor: AuthInteractor,
    private val repository: Repository,
    private val messageInteractor: MessageInteractor,
    private val folderInteractor: FolderInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val databaseInteractor: DatabaseInteractor,
    @AuthConfigFile private val authConfigFile: Int,
) : ViewModel() {
    var state by mutableStateOf(LoginState())

    init {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                interactor.init(authConfigFile)
                var isAuthorized = false
                val user = interactor.getUser()
                Log.v("got user: $user")
                if (user != null) {
                    isAuthorized = true
                } else {
                    databaseInteractor.clear()
                }
                state = state.copy(isAuthorized = isAuthorized, isLoading = false)
                if(isAuthorized) {
                    userAuthorizedSuccessfully()
                }
            } catch (exception: Exception) {
                when (exception) {
                    is UserNotAuthenticatedException -> {/*User needs to login again-do it with saved credentials*/
                    }
                    is WrongCredentialsException -> {

                    }
                }
                exception.log()
                state = state.copy(isLoading = false, isAuthorized = false)
            }
        }
    }

    fun onEvent(event: LoginEvents) {
        when (event) {
            is LoginEvents.OnLoginCompleted -> {
                if (event.isAuthorized) {
                    userAuthorizedSuccessfully()
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

    private fun initData(userId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            messageInteractor.initMessagesAndMessagesInFolders(userId = userId)
            folderInteractor.initFolders(userId = userId)
            settingsInteractor.initSettings(userId = userId)
        }
    }

    private fun loginFailed(exception: Exception?) {
        state = when (exception) {
            is LimitExceededException -> {
                CoroutineScope(Dispatchers.IO).launch {
                    interactor.getUser()?.let {
                        settingsInteractor.createSettings(
                            Settings(
                                SettingsKeys.LoginLimitExceeded,
                                Date().time.toString()
                            ),
                            it.userId
                        )
                    }
                }
                val lastLimitExceedTime =
                    settingsInteractor.getSettings(SettingsKeys.LoginLimitExceeded)?.value?.toLongOrNull()
                        ?: Date().time
                val lastLimitExceedDate = Date(lastLimitExceedTime)
                state.copy(
                    error = "ניסית יותר מידי פעמים. תנסה שוב 15 דק׳ מ:${
                        lastLimitExceedDate.hours()
                    }:${lastLimitExceedDate.minutes()}"
                )
            }
            is InvalidParameterException -> {
                state.copy(error = "הפרטים שהוזנו לא נכונים...")
            }
            is UserNotConfirmedException -> {
                state.copy(showCodeConfirmation = true)
            }
            is UserNotFoundException -> {
                state.copy(error = "המשתמש לא מוכר לנו...")
            }
            is WrongCredentialsException -> {
                state.copy(error = "המשתמש לא מוכר לנו...")
            }
            else -> {
                state.copy(error = "קרתה שגיאה לא צפויה. אנחנו מטפלים בזה")
            }
        }
        state = state.copy(isLoading = false)
    }

    private fun userAuthorizedSuccessfully() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val userId = interactor.getUser()?.userId
                val isAuthorized = if (userId != null) {
                    confirmUserCreated(userId, state.email)
                } else {
                    false
                }
                if (isAuthorized) {
                    initData(userId ?: "")
                }
                state = state.copy(isAuthorized = isAuthorized)
            } catch (exception: Exception) {
                exception.log()
                state = state.copy(isLoading = false)
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
                    userAuthorizedSuccessfully()
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

    private suspend fun confirmUserCreated(userId: String, email: String = ""): Boolean {
        return try {
            val user = repository.getUser(userId)
            if (user?.userId == null) {
                createUser(userId, email)
            }
            true
        } catch (exception: Exception) {
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
        } catch (exception: Exception) {
            exception.log()
        }
    }

}