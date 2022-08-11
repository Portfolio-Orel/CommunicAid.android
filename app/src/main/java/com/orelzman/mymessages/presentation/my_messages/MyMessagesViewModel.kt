package com.orelzman.mymessages.presentation.my_messages

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.auth.domain.model.UserState
import com.orelzman.mymessages.domain.AuthConfigFile
import com.orelzman.mymessages.domain.interactors.GeneralInteractor
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.managers.worker.WorkerManager
import com.orelzman.mymessages.domain.model.entities.SettingsKey
import com.orelzman.mymessages.domain.util.extension.log
import com.orelzman.mymessages.domain.util.extension.safeCollectLatest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MyMessagesViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val generalInteractor: GeneralInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val workerManager: WorkerManager,
    @AuthConfigFile private val authConfigFile: Int,
) : ViewModel() {
    var state by mutableStateOf(MyMessagesState())
    private var loadingData: Boolean = false

    init {
        viewModelScope.launch {
            state = if(authInteractor.getUser()?.state == UserState.Authorized) {
                state.copy(isLoading = false, isAuthorized = true)
            } else {
                state.copy(isLoading = false, isAuthorized = false)
            }
            authInteractor.init(authConfigFile)
            authInteractor.getUserFlow().safeCollectLatest({ loadingData = false }) {
                // will not kill the collectLatest if an error is thrown
                supervisorScope {
                    val isAuthorized =
                        if (!authInteractor.isAuthorized(it)) {
                            generalInteractor.clearAllDatabases()
                            workerManager.clearAll()
                            false
                        } else {
                            if (loadingData) { // The data is being loaded and will return true once it's done
                                false
                            } else {
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
                    state = state.copy(isLoading = false, isAuthorized = isAuthorized)
                }
            }
        }
    }

    fun signOut() = viewModelScope.launch(Dispatchers.Main) {
        try {
            authInteractor.signOut()
        } catch (e: Exception) {
            e.log()
        }
    }

    private fun isDataInit(): Boolean =
        settingsInteractor.getSettings(SettingsKey.IsDataInit)?.value?.toBooleanStrictOrNull()
            ?: false
}