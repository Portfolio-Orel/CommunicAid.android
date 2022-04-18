package com.orelzman.mymessages.data.local.interactors.message

import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.data.dto.messages
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.repository.Repository
import javax.inject.Inject

class MessageInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : MessageInteractor {

    private val db = database.messageDao

    override suspend fun getMessages(uid: String): List<Message> {
        var messages = db.getMessages()
        if(messages.isEmpty()) {
            messages = repository.getMessages(uid).messages
            db.insert(messages)
        }
        return messages
    }

    override suspend fun addMessage(uid: String, message: Message, folderId: String): String {
        val messageId = repository.addMessage(uid, message.data, folderId)
        db.insert(Message(message, messageId))
        return messageId
    }

}