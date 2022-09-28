package com.orelzman.mymessages.data.remote.repository.api

import com.orelzman.mymessages.data.remote.dto.body.create.*
import com.orelzman.mymessages.data.remote.dto.body.update.UpdateFolderBody
import com.orelzman.mymessages.data.remote.dto.body.update.UpdateMessageBody
import com.orelzman.mymessages.data.remote.dto.response.*
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.domain.repository.Repository
import java.util.*
import javax.inject.Inject

class APIRepository @Inject constructor(
    private val api: API
) : Repository {

    /* Messages */
    override suspend fun createMessage(createMessageBody: CreateMessageBody): List<String> {
        val result = api.createMessage(createMessageBody)
        return result.body
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

    override suspend fun deleteMessagesFromFolder(folderId: String) {
        api.deleteMessagesInFolder(folderId = folderId)
    }

    override suspend fun getMessages(): List<GetMessagesResponse> {
        val result = api.getMessages()
        return result.body
    }

    override suspend fun updateMessage(
        message: Message,
        oldFolderId: String?,
        newFolderId: String?
    ) {
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
    }
    /* Messages */

    /* Folders */
    override suspend fun createFolder(createFolderBody: CreateFolderBody): String? {
        return try {
            val result = api.createFolder(createFolderBody)
            result.body
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteFolder(id: String) =
        api.deleteFolder(id = id)

    override suspend fun getFolders(): List<GetFoldersResponse> {
        val result = api.getFolders()
        return result.body
    }

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
    /* Folders */

    /* Phone Calls */
    override suspend fun createPhoneCall(createPhoneCallBody: CreatePhoneCallBody): String {
        val result = api.createPhoneCall(createPhoneCallBody)
        return result.body
    }

    override suspend fun createPhoneCalls(createPhoneCallBody: List<CreatePhoneCallBody>): List<String> {
        val result = api.createPhoneCalls(createPhoneCallBody)
        return result.body
    }
    /* Phone Calls */

    /* Deleted Phone Calls */
    override suspend fun createDeletedCall(createDeletedCallBody: CreateDeletedCallBody): String {
        val result = api.createDeletedCall(createDeletedCallBody)
        return result.body
    }

    override suspend fun getDeletedCalls(fromDate: Date): List<GetDeletedCallsResponse> {
        val result = api.getDeletedCalls(fromDate = fromDate.time)
        return result.body
    }
    /* Deleted Phone Calls */

    /* User */
    override suspend fun createUser(createUserBody: CreateUserBody) =
        api.createUser(createUserBody)

    override suspend fun getUser(): GetUserResponse? {
        val response = api.getUser()
        return response?.body
    }
    /* User */

    /* Settings */
    override suspend fun createOrUpdateSettings(createOrUpdateSettingsBody: List<CreateOrUpdateSettingsBody>) =
        api.createOrUpdateSettings(createOrUpdateSettingsBody)

    override suspend fun getAllSettings(): List<SettingsResponse> {
        val result = api.getAllSettings()
        return result.body
    }

    override suspend fun getSettings(
        key: String
    ): List<SettingsResponse> {
        val result = api.getSettings(key)
        return result.body
    }
    /* Settings */

    /* Statistics */
    override suspend fun getCallsCountByType(
        startDate: Date?,
        endDate: Date?
    ): GetCallsCountResponse {
        val result = api.getCallsCountByType(startDate = startDate?.time, endDate = endDate?.time)
        return result.body
    }

    override suspend fun getMessagesSentCount(
        startDate: Date?,
        endDate: Date?
    ): List<GetMessagesSentCountResponse> {
        val result = api.getMessagesSentCount(startDate = startDate?.time, endDate = endDate?.time)
        return result.body
    }
    /* Statistics */
}