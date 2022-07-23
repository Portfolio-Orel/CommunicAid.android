package com.orelzman.mymessages

import com.orelzman.mymessages.domain.model.dto.body.create.*
import com.orelzman.mymessages.domain.model.dto.response.*
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.domain.repository.Repository

class StubRepository : Repository {
    override suspend fun getMessages(userId: String): List<GetMessagesResponse> =
        emptyList()

    override suspend fun getFolders(userId: String): List<GetFoldersResponse> =
        emptyList()

    override suspend fun createMessages(createMessageBody: CreateMessageBody): List<String>? = null

    override suspend fun createFolder(createFolderBody: CreateFolderBody): String? = null


    override suspend fun deleteMessage(message: Message, folderId: String) = Unit

    override suspend fun deleteFolder(folder: Folder) = Unit

    override suspend fun createPhoneCall(createPhoneCallBody: CreatePhoneCallBody): String? =
        null

    override suspend fun createPhoneCalls(createPhoneCallBody: List<CreatePhoneCallBody>): List<String> =
        emptyList()

    override suspend fun createDeletedCall(createDeletedCallBody: CreateDeletedCallBody): String? =
        null

    override suspend fun getDeletedCalls(userId: String): List<GetDeletedCallsResponse> =
        emptyList()

    override suspend fun createUser(createUserBody: CreateUserBody) = Unit

    override suspend fun getUser(userId: String): GetUserResponse? = null
    override suspend fun createOrUpdateSettings(createOrUpdateSettingsBody: CreateOrUpdateSettingsBody) =
        Unit

    override suspend fun getSettings(userId: String, key: String): List<SettingsResponse> =
        emptyList()

    override suspend fun deleteMessagesFromFolder(folderId: String) = Unit
    override suspend fun getCallsCountByType(): GetCallsCountResponse = GetCallsCountResponse()

    override suspend fun getMessagesSentCount(): List<GetMessagesSentCountResponse> = emptyList()

    override suspend fun updateMessage(message: Message, oldFolderId: String, newFolderId: String) =
        Unit

    override suspend fun updateFolder(folder: Folder) = Unit
}