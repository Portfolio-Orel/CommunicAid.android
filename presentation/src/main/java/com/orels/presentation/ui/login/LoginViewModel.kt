package com.orels.presentation.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.auth.domain.interactor.AuthInteractor
import com.orels.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authInteractor: AuthInteractor) : ViewModel() {
    var state by mutableStateOf(LoginState())
        private set

    fun login() {
        state = state.copy(isLoading = true)
        val loginJob = viewModelScope.async {
            authInteractor.login(state.username, state.password)
        }
        viewModelScope.launch {
            loginJob.await()
            withContext(Dispatchers.Main) {
                state = try {
                    state.copy(isLoading = false)
                } catch (e: Exception) {
                    state.copy(isLoading = false, error = R.string.error)
                }
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