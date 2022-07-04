package com.orelzman.mymessages.domain.model.dto.body.create

import com.google.gson.annotations.SerializedName

data class CreateMessageSentBody(
    @SerializedName("sent_at") val sentAt: Long,
    @SerializedName("message_id") val messageId: String
)