package com.orelzman.mymessages.data.interactors

import android.util.Log
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.remote.dto.body.create.CreateDeletedCallBody
import com.orelzman.mymessages.domain.interactors.DeletedCallsInteractor
import com.orelzman.mymessages.domain.model.entities.DeletedCall
import com.orelzman.mymessages.domain.model.entities.UploadState
import com.orelzman.mymessages.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class DeletedCallsInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : DeletedCallsInteractor {
    val db = database.deletedCallsDao

    override suspend fun create(deletedCall: DeletedCall) {
        deletedCall.setUploadState(UploadState.NotUploaded)
        db.insert(deletedCalls = deletedCall)
        val createDeletedCallBody = CreateDeletedCallBody(
            number = deletedCall.number,
            deleteDate = deletedCall.deleteDate
        )
        val id: String? = repository.createDeletedCall(createDeletedCallBody)
        if (id != null) {
            db.delete(deletedCall)
            deletedCall.id = id
            db.insert(deletedCall)
            deletedCall.setUploadState(UploadState.Uploaded)
            deletedCall.id = id
            db.update(deletedCall)
        }
    }

    override suspend fun getAll(startDate: Date): Flow<List<DeletedCall>> {
        return db.getAll()
    }

    override fun getAllOnce(startDate: Date?): List<DeletedCall> {
        return if (startDate == null) {
            db.getAllOnce()
        } else {
            db.getAllOnce(startDate = startDate.time)
        }
    }

    override suspend fun init(fromDate: Date) {
        val result = repository.getDeletedCalls(fromDate = fromDate)
        val deletedCallsList = result.map {
            val deletedCall = DeletedCall(
                id = it.id,
                number = it.number,
                deleteDate = it.deleteDate.time
            )
            deletedCall.setUploadState(UploadState.Uploaded)
            deletedCall
        }
        if (deletedCallsList.isEmpty()) {
            db.insert(arrayListOf(DeletedCall())) // Force collectLatest collection
        } else {
            Log.v("Delete", "Deleted DB")
            db.clear()
            db.insert(deletedCallsList)
        }
    }
}