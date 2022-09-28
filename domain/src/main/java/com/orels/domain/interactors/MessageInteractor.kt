package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.Message
import kotlinx.coroutines.flow.Flow

interface MessageInteractor {
    fun getMessages(isActive: Boolean = true): Flow<List<Message>>
    fun getAllOnce(isActive: Boolean = true): List<Message>
    fun getMessage(messageId: String): Message?
    suspend fun initWithMessagesInFolders(): List<Message>
    suspend fun createMessage(message: Message, folderId: String)
    suspend fun updateMessage(
        message: Message,
        oldFolderId: String? = null,
        newFolderId: String? = null
    )
    suspend fun deleteMessage(message: Message)
    suspend fun increaseTimesUsed(messageId: String)
}