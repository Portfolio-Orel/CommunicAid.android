package com.orels.features.customer_status.presentation

import com.orels.domain.model.entities.PhoneCall
import com.orels.features.customer_status.domain.model.CustomerState
import com.orels.features.customer_status.domain.model.Finances
import com.orels.features.customer_status.domain.model.Insurance
import com.orels.features.customer_status.domain.model.LastDive

/**
 * Created by Orel Zilberman on 30/03/2024.
 */
data class CustomerStateState(
    val customerState: CustomerState? = null,
    val isLoading: Boolean = true,
    val error: String? = null,

    val name: String? = null,
    val image: String? = null,
    val insurance: Insurance? = null,
    val lastDive: LastDive? = null,
    val finances: Finances? = null,
    val callOnTheLine: PhoneCall? = null,
)