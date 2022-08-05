package com.orelzman.mymessages.presentation.my_messages

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.domain.interactors.GeneralInteractor
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.managers.worker.WorkerManager
import com.orelzman.mymessages.domain.model.entities.SettingsKeys
import com.orelzman.mymessages.util.extension.log
import com.orelzman.mymessages.util.extension.safeCollectLatest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MyMessagesViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val generalInteractor: GeneralInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val workerManager: WorkerManager

    ) : ViewModel() {
    var state by mutableStateOf(MyMessagesState())
    private var loadingData: Boolean = false

    init {

        viewModelScope.launch {
            authInteractor.isUserAuthenticated().safeCollectLatest({ loadingData = false }) {
                // will not kill the collectLatest if an error is thrown
                supervisorScope {
                    val isAuthorized = if(!authInteractor.isAuthorized(it)) {
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
                                    } catch (ex: Exception) {
                                        ex.log()
                                        authInteractor.signOut()
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

    private fun isDataInit(): Boolean =
        settingsInteractor.getSettings(SettingsKeys.IsDataInit)?.value?.toBooleanStrictOrNull()
            ?: false

    fun signOut() = viewModelScope.launch(Dispatchers.Main) {
        try {
            authInteractor.signOut()
        } catch (e: Exception) {
            e.log()
        }
    }
}