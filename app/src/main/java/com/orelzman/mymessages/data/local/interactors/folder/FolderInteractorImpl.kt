package com.orelzman.mymessages.data.local.interactors.folder

import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.data.dto.folders
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.dao.FolderDao
import com.orelzman.mymessages.data.remote.repository.Repository
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

    override suspend fun addFolder(userId: String, folder: Folder): String {
        val folderId = repository.saveFolder(userId, folder.data)
        db.insert(Folder(folder, folderId))
        return folderId
    }

    override suspend fun saveMessageInFolder(messageId: String, folderId: String, isLocal: Boolean) {
        val folder = db.get(folderId = folderId)
        if(folder.messageIds.contains(messageId)) return
        (folder.messageIds as ArrayList<String>).add(messageId)
        db.update(folder = folder)
    }

    override suspend fun getFolderWithMessageId(messageId: String): Folder =
        db.getFolders().first { it.messageIds.contains(messageId) }

    override suspend fun removeMessageFromFolder(userId: String, message: Message, folderId: String, isLocal: Boolean) {
        val folder = db.get(folderId = folderId)
        (folder.messageIds as ArrayList<String>).remove(message.id)
        db.update(folder)
    }

}