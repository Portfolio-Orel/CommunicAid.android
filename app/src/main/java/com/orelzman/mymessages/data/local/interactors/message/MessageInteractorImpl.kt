package com.orelzman.mymessages.data.local.interactors.message

import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.data.dto.MessageInFolder
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.interactors.folder.FolderInteractor
import com.orelzman.mymessages.data.local.interactors.message_in_folder.MessageInFolderInteractor
import com.orelzman.mymessages.data.remote.repository.APIRepository
import com.orelzman.mymessages.data.remote.repository.dto.CreateMessageBody
import com.orelzman.mymessages.data.remote.repository.dto.toMessagesInFolders
import javax.inject.Inject

class MessageInteractorImpl @Inject constructor(
    private val repository: APIRepository,
    private val folderInteractor: FolderInteractor,
    private val messageInFolderInteractor: MessageInFolderInteractor,
    database: LocalDatabase,
) : MessageInteractor {

    private val db = database.messageDao

    override suspend fun getMessagesWithFolders(userId: String): List<Message> {
        var messages = db.getMessages()
        if (messages.isEmpty()) {
            val response = repository.getMessages(userId)
            messages = response.map { it.toMessage() }
            db.insert(messages)
            messageInFolderInteractor.insert(response.toMessagesInFolders())
        }
        return messages
    }

    override suspend fun createMessage(userId: String, message: Message, folderId: String): String {
        val messageId =
            repository.createMessage(CreateMessageBody.fromMessage(userId, message, folderId))
        db.insert(Message(message, messageId))
        messageInFolderInteractor.insert(MessageInFolder(messageId = messageId, folderId = folderId))
        return messageId
    }

    override suspend fun getMessage(messageId: String): Message =
        db.getMessage(messageId = messageId)

    override suspend fun editMessage(
        userId: String,
        message: Message,
        oldFolderId: String,
        newFolderId: String
    ) {
//        repository.editMessage(
//            userId = userId,
//            message = message,
//            oldFolderId = oldFolderId,
//            newFolderId = newFolderId
//        )
//        db.update(message)
//        folderInteractor.removeMessageFromFolder(
//            userId = userId,
//            message = message,
//            folderId = oldFolderId
//        )
//        folderInteractor.saveMessageInFolder(messageId = message.id, folderId = newFolderId)
    }


}