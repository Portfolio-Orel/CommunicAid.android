package com.orelzman.mymessages.presentation.unhandled_calls

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.local.interactors.unhandled_calls.UnhandledCallsInteractor
import com.orelzman.mymessages.domain.model.CallLogEntity
import com.orelzman.mymessages.util.CallLogUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnhandledCallsViewModel @Inject constructor(
    private val unhandledCallsInteractor: UnhandledCallsInteractor,
    private val authInteractor: AuthInteractor,
) : ViewModel() {

    var state by mutableStateOf(UnhandledCallsState())

    fun initCalls(context: Context) {
        viewModelScope.launch {
            authInteractor.user?.uid?.let {
                val unhandledCalls = unhandledCallsInteractor.getAll(it)
                    .sortedByDescending { unhandledCall -> unhandledCall.phoneCall.startDate }
                    .distinctBy { unhandledCall -> unhandledCall.phoneCall.startDate }
                val callsFromCallLog = getCallsFromCallLog(context = context)
                val callsToHandle = unhandledCallsInteractor.filterUnhandledCalls(
                    unhandledCalls = unhandledCalls,
                    callLogs = callsFromCallLog
                )
                state = state.copy(callsToHandle = callsToHandle)
            }
        }
    }

    private fun getCallsFromCallLog(context: Context): ArrayList<CallLogEntity> =
        CallLogUtils.getTodaysCallLog(context)
}