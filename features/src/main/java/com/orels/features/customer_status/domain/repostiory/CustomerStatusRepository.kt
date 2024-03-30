package com.orels.features.customer_status.domain.repostiory

import com.orels.features.customer_status.domain.model.CustomerState

/**
 * Created by Orel Zilberman on 30/03/2024.
 */
interface CustomerStatusRepository {
    suspend fun getCustomerState(phoneNumber: String): CustomerState
}