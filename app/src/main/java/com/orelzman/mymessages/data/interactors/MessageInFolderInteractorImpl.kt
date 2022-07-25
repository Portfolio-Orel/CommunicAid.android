package com.orelzman.mymessages.data.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.dao.MessageInFolderDao
import com.orelzman.mymessages.domain.interactors.MessageInFolderInteractor
import com.orelzman.mymessages.domain.model.entities.MessageInFolder
import com.orelzman.mymessages.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageInFolderInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : MessageInFolderInteractor {

    private val db: MessageInFolderDao = database.messageInFolderDao

    override suspend fun insert(messageInFolder: MessageInFolder) {
        db.insert(messageInFolder)
    }

    override suspend fun insert(messagesInFolders: List<MessageInFolder>) {
        db.insert(messagesInFolders)
    }

    override fun getMessagesInFolders(): Flow<List<MessageInFolder>> =
        db.get()


    override suspend fun delete(messageInFolder: MessageInFolder) {
        db.delete(messageInFolder)
    }

    override suspend fun deleteMessagesFromFolder(folderId: String) {
        repository.deleteMessagesFromFolder(folderId = folderId)
        db.delete(folderId)
    }

    override suspend fun getMessageFolderId(messageId: String): String? =
        db.getWithMessageId(messageId)

    override suspend fun update(messageId: String, oldFolderId: String, newFolderId: String) {
        db.get(messageId, oldFolderId)
        db.update(
            MessageInFolder(
                messageId = messageId,
                folderId = newFolderId
            )
        )
    }


}