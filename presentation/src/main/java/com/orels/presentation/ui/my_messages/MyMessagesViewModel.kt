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
import com.orels.domain.model.entities.SettingsKey
import com.orels.domain.model.entities.UserState
import com.orels.domain.system.connectivity.ConnectivityObserver
import com.orels.domain.system.connectivity.NetworkState
import com.orels.domain.util.common.Logger
import com.orels.domain.util.extension.log
import com.orels.domain.util.extension.safeCollectLatest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class MyMessagesViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val generalInteractor: GeneralInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val phoneCallManager: PhoneCallManager,
    private val workerManager: WorkerManager,
    private val connectivityObserver: ConnectivityObserver,
) : ViewModel() {
    var state by mutableStateOf(MyMessagesState())
    private var loadingData: Boolean = false

    init {
        observeUser()
        state = state.copy(isAuthenticated = isUserAuthenticated())
    }

    fun onResume() {
        initSettings()
    }

    private fun isUserAuthenticated(): Boolean {
        val isAuthorized = authInteractor.getUser()?.state == UserState.Authorized
        state = state.copy(isLoading = false, isAuthenticated = isAuthorized)
        return isAuthorized
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

    private fun isDataInit(): Boolean =
        settingsInteractor.getSettings(SettingsKey.IsDataInit).value.toBooleanStrictOrNull()
            ?: false

    private fun observeUser() {
        viewModelScope.launch(SupervisorJob()) {
            authInteractor.getUserFlow().safeCollectLatest({
                loadingData = false
                it.log()
            }) { user ->
                Logger.v("JWT: ${user?.token}")
                val isAuthenticated =
                    if (!authInteractor.isAuthorized(user)) {
                        false
                    } else {
                        if (loadingData) { // The data is being loaded and will return true once it's done
                            false
                        } else { // User is authenticated
                            if (!isDataInit()) { // Maybe replace and place this in login
                                loadingData = true
                                state = state.copy(isLoading = true)
                                withContext(NonCancellable) {
                                    try {
                                        generalInteractor.initData()
                                        loadingData = false
                                    } catch (e: Exception) {
                                        e.log()
                                        loadingData = false
                                        false
                                    }
                                }
                            }
                            workerManager.startWorker(WorkerType.RefreshToken)
                            true
                        }
                    }
                userAuthenticated(isAuthenticated = isAuthenticated)
                state = state.copy(isLoading = false, isAuthenticated = isAuthenticated)
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
    }
}