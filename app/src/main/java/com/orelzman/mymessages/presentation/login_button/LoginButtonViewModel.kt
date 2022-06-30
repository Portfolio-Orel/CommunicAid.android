package com.orelzman.mymessages.presentation.login_button

import android.util.Log
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
class LoginButtonViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
): ViewModel() {
    var state by mutableStateOf(LoginButtonState())

    fun login(username: String, password: String) {
        state = state.copy(isSignInAttempt = true)
        CoroutineScope(Dispatchers.IO).launch {
            state = try {
                authInteractor.init()
                authInteractor.signIn(username = username, password = password)
                val user = authInteractor.getUser()
                state = state.copy(isLoading = false, isAuthorized = true, user = user)
                state.copy(isSignInAttempt = false)
            } catch(exception: Exception) {
                Log.e("AWSAuth", exception.message ?: "")
                state = state.copy(isLoading = false, isAuthorized = false)
                state.copy(isSignInAttempt = false)
            }
        }
    }
}