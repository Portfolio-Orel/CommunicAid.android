package com.orelzman.mymessages.data.local.interactors.folder

import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.dao.FolderDao
import com.orelzman.mymessages.data.remote.repository.api.Repository
import com.orelzman.mymessages.data.remote.repository.dto.CreateFolderBody
import com.orelzman.mymessages.data.remote.repository.dto.folders
import javax.inject.Inject

class FolderInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : FolderInteractor {

    private val db: FolderDao = database.folderDao

    override suspend fun getFolders(userId: String): List<Folder> {
        var folders = db.getFolders()
        if (folders.isEmpty()) {
            folders = repository.getFolders(userId).folders
            db.insert(folders)
        }
        return folders
    }

    override suspend fun addFolder(userId: String, folder: Folder): String? {
        try {
            val folderId = repository.createFolder(
                CreateFolderBody(
                    title = folder.title,
                    userId = userId,
                    position = null
                )
            ) ?: return null

            db.insert(Folder(folder, folderId))
            return folderId
        } catch(exception: Exception) {
            // ToDo
            return null
        }

    }
}