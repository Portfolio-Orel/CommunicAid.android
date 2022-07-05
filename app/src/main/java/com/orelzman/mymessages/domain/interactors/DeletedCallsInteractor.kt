package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.DeletedCall
import kotlinx.coroutines.flow.Flow

interface DeletedCallsInteractor {
    suspend fun create(userId: String, deletedCall: DeletedCall)
    suspend fun getAll(userId: String): Flow<List<DeletedCall>>
}