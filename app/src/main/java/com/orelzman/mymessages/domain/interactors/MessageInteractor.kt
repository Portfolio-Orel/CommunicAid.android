package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.Message
import kotlinx.coroutines.flow.Flow

interface MessageInteractor {
    fun getMessages(): Flow<List<Message>>
    suspend fun initMessagesAndMessagesInFolders(userId: String): List<Message>
    suspend fun createMessage(userId: String, message: Message, folderId: String)
    suspend fun getMessage(messageId: String): Message?
    suspend fun updateMessage(
        message: Message,
        oldFolderId: String? = null,
        newFolderId: String? = null
    )
    suspend fun deleteMessage(message: Message, folderId: String)
    suspend fun increaseTimesUsed(messageId: String)
}