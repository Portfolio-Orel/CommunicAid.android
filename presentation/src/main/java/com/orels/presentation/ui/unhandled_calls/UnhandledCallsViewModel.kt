package com.orels.presentation.ui.unhandled_calls

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
import com.orels.domain.interactors.*
import com.orels.domain.managers.unhandled_calls.UnhandledCallsManager
import com.orels.domain.model.entities.CallLogEntity
import com.orels.domain.model.entities.DeletedCall
import com.orels.domain.model.entities.PhoneCall
import com.orels.domain.model.entities.SettingsKey
import com.orels.domain.util.common.DateUtils.getStartOfDay
import com.orels.domain.util.common.Logger
import com.orels.domain.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnhandledCallsViewModel @Inject constructor(
    application: Application,
    private val callLogInteractor: CallDetailsInteractor,
    private val deletedCallsInteractor: DeletedCallsInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val unhandledCallsManager: UnhandledCallsManager,
    private val analyticsInteractor: AnalyticsInteractor
) : AndroidViewModel(application) {

    var state by mutableStateOf(UnhandledCallsState())

    var isRefreshing by mutableStateOf(false)

    init {
        analyticsInteractor.track(AnalyticsIdentifiers.UnhandledCallsShow)
        initData()
        observeDeletedCalls()
    }

    fun refresh(isPullToRefresh: Boolean = false) {
        if(isPullToRefresh) {
            analyticsInteractor.track(AnalyticsIdentifiers.PullToRefreshUnhandledCalls)
        }
        isRefreshing = isPullToRefresh
        if (isPullToRefresh) {
            fetchDeletedCalls()
        } else {
            val callsToHandle = unhandledCallsManager.filterUnhandledCalls(
                deletedCalls = deletedCallsInteractor.getAllOnce(getStartOfDay()),
                callLogs = getCallsFromCallLog(),
                countRejectedAsUnhandled = isCountRejectedAsUnhandled()
            ).withContactDetails()
            state = state.copy(callsToHandle = callsToHandle, isLoading = false)
        }
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
        val canDeleteCalls: Boolean =
            settingsInteractor.getSettings(SettingsKey.CanDeleteUnhandledCalls).getRealValue()
                ?: true
        val callsToHandle = unhandledCallsManager.filterUnhandledCalls(
            deletedCalls = deletedCallsInteractor.getAllOnce(getStartOfDay()),
            callLogs = getCallsFromCallLog(),
            countRejectedAsUnhandled = isCountRejectedAsUnhandled()
        ).withContactDetails()
        state = state.copy(
            callsToHandle = callsToHandle,
            canDeleteCalls = canDeleteCalls,
            isLoading = false
        )
    }

    private fun observeDeletedCalls() {
        viewModelScope.launch(Dispatchers.Main) {
            deletedCallsInteractor.getAll(getStartOfDay())
                .collectLatest {
                    val callsToHandle = unhandledCallsManager.filterUnhandledCalls(
                        deletedCalls = it,
                        callLogs = getCallsFromCallLog(),
                        countRejectedAsUnhandled = isCountRejectedAsUnhandled()
                    ).withContactDetails()
                    state = state.copy(callsToHandle = callsToHandle, isLoading = false)
                }
        }
    }

    /**
     * Fills in the caller identification (contact name and photo) for every call.
     * Falls back to the number / no photo when the number has no matching contact.
     */
    private fun List<CallLogEntity>.withContactDetails(): List<CallLogEntity> = onEach { call ->
        // getContactName() returns the number itself when there is no matching contact; in that
        // case keep the name cached in the call log (if any) instead of overwriting it.
        val contactName = callLogInteractor.getContactName(call.number)
        if (contactName != call.number || call.name.isBlank()) {
            call.name = contactName
        }
        if (call.photoUri.isBlank()) {
            // Private / unknown numbers can be rejected by the contacts provider
            call.photoUri = runCatching { callLogInteractor.getContactImage(call.number) }
                .getOrDefault("")
        }
    }

    private fun isCountRejectedAsUnhandled(): Boolean =
        settingsInteractor.getSettings(SettingsKey.CountRejectedAsUnhandled).getRealValue()
            ?: true

    /**
     * Sets all the calls that were not handled by the user and might require his attention.
     */
    private fun fetchDeletedCalls() {
        val job = viewModelScope.async {
            deletedCallsInteractor.init(getStartOfDay())
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                job.await()
            } catch (e: Exception) {
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