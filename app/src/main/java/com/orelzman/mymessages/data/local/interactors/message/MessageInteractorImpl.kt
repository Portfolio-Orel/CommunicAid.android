package com.orelzman.mymessages.data.local.interactors.message

import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.data.dto.MessageInFolder
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.interactors.message_in_folder.MessageInFolderInteractor
import com.orelzman.mymessages.data.remote.repository.api.Repository
import com.orelzman.mymessages.data.remote.repository.dto.body.create.CreateMessageBody
import com.orelzman.mymessages.data.remote.repository.dto.response.toMessagesInFolders
import javax.inject.Inject

class MessageInteractorImpl @Inject constructor(
    private val repository: Repository,
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

    override suspend fun createMessage(
        userId: String,
        message: Message,
        folderId: String
    ): String? {
        val messageId =
            repository.createMessage(CreateMessageBody.fromMessage(userId, message, folderId))
                ?: return null

        db.insert(Message(message, messageId))
        messageInFolderInteractor.insert(
            MessageInFolder(
                messageId = messageId,
                folderId = folderId
            )
        )
        return messageId
    }

    override suspend fun getMessage(messageId: String): Message =
        db.getMessage(messageId = messageId)

    override suspend fun updateMessage(
        userId: String,
        message: Message,
        oldFolderId: String,
        newFolderId: String
    ) {
        repository.updateMessage(
            message = message,
            oldFolderId = oldFolderId,
            newFolderId = newFolderId
        )
        db.update(message)
        messageInFolderInteractor.update(
            messageId = message.id,
            oldFolderId = oldFolderId,
            newFolderId = newFolderId
        )
    }

    override suspend fun deleteMessage(message: Message, folderId: String) =
        repository.deleteMessage(message, folderId)


}