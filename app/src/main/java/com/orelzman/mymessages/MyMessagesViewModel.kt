package com.orelzman.mymessages

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.domain.interactors.GeneralInteractor
import com.orelzman.mymessages.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyMessagesViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val generalInteractor: GeneralInteractor,

    ) : ViewModel() {
    var isLoading by mutableStateOf(false)
    var isAuthorized by mutableStateOf(false)

    init {
        viewModelScope.launch(Dispatchers.Main) {

            authInteractor.getUserFlow().collectLatest {
                isAuthorized = if (it == null || it.token == "" || it.userId == "") {
                    false
                } else {
                    try {
                        isLoading = true
                        generalInteractor.initData()
                        isLoading = false
                        true
                    } catch(ex: Exception) {
                        ex.log()
                        authInteractor.signOut()
                        false
                    }
                }
            }
        }
    }

    fun signOut() = viewModelScope.launch(Dispatchers.Main) {
        try {
            authInteractor.signOut()
        } catch (e: Exception) {
            e.log()
        }
    }
}