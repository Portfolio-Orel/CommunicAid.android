package com.orelzman.mymessages.data.local.interactors.message_in_folder

import com.orelzman.mymessages.data.dto.MessageInFolder
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.dao.MessageInFolderDao
import javax.inject.Inject

class MessageInFolderInteractorImpl @Inject constructor(
    database: LocalDatabase,
) : MessageInFolderInteractor {

    private val db: MessageInFolderDao = database.messageInFolderDao

    override suspend fun insert(messageInFolder: MessageInFolder) {
        db.insert(messageInFolder)
    }

    override suspend fun insert(messagesInFolders: List<MessageInFolder>) {
        db.insert(messagesInFolders)
    }

    override suspend fun getMessagesInFolders(): List<MessageInFolder> =
        db.getMessageInFolders()


    override suspend fun deleteMessageInFolder(messageInFolder: MessageInFolder) {
        db.delete(messageInFolder)
    }

    override suspend fun getMessageFolderId(messageId: String): String =
        db.getWithMessageId(messageId)

    override suspend fun update(messageId: String, oldFolderId: String, newFolderId: String) {
        val messageInFolder = db.getWithMessageIdAndFolderId(messageId, oldFolderId)
        db.update(
            MessageInFolder(
                id = messageInFolder.id,
                messageId = messageId,
                folderId = newFolderId
            )
        )
    }


}