package com.orels.presentation.ui.components.top_app_bar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.orels.domain.interactors.CallDetailsInteractor
import com.orels.domain.interactors.CallPreferences
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
    private val callLogInteractor: CallDetailsInteractor
    ): ViewModel() {
    var state by mutableStateOf(TopAppBarState())

    init {
        observeNumberOnTheLine()
    }

    private fun observeNumberOnTheLine() {
        CoroutineScope(Dispatchers.Main).launch {
            phoneCallManagerInteractor.callsDataFlow.collectLatest { callPreferences: CallPreferences ->
                val phoneCall = callPreferences.callOnTheLine?.toPhoneCall()
                phoneCall?.name = phoneCall?.number?.let { callLogInteractor.getContactName(number = it) }
                state = state.copy(callOnTheLine = phoneCall)
            }
        }
    }
}