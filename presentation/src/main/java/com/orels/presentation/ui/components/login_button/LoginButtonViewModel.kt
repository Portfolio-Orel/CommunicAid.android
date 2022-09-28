package com.orelzman.mymessages.presentation.components.login_button

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.domain.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginButtonViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
): ViewModel() {
    var state by mutableStateOf(LoginButtonState())

    fun login(username: String, password: String, onLoginComplete: (Boolean, Exception?) -> Unit) {
        state = state.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            state = try {
                authInteractor.signIn(username = username, password = password)
                onLoginComplete(true, null)
                state.copy(isLoading = false)
            } catch(e: Exception) {
                e.log()
                onLoginComplete(false, e)
                state.copy(isLoading = false)
            }
        }
    }
}