package com.orels.features.customer_status.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.orels.domain.interactors.CallDetailsInteractor
import com.orels.domain.managers.phonecall.interactor.PhoneCallManagerInteractor
import com.orels.domain.model.entities.toPhoneCall
import com.orels.domain.util.common.Logger
import com.orels.domain.util.extension.log
import com.orels.features.customer_status.domain.repository.CustomerStatusRepository
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
    private val callDetailsInteractor: CallDetailsInteractor,
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
                    if (call == null) return@collectLatest
                    state = state.copy(callOnTheLine = call, isLoading = true, error = null)
                    try {
                        val customer = repository.getCustomerState(call.number)
                        val customerImage = callDetailsInteractor.getContactImage(call.number)
                        state = state.copy(
                            insurance = customer.personal.insurance.firstOrNull(),
                            name = customer.personal.personalDetails.name,
                            lastDive = customer.personal.lastDive,
                            finances = customer.finances,
                            customerState = customer,
                            image = customerImage,
                            isLoading = false
                        )
                        Logger.i(
                            "CustomerStateViewModel",
                            mapOf("customer" to Gson().toJson(customer))
                        )
                    } catch (e: Exception) {
                        state = state.copy(error = "הלקוח לא נמצא", isLoading = false)
                        e.log()
                    }
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