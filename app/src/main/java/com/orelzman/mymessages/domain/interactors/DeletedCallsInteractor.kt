package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.DeletedCall
import kotlinx.coroutines.flow.Flow
import java.util.*

interface DeletedCallsInteractor {
    suspend fun create(userId: String, deletedCall: DeletedCall)
    suspend fun fetchDeletedCalls(userId: String)
    suspend fun getAll(startDate: Date): Flow<List<DeletedCall>>
}