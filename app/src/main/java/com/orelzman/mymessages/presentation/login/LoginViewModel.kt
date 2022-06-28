package com.orelzman.mymessages.presentation.login

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.auth.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val interactor: AuthInteractor,

    ) : ViewModel() {
    var state by mutableStateOf(LoginState())

    init {
        if (interactor.user != null) {
            state = state.copy(user = User(uid = interactor.user!!.uid))
        }
        CoroutineScope(Dispatchers.IO).launch {
            interactor.init()
        }
    }

    fun test(activity: Activity) {
        CoroutineScope(Dispatchers.IO).launch {
            interactor.googleAuth(activity)
        }
    }

    fun onEvent(event: LoginEvents) {
        when (event) {
            is LoginEvents.AuthWithEmailAndPassowrd -> login(event.email, event.password)
            is LoginEvents.AuthWithGmail -> googleSignIn(event.signInAccount)
        }
    }

    private fun googleSignIn(account: GoogleSignInAccount) =
        CoroutineScope(Dispatchers.IO).launch {
            interactor.googleAuth(account = account)
        }


    private fun login(email: String, password: String) {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            state = try {
                val user = interactor.auth(email = email, password = password)
                state.copy(user = User(uid = user?.uid ?: ""), isLoading = false)
            } catch (exception: Exception) {
                state.copy(error = exception.message, isLoading = false)
            }
        }
    }
}