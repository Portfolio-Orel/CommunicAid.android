package com.orels.domain.model.dto.body.create

import com.google.gson.annotations.SerializedName

/**
 * Created by Orel Zilberman on 16/07/2024.
 */

data class CreateOngoingCallBody(
    @SerializedName("number") val number: String,
    @SerializedName("contact_name") val contactName: String,
    @SerializedName("start_date") val startDate: Long,
)