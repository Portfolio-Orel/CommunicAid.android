package com.orelzman.mymessages.data.local.interactors.message

import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.data.remote.repository.dto.CreateMessageBody

interface MessageInteractor {
    suspend fun getMessages(userId: String): List<Message>
    suspend fun createMessage(createMessageBody: CreateMessageBody): String
    suspend fun getMessage(messageId: String): Message
    suspend fun editMessage(
        userId: String,
        message: Message,
        oldFolderId: String,
        newFolderId: String
    )
}