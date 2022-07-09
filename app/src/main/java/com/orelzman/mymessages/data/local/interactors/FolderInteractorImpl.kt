package com.orelzman.mymessages.data.local.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.dao.FolderDao
import com.orelzman.mymessages.domain.interactors.FolderInteractor
import com.orelzman.mymessages.domain.interactors.MessageInFolderInteractor
import com.orelzman.mymessages.domain.model.dto.body.create.CreateFolderBody
import com.orelzman.mymessages.domain.model.dto.response.folders
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FolderInteractorImpl @Inject constructor(
    private val repository: Repository,
    private val messageInFolderInteractor: MessageInFolderInteractor,
    database: LocalDatabase,
) : FolderInteractor {

    private val db: FolderDao = database.folderDao

    override suspend fun initFolders(userId: String) {
        if (db.getFoldersCount() == 0) {
            db.insert(repository.getFolders(userId).folders)
        }
    }

    override fun getFolders(): Flow<List<Folder>> = db.getFolders()

    override suspend fun deleteFolder(userId: String, folder: Folder) {
        repository.deleteFolder(folder)
        messageInFolderInteractor.deleteMessagesFromFolder(folder.id)
        db.delete(folder)
    }

    override suspend fun getFolder(folderId: String): Folder =
        db.get(folderId = folderId)

    override suspend fun createFolder(userId: String, folder: Folder): String? {
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
        } catch (exception: Exception) {
            // ToDo
            return null
        }

    }

    override suspend fun updateFolder(folder: Folder) {
        repository.updateFolder(folder)
        db.update(folder)
    }


    override suspend fun deleteFolder(folder: Folder) {
        repository.deleteFolder(folder)
        db.delete(folder)
    }

    override suspend fun getFolderWithMessageId(messageId: String): Folder {
        val folderId = messageInFolderInteractor.getMessageFolderId(messageId)
        return db.get(folderId)
    }

}