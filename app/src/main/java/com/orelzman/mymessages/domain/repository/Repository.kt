package com.orelzman.mymessages.domain.repository

import com.orelzman.mymessages.domain.model.dto.body.create.*
import com.orelzman.mymessages.domain.model.dto.response.GetFoldersResponse
import com.orelzman.mymessages.domain.model.dto.response.GetMessagesResponse
import com.orelzman.mymessages.domain.model.dto.response.GetUserResponse
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message


/**
 * TODO: Think of a way to inject it with the uid
 */
interface Repository {
    suspend fun getMessages(userId: String): List<GetMessagesResponse>

    suspend fun getFolders(userId: String): List<GetFoldersResponse>

    suspend fun createMessage(createMessageBody: CreateMessageBody): String?

    suspend fun createFolder(createFolderBody: CreateFolderBody): String?

    suspend fun deleteMessage(message: Message, folderId: String)

    suspend fun deleteFolder(folder: Folder)

    suspend fun createPhoneCall(createPhoneCallBody: CreatePhoneCallBody): String?

    suspend fun createPhoneCalls(createPhoneCallBody: List<CreatePhoneCallBody>): List<String>

    suspend fun createDeletedCall(createDeletedCallBody: CreateDeletedCallBody): String?

    suspend fun createUser(createUserBody: CreateUserBody)

    suspend fun getUser(userId: String): GetUserResponse?

    suspend fun updateMessage(
        message: Message,
        oldFolderId: String,
        newFolderId: String
    )

    suspend fun updateFolder(
        folder: Folder,
    )
}