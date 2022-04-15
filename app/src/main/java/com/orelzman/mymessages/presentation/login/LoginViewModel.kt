package com.orelzman.mymessages.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
//    private val savedStateHandle: SavedStateHandle,
    private val interactor: AuthInteractor
): ViewModel() {
    var state by mutableStateOf(LoginState())

    fun login(email: String = "1@2.com", password: String = "o123456") {
        state = state.copy(isLoadingLogin = true)
        viewModelScope.launch {
            val user = interactor.auth(email = email, password = password)
            state = state.copy(user = user, isLoadingLogin = false)
        }
    }
}