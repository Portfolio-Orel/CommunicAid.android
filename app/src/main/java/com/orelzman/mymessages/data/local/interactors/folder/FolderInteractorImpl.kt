package com.orelzman.mymessages.data.local.interactors.folder

import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.data.dto.folders
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.dao.FolderDao
import com.orelzman.mymessages.data.repository.Repository
import javax.inject.Inject

class FolderInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
): FolderInteractor {

    private val db: FolderDao = database.folderDao

    override suspend fun getFolders(uid: String): List<Folder> {
        var folders = db.getFolders()
        if (folders.isEmpty()) {
            folders = repository.getFolders(uid).folders
            db.insert(folders)
        }
        return folders
    }

    override suspend fun addFolder(uid: String, folder: Folder): String {
        val folderId = repository.addFolder(uid, folder.data)
        db.insert(Folder(folder, folderId))
        return folderId
    }

    override suspend fun saveMessageInFolder(messageId: String, folderId: String) {
        val folder = db.get(folderId = folderId)
        (folder.messages as ArrayList<String>).add(messageId)
        db.updateFolder(folder = folder)
    }
}