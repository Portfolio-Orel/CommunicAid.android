package com.orels.data.interactors

import android.util.Log
import com.orels.data.local.LocalDatabase
import com.orels.domain.interactors.DeletedCallsInteractor
import com.orels.domain.model.dto.body.create.CreateDeletedCallBody
import com.orels.domain.model.entities.DeletedCall
import com.orels.domain.model.entities.UploadState
import com.orels.domain.repository.Repository
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
            db.updateId(deletedCall.deleteDate, id)
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