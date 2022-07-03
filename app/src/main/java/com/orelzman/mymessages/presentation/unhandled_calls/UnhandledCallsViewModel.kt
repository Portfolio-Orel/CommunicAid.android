package com.orelzman.mymessages.presentation.unhandled_calls

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.dto.DeletedUnhandledCalls
import com.orelzman.mymessages.data.dto.PhoneCall
import com.orelzman.mymessages.data.local.interactors.unhandled_calls.UnhandledCallsInteractor
import com.orelzman.mymessages.domain.model.CallLogEntity
import com.orelzman.mymessages.util.CallLogUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnhandledCallsViewModel @Inject constructor(
    application: Application,
    private val unhandledCallsInteractor: UnhandledCallsInteractor,
    private val authInteractor: AuthInteractor,
) : AndroidViewModel(application) {

    var state by mutableStateOf(UnhandledCallsState())


    init {
        setCalls()
    }

    /**
     * Sets all the calls that were not handled by the user and might require his attention.
     */
    private fun setCalls() {
        viewModelScope.launch(Dispatchers.IO) {
            authInteractor.getUser()?.userId?.let {
                val unhandledCalls = unhandledCallsInteractor.getAll(it)
                    .sortedByDescending { unhandledCall -> unhandledCall.phoneCall.startDate }
                    .distinctBy { unhandledCall -> unhandledCall.phoneCall.startDate }
                val callsFromCallLog = getCallsFromCallLog(context = getApplicationContext())
                val callsToHandle = unhandledCallsInteractor.filterUnhandledCalls(
                    deletedUnhandledCalls = unhandledCalls,
                    callLogs = callsFromCallLog
                )
                state = state.copy(callsToHandle = callsToHandle)
            }
        }
    }

    /**
     * Deletes [phoneCall] as marks it as deletedUnhandled
     */
    fun onDelete(phoneCall: PhoneCall) {
        viewModelScope.launch(Dispatchers.IO) {
            authInteractor.getUser()?.userId?.let {
                unhandledCallsInteractor.insert(
                    uid = it, deletedUnhandledCalls = DeletedUnhandledCalls(phoneCall = phoneCall)
                )
            }
            setCalls()
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
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(getApplicationContext(), intent, Bundle())
        }
    }

    private fun getApplicationContext(): Context =
        getApplication<Application>().applicationContext


    private fun getCallsFromCallLog(context: Context): ArrayList<CallLogEntity> =
        CallLogUtils.getTodaysCallLog(context)
}