package com.orels.features.customer_status.data.remote

import com.orels.features.customer_status.domain.model.CustomerState
import com.orels.features.customer_status.domain.repository.CustomerStatusRepository
import javax.inject.Inject

/**
 * Created by Orel Zilberman on 30/03/2024.
 */
class CustomerStatusRepositoryImpl @Inject constructor(
    private val api: API
) : CustomerStatusRepository {
    override suspend fun getCustomerState(phoneNumber: String): CustomerState {
        return api.getCustomerState(phoneNumber)
    }

}
