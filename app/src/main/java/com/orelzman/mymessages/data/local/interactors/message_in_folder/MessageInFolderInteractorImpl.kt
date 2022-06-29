package com.orelzman.mymessages.data.local.interactors.message_in_folder

import com.orelzman.mymessages.data.dto.MessageInFolder
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.dao.MessageInFolderDao
import com.orelzman.mymessages.data.remote.repository.Repository
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

    override suspend fun getMessagesInFolders(): List<MessageInFolder> =
        db.getMessageInFolders()


    override suspend fun deleteMessageInFolder(messageInFolder: MessageInFolder) {
        db.delete(messageInFolder)
    }

}