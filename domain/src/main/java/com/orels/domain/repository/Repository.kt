package com.orels.domain.repository

import com.orels.domain.model.dto.body.create.CreateDeletedCallBody
import com.orels.domain.model.dto.body.create.CreateFolderBody
import com.orels.domain.model.dto.body.create.CreateMessageBody
import com.orels.domain.model.dto.body.create.CreateOrUpdateSettingsBody
import com.orels.domain.model.dto.body.create.CreatePhoneCallBody
import com.orels.domain.model.dto.body.create.CreateUserBody
import com.orels.domain.model.dto.response.GetCallsCountResponse
import com.orels.domain.model.dto.response.GetDeletedCallsResponse
import com.orels.domain.model.dto.response.GetFoldersResponse
import com.orels.domain.model.dto.response.GetMessagesResponse
import com.orels.domain.model.dto.response.GetMessagesSentCountResponse
import com.orels.domain.model.dto.response.GetUserResponse
import com.orels.domain.model.dto.response.SettingsResponse
import com.orels.domain.model.entities.Folder
import com.orels.domain.model.entities.Message
import java.util.Date


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
    suspend fun createPhoneCalls(createPhoneCallBody: List<CreatePhoneCallBody>): List<String>
    /* Phone Calls */

    /* Ongoing Calls */
    suspend fun createOngoingCall(number: String, contactName: String, date: Long)

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
