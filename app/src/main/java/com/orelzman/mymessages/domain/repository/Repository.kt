package com.orelzman.mymessages.domain.repository

import com.orelzman.mymessages.domain.model.dto.body.create.*
import com.orelzman.mymessages.domain.model.dto.response.*
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message
import java.util.*


/**
 * TODO: Think of a way to inject it with the uid
 */
interface Repository {
    suspend fun createMessage(createMessageBody: CreateMessageBody): List<String>?
    suspend fun getMessages(): List<GetMessagesResponse>
    suspend fun deleteMessage(message: Message, folderId: String)
    suspend fun updateMessage(
        message: Message,
        oldFolderId: String? = null,
        newFolderId: String? = null
    )

    suspend fun createFolder(createFolderBody: CreateFolderBody): String?
    suspend fun getFolders(): List<GetFoldersResponse>
    suspend fun deleteFolder(folder: Folder)
    suspend fun updateFolder(folder: Folder)

    suspend fun createDeletedCall(createDeletedCallBody: CreateDeletedCallBody): String?
    suspend fun getDeletedCalls(): List<GetDeletedCallsResponse>

    suspend fun createPhoneCall(createPhoneCallBody: CreatePhoneCallBody): String?
    suspend fun createPhoneCalls(createPhoneCallBody: List<CreatePhoneCallBody>): List<String>

    suspend fun createUser(createUserBody: CreateUserBody)
    suspend fun getUser(): GetUserResponse?

    suspend fun createOrUpdateSettings(createOrUpdateSettingsBody: CreateOrUpdateSettingsBody)
    suspend fun getSettings(key: String = ""): List<SettingsResponse>
    suspend fun getAllSettings(): List<SettingsResponse>

    suspend fun deleteMessagesFromFolder(folderId: String)

    suspend fun getCallsCountByType(startDate: Date? = null, endDate: Date? = null): GetCallsCountResponse
    suspend fun getMessagesSentCount(startDate: Date? = null, endDate: Date? = null): List<GetMessagesSentCountResponse>?
}
