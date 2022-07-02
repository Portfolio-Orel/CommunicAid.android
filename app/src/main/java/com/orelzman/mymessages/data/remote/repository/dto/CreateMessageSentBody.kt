package com.orelzman.mymessages.data.remote.repository.dto

import com.google.gson.annotations.SerializedName

data class CreateMessageSentBody(
    @SerializedName("sent_at") val sentAt: Long,
    @SerializedName("message_id") val messageId: String
)