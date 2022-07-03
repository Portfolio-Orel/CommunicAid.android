package com.orelzman.mymessages.data.dto

import com.orelzman.mymessages.data.remote.repository.dto.body.create.CreateMessageSentBody

data class MessageSent(
    val sentAt: Long,
    val messageId: String
) {
    val createMessageSentBody: CreateMessageSentBody
        get() = CreateMessageSentBody(sentAt = sentAt, messageId = messageId)
}