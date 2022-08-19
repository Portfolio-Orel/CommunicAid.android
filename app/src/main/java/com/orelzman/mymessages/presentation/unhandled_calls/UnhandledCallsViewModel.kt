package com.orelzman.mymessages.presentation.unhandled_calls

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.mymessages.domain.interactors.CallLogInteractor
import com.orelzman.mymessages.domain.interactors.DeletedCallsInteractor
import com.orelzman.mymessages.domain.managers.unhandled_calls.UnhandledCallsManager
import com.orelzman.mymessages.domain.model.entities.CallLogEntity
import com.orelzman.mymessages.domain.model.entities.DeletedCall
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.domain.util.common.DateUtils.getStartOfDay
import com.orelzman.mymessages.domain.util.extension.Logger
import com.orelzman.mymessages.domain.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnhandledCallsViewModel @Inject constructor(
    application: Application,
    private val deletedCallsInteractor: DeletedCallsInteractor,
    private val unhandledCallsManager: UnhandledCallsManager,
    private val callLogInteractor: CallLogInteractor,
) : AndroidViewModel(application) {

    var state by mutableStateOf(UnhandledCallsState())

    var isRefreshing by mutableStateOf(false)

    init {
        initData()
        observeDeletedCalls()
    }

    fun refresh() {
        isRefreshing = true
        fetchDeletedCalls()
    }

    fun onResume() {
        initData()
    }

    /**
     * Deletes [phoneCall] as marks it as deletedUnhandled
     */
    fun onDelete(phoneCall: PhoneCall) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deletedCallsInteractor.create(
                    deletedCall = DeletedCall(
                        number = phoneCall.number
                    )
                )
            } catch (e: Exception) {
                e.log()
            }
        }
    }

    /**
     * Start a phone call to [phoneCall]
     */
    fun onCall(phoneCall: PhoneCall, context: Context? = null) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${phoneCall.number}"))
        if (context != null) {
            context.startActivity(intent)
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(getApplicationContext(), intent, Bundle())
        }
    }

    private fun initData() {
        state = state.copy(isLoading = true)
        val callsToHandle = unhandledCallsManager.filterUnhandledCalls(
            deletedCalls = deletedCallsInteractor.getAllOnce(getStartOfDay()),
            callLogs = getCallsFromCallLog()
        )
        state = state.copy(callsToHandle = callsToHandle, isLoading = false)
    }

    private fun observeDeletedCalls() {
        viewModelScope.launch(Dispatchers.Main) {
            deletedCallsInteractor.getAll(getStartOfDay())
                .collect {
                    val callsToHandle = unhandledCallsManager.filterUnhandledCalls(
                        deletedCalls = it,
                        callLogs = getCallsFromCallLog()
                    )
                    state = state.copy(callsToHandle = callsToHandle, isLoading = false)
                }
        }
    }

    /**
     * Sets all the calls that were not handled by the user and might require his attention.
     */
    private fun fetchDeletedCalls() {
        val job = viewModelScope.async {
            deletedCallsInteractor.init()
        }
        viewModelScope.launch(Dispatchers.Main) {
            try {
                job.await()
            } catch (e: Exception) {
                e.log()
                Logger.e(e.message ?: "Failed to get unhandled calls")
            } finally {
                isRefreshing = false
            }
        }
    }

    private fun getApplicationContext(): Context =
        getApplication<Application>().applicationContext

    private fun getCallsFromCallLog(): ArrayList<CallLogEntity> =
        callLogInteractor.getTodaysCallLog()
}