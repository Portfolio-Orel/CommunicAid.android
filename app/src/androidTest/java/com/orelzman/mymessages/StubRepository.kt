package com.orelzman.mymessages

import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.domain.repository.Repository
import com.orelzman.mymessages.domain.model.dto.body.create.*
import com.orelzman.mymessages.domain.model.dto.response.GetFoldersResponse
import com.orelzman.mymessages.domain.model.dto.response.GetMessagesResponse
import com.orelzman.mymessages.domain.model.dto.response.GetUserResponse

class StubRepository : Repository {
    override suspend fun getMessages(userId: String): List<GetMessagesResponse> =
        emptyList()

    override suspend fun getFolders(userId: String): List<GetFoldersResponse> =
        emptyList()

    override suspend fun createMessage(createMessageBody: CreateMessageBody): String? = null

    override suspend fun createFolder(createFolderBody: CreateFolderBody): String? = null


    override suspend fun deleteMessage(message: Message, folderId: String) = Unit

    override suspend fun deleteFolder(folder: Folder) = Unit

    override suspend fun createPhoneCall(createPhoneCallBody: CreatePhoneCallBody): String? =
        null

    override suspend fun createPhoneCalls(createPhoneCallBody: List<CreatePhoneCallBody>): List<String> =
        emptyList()

    override suspend fun createDeletedCall(createDeletedCallBody: CreateDeletedCallBody): String? =
        null

    override suspend fun createUser(createUserBody: CreateUserBody) = Unit

    override suspend fun getUser(userId: String): GetUserResponse? = null

    override suspend fun updateMessage(message: Message, oldFolderId: String, newFolderId: String) =
        Unit

    override suspend fun updateFolder(folder: Folder) = Unit
}