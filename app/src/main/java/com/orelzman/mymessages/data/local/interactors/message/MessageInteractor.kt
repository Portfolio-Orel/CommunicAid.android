package com.orelzman.mymessages.data.local.interactors.message

import com.orelzman.mymessages.data.dto.Message

interface MessageInteractor {
    suspend fun getMessagesWithFolders(userId: String): List<Message>
    suspend fun createMessage(userId: String, message: Message, folderId: String): String
    suspend fun getMessage(messageId: String): Message
    suspend fun editMessage(
        userId: String,
        message: Message,
        oldFolderId: String,
        newFolderId: String
    )
}