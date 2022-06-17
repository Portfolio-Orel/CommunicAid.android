package com.orelzman.mymessages.data.local.interactors.message

import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.data.dto.messages
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.interactors.folder.FolderInteractor
import com.orelzman.mymessages.data.remote.repository.Repository
import javax.inject.Inject

class MessageInteractorImpl @Inject constructor(
    private val repository: Repository,
    private val folderInteractor: FolderInteractor,
    database: LocalDatabase,
) : MessageInteractor {

    private val db = database.messageDao

    override suspend fun getMessages(uid: String): List<Message> {
        var messages = db.getMessages()
        if (messages.isEmpty()) {
            messages = repository.getMessages(uid).messages
            db.insert(messages)
        }
        return messages
    }

    override suspend fun saveMessage(uid: String, message: Message, folderId: String): String {
        val messageId = repository.saveMessage(uid, message.data, folderId)
        db.insert(Message(message, messageId))
        folderInteractor.saveMessageInFolder(messageId = messageId, folderId = folderId)
        return messageId
    }

    override suspend fun getMessage(messageId: String): Message =
        db.getMessage(messageId = messageId)

    override suspend fun editMessage(
        uid: String,
        message: Message,
        oldFolderId: String,
        newFolderId: String
    ) {
        repository.editMessage(
            uid = uid,
            message = message,
            oldFolderId = oldFolderId,
            newFolderId = newFolderId
        )
        db.update(message)
        folderInteractor.removeMessageFromFolder(uid = uid, message = message, folderId = oldFolderId)
        folderInteractor.saveMessageInFolder(messageId = message.id, folderId = newFolderId)
    }


}