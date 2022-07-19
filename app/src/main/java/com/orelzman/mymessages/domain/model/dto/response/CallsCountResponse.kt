package com.orelzman.mymessages.domain.model.dto.response

import com.google.gson.annotations.SerializedName

data class CallsCountResponse(
    @SerializedName("incoming_count") val incomingCount: Int,
    @SerializedName("outgoing_count") val outgoingCount: Int,
    @SerializedName("missed_count") val missedCount: Int,
    @SerializedName("rejected_count") val rejectedCount: Int,
)