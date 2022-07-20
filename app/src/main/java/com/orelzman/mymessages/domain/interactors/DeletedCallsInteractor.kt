package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.DeletedCall
import kotlinx.coroutines.flow.Flow
import java.util.*

interface DeletedCallsInteractor {
    suspend fun create(userId: String, deletedCall: DeletedCall)
    suspend fun fetch(userId: String)
    suspend fun getAll(userId: String, startDate: Date): Flow<List<DeletedCall>>
}