package com.orelzman.mymessages.data.local.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.MessageInFolderInteractor
import com.orelzman.mymessages.domain.interactors.MessageInteractor
import com.orelzman.mymessages.domain.model.dto.body.create.CreateMessageBody
import com.orelzman.mymessages.domain.model.dto.response.toMessagesInFolders
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.domain.model.entities.MessageInFolder
import com.orelzman.mymessages.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageInteractorImpl @Inject constructor(
    private val repository: Repository,
    private val messageInFolderInteractor: MessageInFolderInteractor,
    database: LocalDatabase,
) : MessageInteractor {

    private val db = database.messageDao

    override suspend fun initMessagesAndMessagesInFolders(userId: String): List<Message> {
        val messagesCount = db.getMessagesCount()
        var messages: List<Message> = emptyList()
        if (messagesCount == 0) {
            val response = repository.getMessages(userId)
            messages = response.map { it.toMessage() }
            db.insert(messages)
            messageInFolderInteractor.insert(response.toMessagesInFolders())
        }
        return messages
    }

    override fun getMessages(): Flow<List<Message>> = db.getMessages()

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

    override suspend fun deleteMessage(message: Message, folderId: String) {
        repository.deleteMessage(message, folderId)
        db.delete(message)
        messageInFolderInteractor.deleteMessageInFolder(
            MessageInFolder(
                messageId = message.id,
                folderId = folderId
            )
        )
    }


}