package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.Folder
import kotlinx.coroutines.flow.Flow

interface FolderInteractor {
    fun getFolders(): Flow<List<Folder>>
    suspend fun deleteFolder(userId: String, folder: Folder)
    suspend fun initFolders(userId: String)
    suspend fun getFolder(folderId: String): Folder
    suspend fun createFolder(userId: String, folder: Folder): String?
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folder: Folder)
    suspend fun getFolderWithMessageId(messageId: String): Folder
}