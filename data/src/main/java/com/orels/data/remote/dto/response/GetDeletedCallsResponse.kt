package com.orelzman.mymessages.data.remote.dto.response

import com.google.gson.annotations.SerializedName
import java.util.*

data class GetDeletedCallsResponse(
    @SerializedName("id") val id: String,
    @SerializedName("number") val number: String,
    @SerializedName("deleted_at") val deleteDate: Date
)