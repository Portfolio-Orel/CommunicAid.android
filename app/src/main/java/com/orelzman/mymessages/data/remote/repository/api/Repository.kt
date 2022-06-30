package com.orelzman.mymessages.data.remote.repository.api

import com.orelzman.mymessages.data.remote.repository.dto.*


/**
 * TODO: Think of a way to inject it with the uid
 */
interface Repository {
    /**
     * Returns all [userId]'s messages.
     */
    suspend fun getMessages(userId: String): List<GetMessagesResponse>

    /**
     * Returns all [userId]'s folders.
     */
    suspend fun getFolders(userId: String): List<GetFoldersResponse>

    /**
     * Adds a message to the db and to the folder's messages and returns its id.
     */
    suspend fun createMessage(createMessageBody: CreateMessageBody): String

    /**
     * Adds a folder to the db and returns its id.
     */
    suspend fun createFolder(createFolderBody: CreateFolderBody): String

//    suspend fun deleteMessage(userId: String, id: String)
//
//    suspend fun deleteFolder(userId: String, id: String)

    suspend fun createPhoneCall(createPhoneCallBody: CreatePhoneCallBody): String

    suspend fun createPhoneCalls(createPhoneCallBody: List<CreatePhoneCallBody>): List<String>

    suspend fun createDeletedCall(createDeletedCallBody: CreateDeletedCallBody): String

//    suspend fun editMessage(
//        userId: String,
//        message: Message,
//        oldFolderId: String,
//        newFolderId: String
//    )
}