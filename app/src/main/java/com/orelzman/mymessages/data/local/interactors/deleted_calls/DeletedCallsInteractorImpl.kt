package com.orelzman.mymessages.data.local.interactors.deleted_calls

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.DeletedCallsInteractor
import com.orelzman.mymessages.domain.model.dto.body.create.CreateDeletedCallBody
import com.orelzman.mymessages.domain.model.entities.DeletedCall
import com.orelzman.mymessages.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeletedCallsInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : DeletedCallsInteractor {
    val db = database.deletedCallsDao

    override suspend fun create(userId: String, deletedCall: DeletedCall) {
        try {
            db.insert(deletedCalls = deletedCall)
            val createDeletedCallBody = CreateDeletedCallBody(
                number = deletedCall.number,
                userId = userId,
                deleteDate = deletedCall.deleteDate.time
            )
            repository.createDeletedCall(createDeletedCallBody)
            deletedCall.isInDB = true
        } catch (exception: Exception) {
            throw exception
        } finally {
            db.insert(deletedCalls = deletedCall)
        }
    }

    override suspend fun getAll(userId: String): Flow<List<DeletedCall>> {
        if (db.getAllOnce().isEmpty()) {
            val result = repository.getDeletedCalls(userId)
            val deletedCallsList = result.map {
                DeletedCall(
                    id = it.id,
                    number = it.number,
                    deleteDate = it.deleteDate,
                    isInDB = true
                )
            }
            db.insert(deletedCallsList)
        }
        return db.getAll()
    }

}