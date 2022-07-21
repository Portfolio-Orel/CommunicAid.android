package com.orelzman.mymessages.data.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.DeletedCallsInteractor
import com.orelzman.mymessages.domain.model.dto.body.create.CreateDeletedCallBody
import com.orelzman.mymessages.domain.model.entities.DeletedCall
import com.orelzman.mymessages.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import java.util.*
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
            val id: String? = repository.createDeletedCall(createDeletedCallBody)
            if(id != null) {
                db.delete(deletedCall)
                deletedCall.isInDB = true
                deletedCall.id = id
                db.insert(deletedCall)
            }
        } catch (exception: Exception) {
            throw exception
        }
    }

    override suspend fun fetch(userId: String) =
        initDeletedCalls(userId)


    override suspend fun getAll(userId: String, startDate: Date): Flow<List<DeletedCall>> {
        if (db.getAllOnce(startDate.time).isEmpty()) {
            initDeletedCalls(userId)
        }
        return db.getAll(startDate.time)
    }

    private suspend fun initDeletedCalls(userId: String) {
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
}