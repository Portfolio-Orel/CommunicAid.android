package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.Message
import kotlinx.coroutines.flow.Flow

interface MessageInteractor {
    fun getMessages(): Flow<List<Message>>
    suspend fun initMessagesAndMessagesInFolders(userId: String): List<Message>
    suspend fun createMessage(userId: String, message: Message, folderId: String)
    suspend fun getMessage(messageId: String): Message
    suspend fun updateMessage(
        userId: String,
        message: Message,
        oldFolderId: String,
        newFolderId: String
    )
    suspend fun deleteMessage(message: Message, folderId: String)
}