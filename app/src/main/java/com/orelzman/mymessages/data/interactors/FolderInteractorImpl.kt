package com.orelzman.mymessages.data.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.dao.FolderDao
import com.orelzman.mymessages.data.remote.dto.body.create.CreateFolderBody
import com.orelzman.mymessages.data.remote.dto.response.GetFoldersResponse
import com.orelzman.mymessages.domain.interactors.FolderInteractor
import com.orelzman.mymessages.domain.interactors.MessageInFolderInteractor
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.UploadState
import com.orelzman.mymessages.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class FolderInteractorImpl @Inject constructor(
    private val repository: Repository,
    private val messageInFolderInteractor: MessageInFolderInteractor,
    database: LocalDatabase,
) : FolderInteractor {

    private val db: FolderDao = database.folderDao

    override suspend fun init() {
        val folders = repository
            .getFolders()
            .folders
            .map {
                it.setUploadState(UploadState.Uploaded)
                it
            }
        db.clear()
        db.insert(folders)
    }

    override fun getFolders(isActive: Boolean): Flow<List<Folder>> =
        db.getFolders(isActive = isActive)

    override fun getAllOnce(isActive: Boolean): List<Folder> =
        db.getFoldersOnce(isActive = isActive)

    override fun getFolder(folderId: String): Folder? = db.get(folderId = folderId)

    override suspend fun deleteFolder(id: String) {
        repository.deleteFolder(id)
        messageInFolderInteractor.deleteMessagesFromFolder(id)
        db.delete(id)
    }

    override suspend fun createFolder(folder: Folder): String? {
        val tempFolder = Folder(folder, UUID.randomUUID().toString())
        tempFolder.setUploadState(UploadState.BeingUploaded)
        db.insert(tempFolder)
        val folderId = repository.createFolder(
            CreateFolderBody(
                title = folder.title,
                position = null
            )
        ) ?: return null
        val newFolder = Folder(folder, folderId)
        newFolder.setUploadState(UploadState.Uploaded)
        db.delete(tempFolder.id)
        db.insert(newFolder)
        return folderId
    }

    override suspend fun update(folder: Folder) {
        folder.setUploadState(uploadState = UploadState.BeingUploaded)
        db.update(folder)
        repository.updateFolder(folder)
        folder.setUploadState(uploadState = UploadState.Uploaded)
        db.update(folder)
        if(folder.isActive) {
            messageInFolderInteractor.restore(folderId = folder.id)
        } else {
            messageInFolderInteractor.deleteMessagesFromFolder(folderId = folder.id)
        }
    }

    override fun getFolderWithMessageId(messageId: String): Folder? {
        val folderId = messageInFolderInteractor.getMessageFolderId(messageId) ?: return null
        return db.get(folderId)
    }
}


val List<GetFoldersResponse>.folders: List<Folder>
    get() {
        val array = ArrayList<Folder>()
        forEach {
            with(it) {
                array.add(
                    Folder(
                        title = title,
                        isActive = isActive,
                        timesUsed = timesUsed,
                        position = position,
                        id = id
                    )
                )
            }
        }
        return array
    }