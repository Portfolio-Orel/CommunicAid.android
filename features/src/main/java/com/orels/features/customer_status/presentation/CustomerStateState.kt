package com.orels.features.customer_status.presentation

import com.orels.domain.model.entities.PhoneCall
import com.orels.features.customer_status.domain.model.CustomerState

/**
 * Created by Orel Zilberman on 30/03/2024.
 */
data class CustomerStateState(
    val customerState: CustomerState? = null,
    val isLoading: Boolean = true,
    val error: String? = null,

    val callOnTheLine: PhoneCall? = null,
)