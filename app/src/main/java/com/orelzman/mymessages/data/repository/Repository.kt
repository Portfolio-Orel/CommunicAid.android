package com.orelzman.mymessages.data.repository

/**
 * TODO: Think of a way to inject it with the uid
 */
interface Repository {
    /**
     * Returns all [uid]'s messages.
     */
    suspend fun getMessages(uid: String): List<Map<String, Any>?>

    /**
     * Returns all [uid]'s folders.
     */
    suspend fun getFolders(uid: String): List<Map<String, Any>?>

    /**
     * Adds a message to the db and to the folder's messages and returns its id.
     */
    suspend fun addMessage(uid: String, data: Map<String, Any>, folderId: String): String

    /**
     * Adds a folder to the db and returns its id.
     */
    suspend fun addFolder(uid: String, data: Map<String, Any>): String

    val folderExistsException: FolderExistsException
}

class FolderExistsException(message: String = "A folder with the same title already exists."): Exception(message)