package com.orels.app

import com.orels.app.data.remote.dto.body.create.*
import com.orels.app.data.remote.dto.response.*
import com.orels.app.domain.model.entities.Folder
import com.orels.app.domain.model.entities.Message
import com.orels.app.domain.repository.Repository
import com.orels.domain.model.dto.body.create.CreateFolderBody
import com.orels.domain.model.dto.body.create.CreateMessageBody
import com.orels.domain.model.dto.response.GetFoldersResponse
import com.orels.domain.model.dto.response.GetMessagesResponse
import com.orels.domain.model.entities.Folder
import com.orels.domain.repository.Repository
import java.util.*

class StubRepository : Repository {


    override suspend fun createMessage(createMessageBody: CreateMessageBody): List<String>? = null
    override suspend fun getMessages(): List<GetMessagesResponse> =
        emptyList()


    override suspend fun createFolder(createFolderBody: CreateFolderBody): String? = null
    override suspend fun getFolders(): List<GetFoldersResponse> = emptyList()

    override suspend fun deleteMessage(message: Message, folderId: String) = Unit
    override suspend fun updateMessage(
        message: Message,
        oldFolderId: String?,
        newFolderId: String?
    ) = Unit

    override suspend fun deleteFolder(folder: Folder) = Unit

    override suspend fun createPhoneCall(createPhoneCallBody: CreatePhoneCallBody): String? =
        null

    override suspend fun createPhoneCalls(createPhoneCallBody: List<CreatePhoneCallBody>): List<String> =
        emptyList()

    override suspend fun createDeletedCall(createDeletedCallBody: CreateDeletedCallBody): String? =
        null

    override suspend fun getDeletedCalls(fromDate: Date): List<GetDeletedCallsResponse> = emptyList()
    override suspend fun createOrUpdateSettings(createOrUpdateSettingsBody: List<CreateOrUpdateSettingsBody>) = Unit

    override suspend fun createUser(createUserBody: CreateUserBody) = Unit
    override suspend fun getUser(): GetUserResponse? = null

    override suspend fun getSettings(key: String): List<SettingsResponse> = emptyList()

    override suspend fun getAllSettings(): List<SettingsResponse> = emptyList()
    override suspend fun getCallsCountByType(
        startDate: Date?,
        endDate: Date?
    ): GetCallsCountResponse = GetCallsCountResponse()

    override suspend fun getMessagesSentCount(
        startDate: Date?,
        endDate: Date?
    ): List<GetMessagesSentCountResponse>? = null

    override suspend fun deleteMessagesFromFolder(folderId: String) = Unit
    override suspend fun updateFolder(folder: Folder) = Unit
}