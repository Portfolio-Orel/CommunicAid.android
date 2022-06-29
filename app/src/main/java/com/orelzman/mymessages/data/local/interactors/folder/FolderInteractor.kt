package com.orelzman.mymessages.data.local.interactors.folder

import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.data.dto.Message

interface FolderInteractor {
    suspend fun getFolders(userId: String): List<Folder>
    suspend fun addFolder(userId: String, folder: Folder): String
    suspend fun saveMessageInFolder(messageId: String, folderId: String, isLocal: Boolean = false)
    suspend fun getFolderWithMessageId(messageId: String): Folder
    suspend fun removeMessageFromFolder(userId: String, message: Message, folderId: String, isLocal: Boolean = false)
}