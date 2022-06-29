package com.orelzman.mymessages.data.remote.repository

import com.orelzman.mymessages.data.dto.Message


/**
 * TODO: Think of a way to inject it with the uid
 */
interface Repository {
    /**
     * Returns all [userId]'s messages.
     */
    suspend fun getMessages(userId: String): List<Map<String, Any>?>

    /**
     * Returns all [userId]'s folders.
     */
    suspend fun getFolders(userId: String): List<Map<String, Any>?>

    /**
     * Adds a message to the db and to the folder's messages and returns its id.
     */
    suspend fun saveMessage(userId: String, data: Map<String, Any>, folderId: String): String

    /**
     * Adds a folder to the db and returns its id.
     */
    suspend fun saveFolder(userId: String, data: Map<String, Any>): String

    suspend fun deleteMessage(userId: String, id: String)

    suspend fun deleteFolder(userId: String, id: String)

    suspend fun addPhoneCalls(userId: String, dataList: List<Map<String, Any>>)

    suspend fun editMessage(
        userId: String,
        message: Message,
        oldFolderId: String,
        newFolderId: String
    )

    val folderExistsException: FolderExistsException
}

class FolderExistsException(message: String = "A folder with the same title already exists."): Exception(message)