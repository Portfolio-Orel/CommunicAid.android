package com.orelzman.mymessages.domain.repository

import com.orelzman.mymessages.data.remote.dto.body.create.*
import com.orelzman.mymessages.data.remote.dto.response.*
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message
import java.util.*


/**
 * TODO: Think of a way to inject it with the uid
 */
interface Repository {
    /* Messages */
    suspend fun createMessage(createMessageBody: CreateMessageBody): List<String>?
    suspend fun deleteMessage(message: Message, folderId: String)
    suspend fun deleteMessagesFromFolder(folderId: String)
    suspend fun getMessages(): List<GetMessagesResponse>
    suspend fun updateMessage(
        message: Message,
        oldFolderId: String? = null,
        newFolderId: String? = null
    )
    /* Messages */

    /* Folders */
    suspend fun createFolder(createFolderBody: CreateFolderBody): String?
    suspend fun deleteFolder(id: String)
    suspend fun getFolders(): List<GetFoldersResponse>
    suspend fun updateFolder(folder: Folder)
    /* Folders */

    /* Phone Calls */
    suspend fun createPhoneCall(createPhoneCallBody: CreatePhoneCallBody): String?
    suspend fun createPhoneCalls(createPhoneCallBody: List<CreatePhoneCallBody>): List<String>
    /* Phone Calls */

    /*Deleted Phone Calls*/
    suspend fun createDeletedCall(createDeletedCallBody: CreateDeletedCallBody): String?
    suspend fun getDeletedCalls(fromDate: Date): List<GetDeletedCallsResponse>
    /*Deleted Phone Calls*/

    /* Settings */
    suspend fun createOrUpdateSettings(createOrUpdateSettingsBody: List<CreateOrUpdateSettingsBody>)
    suspend fun getSettings(key: String = ""): List<SettingsResponse>
    suspend fun getAllSettings(): List<SettingsResponse>
    /* Settings */

    /* Statistics */
    suspend fun getCallsCountByType(
        startDate: Date? = null,
        endDate: Date? = null
    ): GetCallsCountResponse

    suspend fun getMessagesSentCount(
        startDate: Date? = null,
        endDate: Date? = null
    ): List<GetMessagesSentCountResponse>?
    /* Statistics */

    /* User */
    suspend fun createUser(createUserBody: CreateUserBody)
    suspend fun getUser(): GetUserResponse?
    /* User */


}
