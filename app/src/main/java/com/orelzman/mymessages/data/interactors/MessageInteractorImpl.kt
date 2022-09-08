package com.orelzman.mymessages.data.interactors

import com.orelzman.mymessages.data.exception.MessageNotFoundException
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.remote.dto.body.create.CreateMessageBody
import com.orelzman.mymessages.data.remote.dto.response.GetMessagesResponse
import com.orelzman.mymessages.domain.interactors.MessageInFolderInteractor
import com.orelzman.mymessages.domain.interactors.MessageInteractor
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.domain.model.entities.MessageInFolder
import com.orelzman.mymessages.domain.model.entities.UploadState
import com.orelzman.mymessages.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class MessageInteractorImpl @Inject constructor(
    private val repository: Repository,
    private val messageInFolderInteractor: MessageInFolderInteractor,
    database: LocalDatabase,
) : MessageInteractor {

    private val db = database.messageDao

    override suspend fun initWithMessagesInFolders(): List<Message> {
        val response = repository.getMessages()
        val messages = response
            .map { it.toMessage() }
            .map {
                it.setUploadState(UploadState.Uploaded)
                it
            }
        db.clear()
        db.insert(messages)
        messageInFolderInteractor.clear()
        messageInFolderInteractor.insert(response.toMessagesInFolders().map {
            it.setUploadState(UploadState.Uploaded)
            it
        })
        return messages
    }

    override fun getMessages(isActive: Boolean): Flow<List<Message>> = db.getMessages(isActive = isActive)

    override fun getAllOnce(isActive: Boolean): List<Message> = db.getMessagesOnce(isActive = isActive)

    override suspend fun createMessage(
        message: Message,
        folderId: String
    ) {
        val tempId = UUID.randomUUID().toString()
        val tempMessage = Message(message, tempId)
        val tempMessageInFolder = MessageInFolder(tempId, folderId, isActive = true)

        tempMessageInFolder.setUploadState(UploadState.BeingUploaded)
        tempMessage.setUploadState(UploadState.BeingUploaded)

        db.insert(tempMessage)
        messageInFolderInteractor.insert(tempMessageInFolder)

        val messageIds =
            repository.createMessage(CreateMessageBody.fromMessage(message, folderId))
        messageIds?.forEach { messageId ->
            val messageWithId = Message(message, messageId)

            db.delete(tempMessage)
            messageInFolderInteractor.delete(tempMessageInFolder)

            val messageInFolder = MessageInFolder(messageId = messageId, folderId = folderId, isActive = true)
            messageInFolder.setUploadState(UploadState.Uploaded)
            messageWithId.setUploadState(UploadState.Uploaded)

            db.insert(messageWithId)
            messageInFolderInteractor.insert(messageInFolder)
        }
    }

    override fun getMessage(messageId: String): Message? =
        db.getMessage(messageId = messageId)

    override suspend fun updateMessage(
        message: Message,
        oldFolderId: String?,
        newFolderId: String?
    ) {
        message.setUploadState(uploadState = UploadState.BeingUploaded)
        db.update(message)
        repository.updateMessage(
            message = message,
            oldFolderId = oldFolderId,
            newFolderId = newFolderId
        )
        message.setUploadState(uploadState = UploadState.Uploaded)
        db.update(message)
        if (oldFolderId != null && newFolderId != null) {
            messageInFolderInteractor.update(
                messageId = message.id,
                oldFolderId = oldFolderId,
                newFolderId = newFolderId
            )
        }
    }

    override suspend fun deleteMessage(message: Message) {
        val folderId = messageInFolderInteractor.getMessageFolderId(message.id)
            ?: throw MessageNotFoundException()
        repository.deleteMessage(message, folderId)
        db.delete(message)
        messageInFolderInteractor.delete(
            MessageInFolder(
                messageId = message.id,
                folderId = folderId,
                isActive = false
            )
        )
    }

    override suspend fun increaseTimesUsed(messageId: String) {
        val message = db.getMessage(messageId) ?: return
        message.timesUsed += 1
        updateMessage(
            message = message
        )
    }
}

fun GetMessagesResponse.toMessage(): Message =
    Message(
        title = title,
        shortTitle = shortTitle,
        body = body,
        timesUsed = timesUsed,
        isActive = isActive,
        id = messageId
    )

fun List<GetMessagesResponse>.toMessagesInFolders(): List<MessageInFolder> {
    val array = ArrayList<MessageInFolder>()
    forEach {
        with(it) {
            array.add(
                MessageInFolder(
                    messageId = messageId,
                    folderId = folderId,
                    isActive = isActive
                )
            )
        }
    }
    return array
}