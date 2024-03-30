package com.orels.features.customer_state.data.remote

import com.orels.features.customer_state.domain.model.CustomerState
import com.orels.features.customer_state.domain.repostiory.CustomerStateRepository
import javax.inject.Inject

/**
 * Created by Orel Zilberman on 30/03/2024.
 */
class CustomerStateRepositoryImpl @Inject constructor(
    private val api: API
) : CustomerStateRepository {
    override suspend fun getCustomerState(phoneNumber: String): CustomerState {
        return api.getCustomerState(phoneNumber)
    }

}
