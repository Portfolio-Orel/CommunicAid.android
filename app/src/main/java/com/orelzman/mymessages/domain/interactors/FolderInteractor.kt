package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.Folder
import kotlinx.coroutines.flow.Flow

interface FolderInteractor {
    fun getFolders(isActive: Boolean = true): Flow<List<Folder>>
    fun getAllOnce(isActive: Boolean = true): List<Folder>
    fun getFolder(folderId: String): Folder
    suspend fun init()
    suspend fun createFolder(folder: Folder): String?
    suspend fun update(folder: Folder)
    suspend fun deleteFolder(id: String)
    suspend fun getFolderWithMessageId(messageId: String): Folder?
}