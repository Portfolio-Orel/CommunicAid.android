package com.orels.presentation.ui.components.top_app_bar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.orels.domain.managers.phonecall.interactor.PhoneCallManagerInteractor
import com.orels.domain.model.entities.toPhoneCall
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