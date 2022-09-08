package com.orelzman.mymessages.presentation.components.restore.message

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.orelzman.mymessages.domain.interactors.MessageInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author Orel Zilberman
 * 08/09/2022
 */

@HiltViewModel
class RestoreMessageViewModel @Inject constructor(
    private val messageInteractor: MessageInteractor,
): ViewModel() {
    var state by mutableStateOf(RestoreMessageState())
}