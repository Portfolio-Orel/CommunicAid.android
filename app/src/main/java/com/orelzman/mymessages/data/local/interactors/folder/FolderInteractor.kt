package com.orelzman.mymessages.data.local.interactors.folder

import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.data.dto.Message

interface FolderInteractor {
    suspend fun getFolders(uid: String): List<Folder>
    suspend fun addFolder(uid: String, folder: Folder): String
    suspend fun saveMessageInFolder(messageId: String, folderId: String, isLocal: Boolean = false)
    suspend fun getFolderWithMessageId(messageId: String): Folder
    suspend fun removeMessageFromFolder(uid: String, message: Message, folderId: String, isLocal: Boolean = false)
}