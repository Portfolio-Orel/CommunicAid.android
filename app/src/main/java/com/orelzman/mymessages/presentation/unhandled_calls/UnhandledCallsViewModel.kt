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
import com.orelzman.mymessages.domain.interactors.CallLogInteractor
import com.orelzman.mymessages.domain.interactors.DeletedCallsInteractor
import com.orelzman.mymessages.domain.managers.UnhandledCallsManager
import com.orelzman.mymessages.domain.model.entities.CallLogEntity
import com.orelzman.mymessages.domain.model.entities.DeletedCall
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.util.extension.Log
import com.orelzman.mymessages.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnhandledCallsViewModel @Inject constructor(
    application: Application,
    private val deletedCallsInteractor: DeletedCallsInteractor,
    private val unhandledCallsManager: UnhandledCallsManager,
    private val callLogInteractor: CallLogInteractor,
    private val authInteractor: AuthInteractor,
) : AndroidViewModel(application) {

    var state by mutableStateOf(UnhandledCallsState())

    val isRefreshing = MutableSharedFlow<Boolean>()

    init {
        setCalls()
    }

    fun refresh() {
        setCalls()
    }

    /**
     * Sets all the calls that were not handled by the user and might require his attention.
     */
    private fun setCalls() {
        val job = viewModelScope.async {
            authInteractor.getUser()?.userId?.let { userId ->
                deletedCallsInteractor.getAll(userId)
                    .collect {
                        val callsFromCallLog =
                            getCallsFromCallLog()
                        val callsToHandle = unhandledCallsManager.filterUnhandledCalls(
                            deletedCalls = it,
                            callLogs = callsFromCallLog
                        )
                        state = state.copy(callsToHandle = callsToHandle)
                    }
            }
        }
        viewModelScope.launch(Dispatchers.Main) {
            try {
                job.await()
            } catch (e: Exception) {
                e.log()
                Log.eCustom(e.message ?: "Failed to get unhandled calls")
            }
        }
    }

    /**
     * Deletes [phoneCall] as marks it as deletedUnhandled
     */
    fun onDelete(phoneCall: PhoneCall) {
        viewModelScope.launch(Dispatchers.IO) {
            authInteractor.getUser()?.userId?.let {
                try {
                    deletedCallsInteractor.create(
                        userId = it, deletedCall = DeletedCall(
                            number = phoneCall.number
                        )
                    )
                } catch (exception: Exception) {
                    exception.log()
                }
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
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(getApplicationContext(), intent, Bundle())
        }
    }

    private fun getApplicationContext(): Context =
        getApplication<Application>().applicationContext


    private fun getCallsFromCallLog(): ArrayList<CallLogEntity> =
        callLogInteractor.getTodaysCallLog()
}