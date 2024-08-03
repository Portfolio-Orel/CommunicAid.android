package com.orels.domain.interactors

import com.orels.domain.model.entities.Folder
import kotlinx.coroutines.flow.Flow

interface FolderInteractor {
    fun getFolders(isActive: Boolean = true): Flow<List<Folder>>
    fun getAllOnce(isActive: Boolean = true): List<Folder>
    fun getFolder(folderId: String): Folder?
    fun getFolderWithMessageId(messageId: String): Folder?
    suspend fun init(clearFirst: Boolean = false)
    suspend fun createFolder(folder: Folder): String?
    suspend fun update(folder: Folder)
    suspend fun deleteFolder(id: String)
}