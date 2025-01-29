package com.orels.data.remote.repository.api

import com.orels.domain.model.dto.body.create.CreateDeletedCallBody
import com.orels.domain.model.dto.body.create.CreateFolderBody
import com.orels.domain.model.dto.body.create.CreateMessageBody
import com.orels.domain.model.dto.body.create.CreateOngoingCallBody
import com.orels.domain.model.dto.body.create.CreateOrUpdateSettingsBody
import com.orels.domain.model.dto.body.create.CreatePhoneCallBody
import com.orels.domain.model.dto.body.create.CreateUserBody
import com.orels.domain.model.dto.body.update.UpdateFolderBody
import com.orels.domain.model.dto.body.update.UpdateMessageBody
import com.orels.domain.model.dto.response.GetCallsCountResponse
import com.orels.domain.model.dto.response.GetDeletedCallsResponse
import com.orels.domain.model.dto.response.GetFoldersResponse
import com.orels.domain.model.dto.response.GetMessagesResponse
import com.orels.domain.model.dto.response.GetMessagesSentCountResponse
import com.orels.domain.model.dto.response.GetUserResponse
import com.orels.domain.model.dto.response.SettingsResponse
import com.orels.domain.model.entities.Folder
import com.orels.domain.model.entities.Message
import com.orels.domain.repository.Repository
import com.orels.domain.util.extension.log
import java.util.Date
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
    override suspend fun createPhoneCalls(createPhoneCallBody: List<CreatePhoneCallBody>): List<String> {
        try {
            val result = api.createPhoneCalls(createPhoneCallBody)
            return result.body
        } catch (e: Exception) {
            e.log()
            return emptyList()
        }
    }

    override suspend fun createOngoingCall(number: String, contactName: String, date: Long) {
        try {
            val body =
                CreateOngoingCallBody(number = number, contactName = contactName, startDate = date)
            api.createOngoingCall(body)
        } catch (e: Exception) {
            e.log(
                mapOf(
                    "error" to "Error creating ongoing call",
                    "number" to number,
                    "contactName" to contactName,
                    "date" to date.toString()
                )
            )
        }
    }

    override suspend fun clearOngoingCall() {
        try {
            api.clearOngoingCall()
        } catch (e: Exception) {
            e.log()
        }
    }
    /* Phone Calls */

    /* Deleted Phone Calls */
    override suspend fun createDeletedCall(createDeletedCallBody: CreateDeletedCallBody): String? {
        try {
            val result = api.createDeletedCall(createDeletedCallBody)
            return result.body
        } catch (e: Exception) {
            e.log()
        }
        return null
    }

    override suspend fun getDeletedCalls(fromDate: Date): List<GetDeletedCallsResponse> {
        try {
            val result = api.getDeletedCalls(fromDate = fromDate.time)
            return result.body
        } catch (e: Exception) {
            e.log()
        }
        return emptyList()
    }
    /* Deleted Phone Calls */

    /* User */
    override suspend fun createUser(createUserBody: CreateUserBody) {
        try {

            api.createUser(createUserBody)
        } catch (e: Exception) {
            e.log()
        }
    }

    override suspend fun getUser(): GetUserResponse? {
        val response = api.getUser()
        return response?.body
    }
    /* User */

    /* Settings */
    override suspend fun createOrUpdateSettings(createOrUpdateSettingsBody: List<CreateOrUpdateSettingsBody>) =
        api.createOrUpdateSettings(createOrUpdateSettingsBody)

    override suspend fun getAllSettings(): List<SettingsResponse> {
        try {
            val result = api.getAllSettings()
            return result.body
        } catch (e: Exception) {
            return emptyList()
        }
    }

    override suspend fun getSettings(
        key: String
    ): List<SettingsResponse> {
        val result = api.getSettings(key = key)
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