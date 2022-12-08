package com.orels.presentation.ui.login.forgot_password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.auth.domain.interactor.AuthInteractor
import com.orels.domain.util.extension.log
import com.orels.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
) : ViewModel() {
    var state by mutableStateOf(ForgotPasswordState())

    fun onForgotPassword(username: String) {
        state = state.copy(state = State.ForgotPassword(true))
        val forgotPasswordJob = viewModelScope.async {
            authInteractor.forgotPassword(username)
        }
        viewModelScope.launch {
            try {
                forgotPasswordJob.await()
                state = state.copy(state = State.ResetPassword(false))
            } catch (e: Exception) {
                state = state.copy(state = State.ForgotPassword(false), error = R.string.error)
                e.log()
            }
        }
    }

    fun onResetPassword(code: String, password: String, confirmPassword: String) {
        state = state.copy(state = State.ResetPassword(true))
        val resetPasswordJob = viewModelScope.async {
            authInteractor.resetPassword(code, password, confirmPassword)
        }
        viewModelScope.launch {
            try {
                resetPasswordJob.await()
                state = state.copy(state = State.Done)
            } catch (e: Exception) {
                state = state.copy(state = State.ResetPassword(true), error = R.string.error)
                e.log()
            }
        }
    }
}