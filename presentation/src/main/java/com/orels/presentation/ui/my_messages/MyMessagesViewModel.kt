package com.orels.presentation.ui.my_messages

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.domain.interactors.AuthInteractor
import com.orels.domain.interactors.GeneralInteractor
import com.orels.domain.interactors.SettingsInteractor
import com.orels.domain.managers.phonecall.PhoneCallManager
import com.orels.domain.managers.phonecall.isCallStateIdle
import com.orels.domain.managers.worker.WorkerManager
import com.orels.domain.managers.worker.WorkerType
import com.orels.domain.model.entities.UserState
import com.orels.domain.system.connectivity.ConnectivityObserver
import com.orels.domain.system.connectivity.NetworkState
import com.orels.domain.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class MyMessagesViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val phoneCallManager: PhoneCallManager,
    private val workerManager: WorkerManager,
    private val connectivityObserver: ConnectivityObserver,
    private val generalInteractor: GeneralInteractor,
) : ViewModel() {
    var state by mutableStateOf(MyMessagesState())
        private set

    init {
        observeUserAuthentication()
    }

    private fun observeUserAuthentication() {
        state = state.copy(isLoading = true)
        val userAuthenticatedJob = viewModelScope.async {
            authInteractor.getUserState().collectLatest { userState ->
                withContext(Dispatchers.Main) {
                    userAuthenticated(userState = userState)
                }
            }
        }
        viewModelScope.launch {
            try {
                userAuthenticatedJob.await()
            } catch (e: Exception) {
                e.log()
            }
        }
    }

    /**
     * Make sure settings that are applied to the user by the admin
     * are applied in the app as well.
     */
    private fun initSettings() {
        val fetchSettingsJob = viewModelScope.async { settingsInteractor.init() }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                fetchSettingsJob.await()
            } catch (e: Exception) {
                e.log()
            }
        }
    }

    private fun observeCallState() {
        viewModelScope.launch(SupervisorJob()) {
            try {
                phoneCallManager.callsDataFlow.collectLatest {
                    if (it.callState.isCallStateIdle()) {
                        workerManager.startWorker(WorkerType.UploadCallsOnce)
                    }
                }
            } catch (e: Exception) {
                e.log()
            }
        }
    }

    private fun observeInternetConnectivity() {
        viewModelScope.launch(SupervisorJob()) {
            connectivityObserver.observe().collectLatest { value: NetworkState ->
                when (value) {
                    NetworkState.Available -> workerManager.startWorker(WorkerType.UploadNotUploadedObjectsOnce)
                    else -> {}
                }
            }
        }
    }

    private fun userAuthenticated(userState: UserState) {
        if (userState == UserState.Loading) return
        if (userState == UserState.Authorized) {
            observeCallState()
            observeInternetConnectivity()
            val initDataJob = viewModelScope.async { generalInteractor.initData() }
            viewModelScope.launch {
                try {
                    initDataJob.await()
                } catch (e: Exception) {
                    e.log()
                }
            }
        } else if (userState == UserState.NotAuthorized) {
            workerManager.clearAll()
            generalInteractor.clearAllDatabases()
        }
        state = state.copy(authState = userState, isLoading = false)
    }
}