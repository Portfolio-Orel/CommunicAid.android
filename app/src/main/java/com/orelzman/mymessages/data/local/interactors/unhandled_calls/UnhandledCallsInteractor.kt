package com.orelzman.mymessages.data.local.interactors.unhandled_calls

import com.orelzman.mymessages.data.dto.DeletedUnhandledCalls
import com.orelzman.mymessages.domain.model.CallLogEntity

interface UnhandledCallsInteractor {

    suspend fun insert(uid: String, deletedUnhandledCalls: DeletedUnhandledCalls)

    suspend fun update(uid: String, deletedUnhandledCalls: DeletedUnhandledCalls)

    suspend fun getAll(uid: String): List<DeletedUnhandledCalls>

    fun filterUnhandledCalls(deletedUnhandledCalls: List<DeletedUnhandledCalls>, callLogs: List<CallLogEntity>): List<CallLogEntity>
}