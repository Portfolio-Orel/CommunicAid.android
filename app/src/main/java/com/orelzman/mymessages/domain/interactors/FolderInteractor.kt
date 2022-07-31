package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.Folder
import kotlinx.coroutines.flow.Flow

interface FolderInteractor {
    fun getFolders(): Flow<List<Folder>>
    fun getFoldersOnce(): List<Folder>
    suspend fun deleteFolder(userId: String, folder: Folder)
    suspend fun init()
    suspend fun getFolder(folderId: String): Folder
    suspend fun createFolder(userId: String, folder: Folder): String?
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folder: Folder)
    suspend fun getFolderWithMessageId(messageId: String): Folder?
}