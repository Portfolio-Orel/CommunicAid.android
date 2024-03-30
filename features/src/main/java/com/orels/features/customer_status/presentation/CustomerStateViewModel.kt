package com.orels.features.customer_status.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.domain.managers.phonecall.interactor.PhoneCallManagerInteractor
import com.orels.domain.model.entities.toPhoneCall
import com.orels.domain.util.extension.log
import com.orels.features.customer_status.domain.repostiory.CustomerStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * Created by Orel Zilberman on 30/03/2024.
 */

@HiltViewModel
class CustomerStateViewModel @Inject constructor(
    private val repository: CustomerStatusRepository,
    private val phoneCallManagerInteractor: PhoneCallManagerInteractor,
) : ViewModel() {
    var state by mutableStateOf(CustomerStateState())
    private var callOnTheLineJob: Deferred<Unit>? = null

    init {
        observeNumberOnTheLine()
    }

    private fun observeNumberOnTheLine() {
        val callOnTheLine = phoneCallManagerInteractor.callsData.callOnTheLine?.toPhoneCall()
        state = state.copy(callOnTheLine = callOnTheLine)
        callOnTheLineJob = viewModelScope.async {
            phoneCallManagerInteractor.callsDataFlow.collectLatest {
                val call = it.callOnTheLine?.toPhoneCall()
                if (state.callOnTheLine != call) {
                    state = state.copy(callOnTheLine = call)
                    repository.getCustomerState(call?.number ?: "")
                }
            }
        }
        viewModelScope.launch(SupervisorJob()) {
            try {
                callOnTheLineJob?.await()
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    e.log()
                }
            }
        }
    }

    fun fetchDataFromAPI() {
//        viewModelScope.launch {
//            try {
//                // Assume the API has a method called fetchData()
////                val response = api.getCustomerState("1234567890", "Your_Token_Here")
//                _apiResponse.postValue(response.toString())
//            } catch (e: Exception) {
//                _apiResponse.postValue("Error: ${e.message}")
//            }
//        }
    }
}