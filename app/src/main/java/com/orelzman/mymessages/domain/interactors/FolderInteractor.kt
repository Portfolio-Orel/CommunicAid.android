package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.Folder
import kotlinx.coroutines.flow.Flow

interface FolderInteractor {
    fun getFolders(): Flow<List<Folder>>
    fun getAllOnce(): List<Folder>
    suspend fun init()
    suspend fun getFolder(folderId: String): Folder
    suspend fun createFolder(folder: Folder): String?
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(id: String)
    suspend fun getFolderWithMessageId(messageId: String): Folder?
}