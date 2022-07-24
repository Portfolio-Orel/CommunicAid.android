package com.orelzman.mymessages.data.remote.repository.api

import com.orelzman.mymessages.domain.model.dto.body.create.*
import com.orelzman.mymessages.domain.model.dto.body.update.UpdateFolderBody
import com.orelzman.mymessages.domain.model.dto.body.update.UpdateMessageBody
import com.orelzman.mymessages.domain.model.dto.response.*
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.domain.repository.Repository
import javax.inject.Inject

class APIRepository @Inject constructor(
    private val api: API
) : Repository {
    override suspend fun getMessages(userId: String): List<GetMessagesResponse> {
        val result = api.getMessages(userId)
        return result.body
    }

    override suspend fun getFolders(userId: String): List<GetFoldersResponse> {
        val result = api.getFolders(userId)
        return result.body
    }

    override suspend fun createMessage(createMessageBody: CreateMessageBody): List<String> {
        val result = api.createMessage(createMessageBody)
        return result.body
    }

    override suspend fun createFolder(createFolderBody: CreateFolderBody): String? {
        return try {
            val result = api.createFolder(createFolderBody)
            result.body
        } catch (ex: Exception) {
            println()
            null
        }
    }

    override suspend fun deleteMessage(message: Message, folderId: String) =
        api.updateMessage(
            UpdateMessageBody(
                messageId = message.id,
                title = message.title,
                shortTitle = message.shortTitle,
                body = message.body,
                times_used = message.timesUsed,
                isActive = false,
                folderId = folderId,
                previousFolderId = folderId,
                position = message.position,
            )
        )


    override suspend fun deleteFolder(folder: Folder) =
        api.updateFolder(
            UpdateFolderBody(
                id = folder.id,
                title = folder.title,
                isActive = false,
                timesUsed = folder.timesUsed,
                position = folder.position
            )
        )

    override suspend fun createPhoneCall(createPhoneCallBody: CreatePhoneCallBody): String {
        val result = api.createPhoneCall(createPhoneCallBody)
        return result.body
    }

    override suspend fun createPhoneCalls(createPhoneCallBody: List<CreatePhoneCallBody>): List<String> {
        val result = api.createPhoneCalls(createPhoneCallBody)
        return result.body
    }

    override suspend fun createDeletedCall(createDeletedCallBody: CreateDeletedCallBody): String {
        val result = api.createDeletedCall(createDeletedCallBody)
        return result.body
    }

    override suspend fun getDeletedCalls(userId: String): List<GetDeletedCallsResponse> {
        val result = api.getDeletedCalls(userId)
        return result.body
    }

    override suspend fun createUser(createUserBody: CreateUserBody) =
        api.createUser(createUserBody)

    override suspend fun getUser(userId: String): GetUserResponse? {
        val response = api.getUser(userId)
        return response?.body
    }

    override suspend fun createOrUpdateSettings(createOrUpdateSettingsBody: CreateOrUpdateSettingsBody) =
        api.createOrUpdateSettings(createOrUpdateSettingsBody)

    override suspend fun getSettings(
        userId: String,
        key: String
    ): List<SettingsResponse> {
        val result = api.getSettings(userId, key)
        return result.body
    }

    override suspend fun deleteMessagesFromFolder(folderId: String) {
        api.deleteMessagesInFolder(folderId = folderId)
    }

    override suspend fun getCallsCountByType(): GetCallsCountResponse {
        val result = api.getCallsCountByType()
        return result.body
    }

    override suspend fun getMessagesSentCount(): List<GetMessagesSentCountResponse>? {
        val result = api.getMessagesSentCount()
        return result.body
    }

    override suspend fun updateMessage(message: Message, oldFolderId: String?, newFolderId: String?) =
        api.updateMessage(
            UpdateMessageBody(
                messageId = message.id,
                title = message.title,
                shortTitle = message.shortTitle,
                body = message.body,
                times_used = message.timesUsed,
                isActive = message.isActive,
                folderId = newFolderId,
                previousFolderId = oldFolderId,
                position = message.position,
            )
        )


    override suspend fun updateFolder(folder: Folder) =
        api.updateFolder(
            UpdateFolderBody(
                id = folder.id,
                title = folder.title,
                isActive = folder.isActive,
                timesUsed = folder.timesUsed,
                position = folder.position
            )
        )



}