package com.orelzman.mymessages.data.local.interactors.deleted_calls

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.DeletedCallsInteractor
import com.orelzman.mymessages.domain.model.dto.body.create.CreateDeletedCallBody
import com.orelzman.mymessages.domain.model.entities.*
import com.orelzman.mymessages.domain.repository.Repository
import javax.inject.Inject

class DeletedCallsInteractorImpl @Inject constructor(
    private val repository: Repository?,
    database: LocalDatabase,
) : DeletedCallsInteractor {
    val db = database.unhandledCallDao

    override suspend fun create(userId: String, deletedCall: DeletedCalls) {
        val createDeletedCallBody = CreateDeletedCallBody(
            number = deletedCall.phoneCall.number,
            userId = userId,
            deleteDate = deletedCall.deleteDate.time
        )
        repository?.createDeletedCall(createDeletedCallBody)
        db.insert(deletedCalls = deletedCall)
    }

    override suspend fun update(userId: String, deletedCalls: DeletedCalls) {
        db.update(deletedCalls = deletedCalls)
    }

    override suspend fun getAll(userId: String): List<DeletedCalls> {
        return db.getAll()
    }

    override fun filterUnhandledCalls(
        deletedCalls: List<DeletedCalls>,
        callLogs: List<CallLogEntity>
    ): List<CallLogEntity> {
        val actualDeletedUnhandledCalls = ArrayList(
            filterDeletedUnhandledCalls(
                deletedCalls = deletedCalls,
                callLogs = callLogs
            )
        )
        val actualCallsHandled = ArrayList(filterByCallsHandled(callLogs = callLogs))
        val allActualCallsHandled =
            (actualCallsHandled.numbers + actualDeletedUnhandledCalls.numbers).distinct()
        return callLogs.unhandledCalls.filter { !allActualCallsHandled.contains(it.phoneCall.number) }
            .sortedByDescending { it.dateMilliseconds }
            .distinctBy { it.number }
    }

    /**
     * Filters calls that were handled because the user has called them back
     * or they called back and were answered.
     */
    private fun filterByCallsHandled(callLogs: List<CallLogEntity>): List<CallLogEntity> {
        val handledCalls = ArrayList<CallLogEntity>()
        val unhandledCalls = ArrayList<CallLogEntity>()
        callLogs.sortedByDescending { it.dateMilliseconds }.forEach {
            if (it.isUnhandled()) {
                if (!handledCalls.numbers.contains(it.number)) { // The call was not handled.
                    unhandledCalls.addUniqueByNumber(it)
                    handledCalls.removeByNumber(it)
                }
            } else if (!unhandledCalls.numbers.contains(it.number)) {
                handledCalls.addUniqueByNumber(it)
            }
        }
        return handledCalls
    }

    /**
     * Filters calls that were handled because they were deleted by the user.
     */
    private fun filterDeletedUnhandledCalls(
        deletedCalls: List<DeletedCalls>,
        callLogs: List<CallLogEntity>
    ): List<DeletedCalls> {
        val callsToHandle = callLogs.filter { callLog ->
            return@filter callLog.isUnhandled()
        }
        return deletedCalls
            .filter { deletedCall ->
                val lastCallToHandle = callsToHandle.filter { callToHandle ->
                    callToHandle.number == deletedCall.phoneCall.number
                }.maxByOrNull { it.phoneCall.endDate }
                return@filter (lastCallToHandle?.dateMilliseconds
                    ?: 0) < deletedCall.deleteDate.time
            }
    }
}