package com.orels.presentation.ui.login.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.auth.domain.interactor.AuthInteractor
import com.orels.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authInteractor: AuthInteractor) : ViewModel() {
    var state by mutableStateOf(LoginState())
        private set

    init {
        checkUserAuthState()
    }

    private fun checkUserAuthState() {
        state = state.copy(isLoading = true)
        val authUserJob = viewModelScope.async {
            authInteractor.getUser()?.let {
                state = state.copy(authState = AuthState.SignedIn, isLoading = false)
            }
        }
        viewModelScope.launch {
            try {
                authUserJob.await()
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = R.string.error)
            }
        }
    }

    fun login() {
        state = state.copy(isLoading = true)
        val loginJob = viewModelScope.async {
            authInteractor.login(state.username, state.password)
        }
        viewModelScope.launch {
            state = try {
                loginJob.await()
                state.copy(authState = AuthState.SignedIn, isLoading = false)
            } catch (e: Exception) {
                state.copy(isLoading = false, error = R.string.error)
            }
        }
    }

    fun onUsernameChange(username: String) {
        state = state.copy(username = username)
    }

    fun onPasswordChange(password: String) {
        state = state.copy(password = password)
    }
}