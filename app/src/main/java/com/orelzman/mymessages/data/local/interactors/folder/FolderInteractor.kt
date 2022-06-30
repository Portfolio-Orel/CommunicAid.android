package com.orelzman.mymessages.data.local.interactors.folder

import com.orelzman.mymessages.data.dto.Folder

interface FolderInteractor {
    suspend fun getFolders(userId: String): List<Folder>
    suspend fun addFolder(userId: String, folder: Folder): String
}