package com.orelzman.mymessages.domain.model.dto.response

import com.google.gson.annotations.SerializedName

data class MessagesSentCountResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("title") val title: String,
)