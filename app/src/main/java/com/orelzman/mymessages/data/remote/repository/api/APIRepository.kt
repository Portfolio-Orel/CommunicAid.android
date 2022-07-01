package com.orelzman.mymessages.data.remote.repository.api

import com.orelzman.mymessages.data.remote.repository.dto.*
import javax.inject.Inject

class APIRepository @Inject constructor(
    private val api: API
) : Repository {
    override suspend fun getMessages(userId: String): List<GetMessagesResponse> {
        val result = api.getMessages(userId)
        return result.body
    }

    override suspend fun getFolders(userId: String): List<GetFoldersResponse> {
        val result = api.getFolders(userId)
        return result.body
    }

    override suspend fun createMessage(createMessageBody: CreateMessageBody): String {
        val result = api.createMessage(createMessageBody)
        return result.body
    }

    override suspend fun createFolder(createFolderBody: CreateFolderBody): String? {
        return try {
            val result = api.createFolder(createFolderBody)
            result.body
        } catch (ex: Exception) {
            println()
            null
        }
    }

    override suspend fun createPhoneCall(createPhoneCallBody: CreatePhoneCallBody): String {
        val result = api.createPhoneCall(createPhoneCallBody)
        return result.body
    }

    override suspend fun createPhoneCalls(createPhoneCallBody: List<CreatePhoneCallBody>): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun createDeletedCall(createDeletedCallBody: CreateDeletedCallBody): String {
        val result = api.createDeletedCall(createDeletedCallBody)
        return result.body
    }

    override suspend fun createUser(createUserBody: CreateUserBody) =
        api.createUser(createUserBody)

    override suspend fun getUser(userId: String): GetUserResponse? {
        val response = api.getUser(userId)
        println(response)
        return response?.body
    }
}