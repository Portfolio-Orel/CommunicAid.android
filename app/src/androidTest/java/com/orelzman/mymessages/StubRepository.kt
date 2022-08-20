package com.orelzman.mymessages

import com.orelzman.mymessages.domain.model.dto.body.create.*
import com.orelzman.mymessages.domain.model.dto.response.*
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.domain.repository.Repository
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

    override suspend fun createUser(createUserBody: CreateUserBody) = Unit
    override suspend fun getUser(): GetUserResponse? = null
    override suspend fun createOrUpdateSettings(createOrUpdateSettingsBody: CreateOrUpdateSettingsBody) =
        Unit

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