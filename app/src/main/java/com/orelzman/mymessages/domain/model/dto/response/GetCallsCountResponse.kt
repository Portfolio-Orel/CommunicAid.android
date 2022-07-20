package com.orelzman.mymessages.domain.model.dto.response

import com.google.gson.annotations.SerializedName

data class GetCallsCountResponse(
    @SerializedName("incoming_count") val incomingCount: Int = 0,
    @SerializedName("outgoing_count") val outgoingCount: Int = 0,
    @SerializedName("missed_count") val missedCount: Int = 0,
    @SerializedName("rejected_count") val rejectedCount: Int = 0,
)