package com.orels.features.customer_state.domain.repostiory

import com.orels.features.customer_state.domain.model.CustomerState

/**
 * Created by Orel Zilberman on 30/03/2024.
 */
interface CustomerStateRepository {
    suspend fun getCustomerState(phoneNumber: String): CustomerState
}