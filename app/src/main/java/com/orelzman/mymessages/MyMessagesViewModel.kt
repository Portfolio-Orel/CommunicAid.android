package com.orelzman.mymessages

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class MyMessagesViewModel(
    authInteractor: AuthInteractor
) : ViewModel() {

    var isAuthorized by mutableStateOf(false)

    init {
        viewModelScope.launch(Dispatchers.Main) {
            authInteractor.getUserFlow().collectLatest {
                if (it != null) {
                    isAuthorized = true
                }
            }
        }
    }
}