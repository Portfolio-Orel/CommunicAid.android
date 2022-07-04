package com.orelzman.mymessages.domain.model.dto.response

import com.google.gson.annotations.SerializedName

data class GetDeletedCallsResponse(
    @SerializedName("id") val id: String,
    @SerializedName("number") val number: String,
    @SerializedName("deleted_at_date") val deleteDate: Long
)