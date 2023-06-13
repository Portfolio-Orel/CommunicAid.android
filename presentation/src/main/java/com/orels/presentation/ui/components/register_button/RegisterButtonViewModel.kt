package com.orels.presentation.ui.components.register_button

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.domain.interactors.AuthInteractor
import com.orels.domain.util.common.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterButtonViewModel @Inject constructor(
    val authInteractor: AuthInteractor
) : ViewModel() {
    var state by mutableStateOf(RegisterButtonState())

    fun register(
        username: String,
        password: String,
        email: String,
        isSaveCredentials: Boolean = false,
        onRegisterComplete: () -> Unit
    ) {
        val signUpJob = viewModelScope.async {
            state = state.copy(isLoading = true, isRegisterAttempt = true)
            authInteractor.signUp(
                email = email,
                username = username,
                password = password,
                isSaveCredentials = isSaveCredentials
            )
            state =
                state.copy(isLoading = false)
            onRegisterComplete()
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                signUpJob.await()
            } catch (e: Exception) {
                Logger.e(e.localizedMessage ?: "Error signing up")
                state =
                    state.copy(isLoading = false)
            }
        }
    }
}