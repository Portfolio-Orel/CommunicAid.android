package com.orelzman.mymessages.presentation.components.top_app_bar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.orelzman.mymessages.domain.model.entities.toPhoneCall
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManagerInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopAppBarViewModel @Inject constructor(
    private val phoneCallManagerInteractor: PhoneCallManagerInteractor,
    ): ViewModel() {
    var state by mutableStateOf(TopAppBarState())

    init {
        observeNumberOnTheLine()
    }

    private fun observeNumberOnTheLine() {
        CoroutineScope(Dispatchers.Main).launch {
            phoneCallManagerInteractor.callsDataFlow.collectLatest {
                state = state.copy(callOnTheLine = it.callOnTheLine?.toPhoneCall())
            }
        }
    }
}