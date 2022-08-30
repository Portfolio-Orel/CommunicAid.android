package com.orelzman.mymessages.domain.model.entities

import com.orelzman.mymessages.data.remote.dto.body.create.CreateMessageSentBody

data class MessageSent(
    val sentAt: Long,
    val messageId: String
) : Loggable {
    val createMessageSentBody: CreateMessageSentBody
        get() = CreateMessageSentBody(sentAt = sentAt, messageId = messageId)
    override val data: Map<String, Any>
        get() = mapOf(
            "sent_at" to sentAt,
            "message_id" to messageId
        )
}