package com.orels.domain.interactors

import com.orels.domain.model.entities.MessageInFolder
import kotlinx.coroutines.flow.Flow

interface MessageInFolderInteractor {
    fun getMessagesInFolders(): Flow<List<MessageInFolder>>
    fun getMessagesInFoldersOnce(): List<MessageInFolder>
    fun getMessageFolderId(messageId: String): String?
    fun clear()
    suspend fun insert(messageInFolder: MessageInFolder)
    suspend fun insert(messagesInFolders: List<MessageInFolder>)
    suspend fun delete(messageInFolder: MessageInFolder)
    suspend fun deleteMessagesFromFolder(folderId: String)
    suspend fun update(messageId: String, oldFolderId: String, newFolderId: String)
    suspend fun restore(folderId: String)
}