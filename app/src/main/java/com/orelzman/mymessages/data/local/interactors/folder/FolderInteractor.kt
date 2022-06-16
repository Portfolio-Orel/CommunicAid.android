package com.orelzman.mymessages.data.local.interactors.folder

import com.orelzman.mymessages.data.dto.Folder

interface FolderInteractor {
    suspend fun getFolders(uid: String): List<Folder>
    suspend fun addFolder(uid: String, folder: Folder): String
    suspend fun saveMessageInFolder(messageId: String, folderId: String)
    suspend fun getFolderWithMessageId(messageId: String): Folder
}