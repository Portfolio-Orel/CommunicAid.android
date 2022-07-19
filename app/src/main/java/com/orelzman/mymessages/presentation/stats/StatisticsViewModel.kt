package com.orelzman.mymessages.presentation.stats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.domain.interactors.CallLogInteractor
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.interactors.StatisticsInteractor
import com.orelzman.mymessages.domain.model.entities.UploadState
import com.orelzman.mymessages.domain.model.entities.toPhoneCalls
import com.orelzman.mymessages.util.common.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val phoneCallsInteractor: PhoneCallsInteractor,
    private val authInteractor: AuthInteractor,
    private val callLogInteractor: CallLogInteractor,
    private val statisticsInteractor: StatisticsInteractor
    ) : ViewModel() {
    var state by mutableStateOf(StatisticsState())

    var isRefreshing by mutableStateOf(false)

    init {
        setData()
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
        isRefreshing = true
        setData()
    }

    private fun setData() {
        viewModelScope.launch(Dispatchers.IO) {
            val startOfTodayDate = DateUtils.getStartOfDay()
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
            val phoneCalls = phoneCallsInteractor.getAllFromDate(startOfTodayDate)
            val callsCountToday = callLogInteractor.getTodaysCallLog().size
            val callsNotUploaded =
                phoneCalls.filter { it.uploadState == UploadState.NotUploaded }.size
            val callsUploaded = phoneCalls.filter { it.uploadState == UploadState.Uploaded }.size
            val callsBeingUploaded =
                phoneCalls.filter { it.uploadState == UploadState.BeingUploaded }.size

            val callsCount = statisticsInteractor.getCallsCountByType()
            val messageSent = statisticsInteractor.getMessagesSentCount()

            state = state.copy(
                callsCountToday = callsCountToday,
                callsNotUploaded = callsNotUploaded,
                callsUploaded = callsUploaded,
                callsBeingUploaded = callsBeingUploaded,
                callsCount = callsCount,
                messageSent = messageSent,
                lastUpdateDate = "$hours:$minutes"
            )
            isRefreshing = false
        }
    }
}