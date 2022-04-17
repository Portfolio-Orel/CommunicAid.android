package com.orelzman.mymessages.data.repository

interface Repository {
//    suspend fun getMessages(uid: String): Map<String, Any>
//    suspend fun getFolders(uid: String): Map<String, Any>
suspend fun getMessages(uid: String): List<Map<String, Any>?>
    suspend fun getFolders(uid: String): List<Map<String, Any>?>
    suspend fun addMessage(uid: String, data: Map<String, Any>)
    suspend fun addFolder(uid: String, data: Map<String, Any>)
    suspend fun addMessagesToFolder(
        uid: String,
        folderId: String,
        messageId: List<String>
    )
}