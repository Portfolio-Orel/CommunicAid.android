package com.orels.presentation.ui.components.confirmation_pop_up

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.domain.model.exception.CodeExpiredException
import com.orels.domain.model.exception.CodeMismatchException
import com.orels.domain.model.exception.NotAuthorizedException
import com.orels.domain.interactors.AuthInteractor
import com.orels.domain.util.extension.log
import com.orels.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmationViewModel @Inject constructor(
    private val authInteractor: AuthInteractor
) : ViewModel() {

    var state by mutableStateOf(ConfirmationState())

    fun onCodeChange(
        value: String,
        username: String,
        password: String,
        onUserConfirmed: () -> Unit = {}
    ) {
        state = state.copy(code = value, username = username, error = null)
        if (value.length == 6) {
            state = state.copy(isLoading = true)
            confirmCode(code = value, username = username, password = password, onUserConfirmed = onUserConfirmed)
        }
    }

    private fun confirmCode(
        username: String,
        password: String,
        code: String,
        onUserConfirmed: () -> Unit = {}
    ) {
        val job = viewModelScope.async {
            authInteractor.confirmUser(username = username, password = password, code = code)
            onUserConfirmed()
            state = state.copy(isLoading = false, code = "", error = null)
        }
        viewModelScope.launch(Dispatchers.Main) {
            try {
                job.await()
            } catch (e: Exception) {
                @StringRes val error: Int =
                    when (e) {
                        is CodeMismatchException -> R.string.error_code_mismatch
                        is CodeExpiredException -> R.string.error_code_expired
                        is NotAuthorizedException -> R.string.error_unknown
                        else -> R.string.error_unknown
                    }
                e.log()
                state = state.copy(isLoading = false, error = error)
            }
            onUserConfirmed()
        }
    }
}