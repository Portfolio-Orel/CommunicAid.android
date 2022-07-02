package com.orelzman.mymessages.data.local.interactors.folder

import com.orelzman.mymessages.data.dto.Folder

interface FolderInteractor {
    suspend fun getFolders(userId: String): List<Folder>
    suspend fun getFolder(folderId: String): Folder
    suspend fun createFolder(userId: String, folder: Folder): String?
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folder: Folder)
    suspend fun getFolderWithMessageId(messageId: String): Folder
}