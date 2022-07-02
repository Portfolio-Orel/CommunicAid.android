package com.orelzman.mymessages.presentation.logout_screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutButtonViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
): ViewModel() {
    var state by mutableStateOf(LogoutButtonState())

    fun logout(onLogoutComplete: () -> Unit) {
        state = state.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            state = try {
                authInteractor.signOut()
                onLogoutComplete()
                state.copy(isLoading = false)
            } catch(exception: Exception) {
                Log.e("AWSAuth", exception.message ?: "")
                state.copy(isLoading = false)
            }
        }
    }
}