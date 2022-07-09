package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.MessageInFolder
import kotlinx.coroutines.flow.Flow

interface MessageInFolderInteractor {
    fun getMessagesInFolders(): Flow<List<MessageInFolder>>
    suspend fun insert(messageInFolder: MessageInFolder)
    suspend fun insert(messagesInFolders: List<MessageInFolder>)
    suspend fun deleteMessageInFolder(messageInFolder: MessageInFolder)
    suspend fun deleteMessagesFromFolder(folderId: String)
    suspend fun getMessageFolderId(messageId: String): String
    suspend fun update(messageId: String, oldFolderId: String, newFolderId: String)
}