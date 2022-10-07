package com.orels.domain.model.entities

import com.orels.domain.model.dto.body.create.CreateMessageSentBody

data class MessageSent(
    var sentAt: Long,
    var messageId: String
) : Loggable {
    val createMessageSentBody: CreateMessageSentBody
        get() = CreateMessageSentBody(sentAt = sentAt, messageId = messageId)
    override val data: Map<String, Any>
        get() = mapOf(
            "sent_at" to sentAt,
            "message_id" to messageId
        )
}