package com.orels.presentation.ui.login

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.auth.domain.interactor.AuthInteractor
import com.orels.auth.domain.model.SignInStep
import com.orels.auth.domain.model.exception.UserNotConfirmedException
import com.orels.auth.domain.model.exception.UserNotFoundException
import com.orels.auth.domain.model.exception.WrongCredentialsException
import com.orels.domain.interactors.UserInteractor
import com.orels.domain.util.common.Logger
import com.orels.domain.util.extension.log
import com.orels.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val userInteractor: UserInteractor,
    ) : ViewModel() {
    var state by mutableStateOf(LoginState())
        private set

    fun login() {
        @StringRes var error: Int? = null
        var nextSignInStep: SignInStep? = null
        val isPasswordValid = authInteractor.isPasswordValid(state.password)
        val isUsernameValid = state.username.isNotBlank()

        if (!isPasswordValid || !isUsernameValid) {
            state = state.copy(
                passwordField = Fields.Password(!isPasswordValid),
                usernameField = Fields.Username(!isUsernameValid),
                isLoading = false,
            )
            return
        }

        state = state.copy(isLoading = true)

        val loginJob = viewModelScope.async {
            authInteractor.login(state.username, state.password)
        }
        viewModelScope.launch {
            try {
                when (val nextStep = loginJob.await()) {
                    is SignInStep.Error -> {
                        Logger.e("Error in login: ${nextStep.error}")
                        error = R.string.error_unknown
                        nextSignInStep = nextStep
                    }
                    is SignInStep.Done -> {
                        error = null
                        nextSignInStep = null
                        userInteractor.setUser()
                    }
                    else -> {
                        nextSignInStep = nextStep
                        if (nextSignInStep is SignInStep.ConfirmSignInWithNewPassword) {
                            confirmSignInWithNewPassword(state.password, state.password)
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    state = state.copy(isLoading = false)
                }
            } catch (e: UserNotConfirmedException) {
                error = R.string.error_user_not_confirmed
            } catch (e: UserNotFoundException) {
                error = R.string.error_username_does_not_exist
            } catch (e: WrongCredentialsException) {
                error = R.string.error_wrong_credentials_inserted
            } catch (e: Exception) {
                error = R.string.error_unknown
                e.log()
            } finally {
                withContext(Dispatchers.Main) {
                    state = state.copy(isLoading = false, error = error, nextStep = nextSignInStep)
                }
            }
        }
    }

    fun confirmSignInWithNewPassword(
        password: String,
        confirmPassword: String
    ) {
        @StringRes var error: Int? = null
        val isPasswordValid = authInteractor.isPasswordValid(password)
        val isConfirmPasswordValid = authInteractor.isPasswordValid(confirmPassword)
        val isPasswordMatching = password == confirmPassword

        if (!isPasswordValid || !isConfirmPasswordValid || !isPasswordMatching) {
            // TODO: Add error messages
        }

        state = state.copy(isLoading = true)

        val confirmJob = viewModelScope.async {
            authInteractor.confirmSignInWithNewPassword(password)
        }
        viewModelScope.launch {
            try {
                confirmJob.await()
                error = null
            } catch (e: Exception) {
                error = R.string.error_unknown
                e.log()
            } finally {
                withContext(Dispatchers.Main) {
                    state = state.copy(isLoading = false, error = error)
                }
            }
        }
    }

    fun onDismissConfirmationScreen() {
        state = state.copy(nextStep = null)
    }

    fun onUsernameChange(username: String) {
        state = state.copy(username = username)
    }

    fun onPasswordChange(password: String) {
        state = state.copy(password = password)
    }
}