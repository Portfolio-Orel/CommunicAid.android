package com.orelzman.mymessages.presentation.register_button

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.orelzman.auth.domain.interactor.AuthInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        CoroutineScope(Dispatchers.IO).launch {
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
    }
}