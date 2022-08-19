package com.orelzman.mymessages.presentation.my_messages

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.auth.domain.model.UserState
import com.orelzman.mymessages.domain.AuthConfigFile
import com.orelzman.mymessages.domain.interactors.GeneralInteractor
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.managers.phonecall.PhoneCallManager
import com.orelzman.mymessages.domain.managers.phonecall.isCallStateIdle
import com.orelzman.mymessages.domain.managers.worker.WorkerManager
import com.orelzman.mymessages.domain.managers.worker.WorkerType
import com.orelzman.mymessages.domain.model.entities.SettingsKey
import com.orelzman.mymessages.domain.service.PhonecallReceiver
import com.orelzman.mymessages.domain.util.extension.log
import com.orelzman.mymessages.domain.util.extension.safeCollectLatest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class MyMessagesViewModel @Inject constructor(
    application: Application,
    private val authInteractor: AuthInteractor,
    private val generalInteractor: GeneralInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val phoneCallManager: PhoneCallManager,
    private val workerManager: WorkerManager,
    @AuthConfigFile private val authConfigFile: Int,
) : AndroidViewModel(application) {
    var state by mutableStateOf(MyMessagesState())
    private var loadingData: Boolean = false

    init {
        observeUser()
        state = state.copy(isAuthenticated = isUserAuthenticated())
    }

    fun signOut() = viewModelScope.launch(Dispatchers.Main) {
        try {
            authInteractor.signOut()
            generalInteractor.clearAllDatabases()
        } catch (e: Exception) {
            e.log()
        }
    }

    private fun getApplicationContext(): Context =
        getApplication<Application>().applicationContext

    private fun isUserAuthenticated(): Boolean {
        val isAuthorized = authInteractor.getUser()?.state == UserState.Authorized
        state = state.copy(isLoading = false, isAuthenticated = isAuthorized)
        return isAuthorized
    }

    private fun isDataInit(): Boolean =
        settingsInteractor.getSettings(SettingsKey.IsDataInit)?.value?.toBooleanStrictOrNull()
            ?: false

    private fun observeUser() {
        viewModelScope.launch(SupervisorJob()) {
            authInteractor.getUserFlow().safeCollectLatest({ loadingData = false }) {
                authInteractor.init(authConfigFile)
                val isAuthenticated =
                    if (!authInteractor.isAuthorized(it)) {
                        false
                    } else {
                        if (loadingData) { // The data is being loaded and will return true once it's done
                            false
                        } else { // User is authenticated
                            if (!isDataInit()) {
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

    private fun userAuthenticated(isAuthenticated: Boolean = false) {
        if (isAuthenticated) {
            observeCallState()
            PhonecallReceiver.enable(context = getApplicationContext())
        } else {
            generalInteractor.clearAllDatabases()
            workerManager.clearAll()
            PhonecallReceiver.disable(context = getApplicationContext())
        }
    }
}