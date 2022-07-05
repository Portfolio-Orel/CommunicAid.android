package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.DeletedCalls
import kotlinx.coroutines.flow.Flow

interface DeletedCallsInteractor {
    suspend fun create(userId: String, deletedCall: DeletedCalls)
    fun getAll(userId: String): Flow<List<DeletedCalls>>
}