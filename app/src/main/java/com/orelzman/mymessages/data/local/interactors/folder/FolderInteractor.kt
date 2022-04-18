package com.orelzman.mymessages.data.local.interactors.folder

import com.orelzman.mymessages.data.dto.Folder

interface FolderInteractor {
    suspend fun getFolders(uid: String): List<Folder>
    suspend fun addFolder(uid: String, folder: Folder): String
}