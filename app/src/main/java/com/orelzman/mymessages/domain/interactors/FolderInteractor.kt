package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.Folder

interface FolderInteractor {
    suspend fun getFolders(userId: String): List<Folder>
    suspend fun getFolder(folderId: String): Folder
    suspend fun createFolder(userId: String, folder: Folder): String?
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folder: Folder)
    suspend fun getFolderWithMessageId(messageId: String): Folder
}