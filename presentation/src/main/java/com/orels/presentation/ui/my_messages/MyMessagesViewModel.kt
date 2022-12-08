package com.orels.presentation.ui.my_messages

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.auth.domain.interactor.AuthInteractor
import com.orels.domain.interactors.SettingsInteractor
import com.orels.domain.managers.phonecall.PhoneCallManager
import com.orels.domain.managers.phonecall.isCallStateIdle
import com.orels.domain.managers.worker.WorkerManager
import com.orels.domain.managers.worker.WorkerType
import com.orels.domain.system.connectivity.ConnectivityObserver
import com.orels.domain.system.connectivity.NetworkState
import com.orels.domain.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyMessagesViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val phoneCallManager: PhoneCallManager,
    private val workerManager: WorkerManager,
    private val connectivityObserver: ConnectivityObserver,
) : ViewModel() {
    var state by mutableStateOf(MyMessagesState())
        private set

    init {
        isUserAuthenticated()
    }

    private fun isUserAuthenticated() {
        val userAuthenticatedJob = viewModelScope.async {
            userAuthenticated(authInteractor.isLoggedIn())
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
        CoroutineScope(SupervisorJob()).launch {
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

    private fun userAuthenticated(isAuthenticated: Boolean = false) {
        if (isAuthenticated) {
            observeCallState()
            observeInternetConnectivity()
        } else {
            workerManager.clearAll()
        }
        state = state.copy(isAuthenticated = isAuthenticated)
    }
}