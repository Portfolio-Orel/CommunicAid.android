package com.orels.data.interactor

import com.orels.data.local.LocalDatabase
import com.orels.data.local.dao.MessageInFolderDao
import com.orels.domain.interactors.MessageInFolderInteractor
import com.orels.domain.model.entities.MessageInFolder
import com.orels.domain.repository.Repository
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

    override fun getMessagesInFoldersOnce(): List<MessageInFolder> = db.getOnce()

    override fun clear() =
        db.clear()


    override suspend fun delete(messageInFolder: MessageInFolder) {
        db.delete(folderId = messageInFolder.folderId, messageId = messageInFolder.messageId)
    }

    override suspend fun deleteMessagesFromFolder(folderId: String) {
        repository.deleteMessagesFromFolder(folderId = folderId)
        db.delete(folderId)
    }

    override fun getMessageFolderId(messageId: String): String? =
        db.getWithMessageId(messageId)

    override suspend fun update(messageId: String, oldFolderId: String, newFolderId: String) {
        db.delete(messageId = messageId, folderId = oldFolderId)
        db.insert(MessageInFolder(messageId = messageId, folderId = newFolderId, isActive = true))
    }

    override suspend fun restore(folderId: String) {
        db.restore(folderId)
    }


}