package com.orelzman.mymessages.data.local.interactors.message

import com.orelzman.mymessages.data.dto.Message

interface MessageInteractor {
    suspend fun getMessages(uid: String): List<Message>
    suspend fun saveMessage(uid: String, message: Message, folderId: String): String
    suspend fun getMessage(messageId: String): Message
    suspend fun editMessage(
        uid: String,
        message: Message,
        oldFolderId: String,
        newFolderId: String
    )
}