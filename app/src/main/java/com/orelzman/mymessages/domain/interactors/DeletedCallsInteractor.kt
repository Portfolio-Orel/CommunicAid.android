package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.CallLogEntity
import com.orelzman.mymessages.domain.model.entities.DeletedCalls

interface DeletedCallsInteractor {

    suspend fun create(userId: String, deletedCall: DeletedCalls)

    suspend fun update(userId: String, deletedCalls: DeletedCalls)

    suspend fun getAll(userId: String): List<DeletedCalls>

    fun filterUnhandledCalls(deletedCalls: List<DeletedCalls>, callLogs: List<CallLogEntity>): List<CallLogEntity>
}