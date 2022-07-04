package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.MessageInFolder

interface MessageInFolderInteractor {
    suspend fun insert(messageInFolder: MessageInFolder)
    suspend fun insert(messagesInFolders: List<MessageInFolder>)
    suspend fun getMessagesInFolders(): List<MessageInFolder>
    suspend fun deleteMessageInFolder(messageInFolder: MessageInFolder)
    suspend fun getMessageFolderId(messageId: String): String
    suspend fun update(messageId: String, oldFolderId: String, newFolderId: String)
}