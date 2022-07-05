package com.orelzman.mymessages.data.local.interactors.deleted_calls

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.DeletedCallsInteractor
import com.orelzman.mymessages.domain.model.dto.body.create.CreateDeletedCallBody
import com.orelzman.mymessages.domain.model.entities.DeletedCalls
import com.orelzman.mymessages.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeletedCallsInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : DeletedCallsInteractor {
    val db = database.deletedCallsDao

    override suspend fun create(userId: String, deletedCall: DeletedCalls) {
        val createDeletedCallBody = CreateDeletedCallBody(
            number = deletedCall.phoneCall.number,
            userId = userId,
            deleteDate = deletedCall.deleteDate.time
        )
        repository.createDeletedCall(createDeletedCallBody)
        db.insert(deletedCalls = deletedCall)
    }

    override fun getAll(userId: String): Flow<List<DeletedCalls>> =
        db.getAll()

}