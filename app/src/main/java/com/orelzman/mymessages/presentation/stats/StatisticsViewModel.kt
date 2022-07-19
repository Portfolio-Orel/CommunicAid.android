package com.orelzman.mymessages.presentation.stats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.domain.interactors.CallLogInteractor
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.model.entities.UploadState
import com.orelzman.mymessages.domain.model.entities.toPhoneCalls
import com.orelzman.mymessages.util.common.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val phoneCallsInteractor: PhoneCallsInteractor,
    private val authInteractor: AuthInteractor,
    private val callLogInteractor: CallLogInteractor
    ) : ViewModel() {
    var state by mutableStateOf(StatisticsState())

    val isRefreshing = MutableSharedFlow<Boolean>()

    init {
        refreshData()
    }

    fun sendCallLogs() {
        state = state.copy(isLoadingCallLogSend = true)
        val callLogs = callLogInteractor.getTodaysCallLog()
        viewModelScope.launch(Dispatchers.IO) {
            authInteractor.getUser()?.let {
                val phoneCalls = callLogs.toPhoneCalls().map { call ->
                    call.type = "CALL_LOG_${call.type}"
                    return@map call
                }
                phoneCallsInteractor.createPhoneCalls(it.userId, phoneCalls)
            }
        }.invokeOnCompletion {
            state = state.copy(isLoadingCallLogSend = false)
        }
    }

    fun refreshData() {
        viewModelScope.launch(Dispatchers.Main) {
            val todaysDate = DateUtils.getStartOfDay()
            val cal = Calendar.getInstance()
            cal.time = Date()
            var hours = cal.get(Calendar.HOUR_OF_DAY).toString()
            var minutes = cal.get(Calendar.MINUTE).toString()
            if(hours.length < 2) {
                hours = "0$hours"
            }
            if(minutes.length < 2) {
                minutes = "0$minutes"
            }
            val phoneCalls = phoneCallsInteractor.getAll().filter { it.startDate > todaysDate }
            val callsCountToday = callLogInteractor.getTodaysCallLog().size
            val callsNotUploaded =
                phoneCalls.filter { it.uploadState == UploadState.NotUploaded }.size
            val callsUploaded = phoneCalls.filter { it.uploadState == UploadState.Uploaded }.size
            val callsBeingUploaded =
                phoneCalls.filter { it.uploadState == UploadState.BeingUploaded }.size

            state = state.copy(
                callsCountToday = callsCountToday,
                callsNotUploaded = callsNotUploaded,
                callsUploaded = callsUploaded,
                callsBeingUploaded = callsBeingUploaded,
                lastUpdateDate = "$hours:$minutes"
            )
        }
    }
}