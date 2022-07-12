package com.orelzman.mymessages.presentation.stats

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.model.entities.UploadState
import com.orelzman.mymessages.domain.model.entities.toPhoneCalls
import com.orelzman.mymessages.util.common.CallUtils
import com.orelzman.mymessages.util.common.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val phoneCallsInteractor: PhoneCallsInteractor,
    private val authInteractor: AuthInteractor
    ) : ViewModel() {
    var state by mutableStateOf(StatsState())

    val isRefreshing = MutableSharedFlow<Boolean>()


    fun sendCallLogs(context: Context) {
        state = state.copy(isLoadingCallLogSend = true)
        val callLogs = CallUtils.getTodaysCallLog(context)
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

    fun refreshData(context: Context) {
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
            val callsCountToday = CallUtils.getTodaysCallLog(context).size
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