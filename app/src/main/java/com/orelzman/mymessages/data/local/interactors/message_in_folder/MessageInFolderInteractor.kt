package com.orelzman.mymessages.data.local.interactors.message_in_folder

import com.orelzman.mymessages.data.dto.MessageInFolder

interface MessageInFolderInteractor {
    suspend fun insert(messageInFolder: MessageInFolder)
    suspend fun insert(messagesInFolders: List<MessageInFolder>)
    suspend fun getMessagesInFolders(): List<MessageInFolder>
    suspend fun deleteMessageInFolder(messageInFolder: MessageInFolder)
}