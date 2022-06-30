package com.orelzman.mymessages.data.local.interactors.unhandled_calls

import com.orelzman.mymessages.data.dto.DeletedUnhandledCalls
import com.orelzman.mymessages.data.dto.numbers
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.remote.repository.api.Repository
import com.orelzman.mymessages.domain.model.*
import javax.inject.Inject

class UnhandledCallsInteractorImpl @Inject constructor(
    private val repository: Repository?,
    database: LocalDatabase,
) : UnhandledCallsInteractor {
    val db = database.unhandledCallDao

    override suspend fun insert(uid: String, deletedUnhandledCalls: DeletedUnhandledCalls) {
        db.insert(deletedUnhandledCalls = deletedUnhandledCalls)
    }

    override suspend fun update(uid: String, deletedUnhandledCalls: DeletedUnhandledCalls) {
        db.update(deletedUnhandledCalls = deletedUnhandledCalls)
    }

    override suspend fun getAll(uid: String): List<DeletedUnhandledCalls> {
        return db.getAll()
    }

    override fun filterUnhandledCalls(
        deletedUnhandledCalls: List<DeletedUnhandledCalls>,
        callLogs: List<CallLogEntity>
    ): List<CallLogEntity> {
        val actualDeletedUnhandledCalls = ArrayList(
            filterDeletedUnhandledCalls(
                deletedUnhandledCalls = deletedUnhandledCalls,
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
            } else if(!unhandledCalls.numbers.contains(it.number) ){
                handledCalls.addUniqueByNumber(it)
            }
        }
        return handledCalls
    }

    /**
     * Filters calls that were handled because they were deleted by the user.
     */
    private fun filterDeletedUnhandledCalls(
        deletedUnhandledCalls: List<DeletedUnhandledCalls>,
        callLogs: List<CallLogEntity>
    ): List<DeletedUnhandledCalls> {
        val callsToHandle = callLogs.filter { callLog ->
            return@filter callLog.isUnhandled()
        }
        return deletedUnhandledCalls
            .filter { deletedCall ->
                val lastCallToHandle = callsToHandle.filter { callToHandle ->
                    callToHandle.number == deletedCall.phoneCall.number
                }.maxByOrNull { it.phoneCall.endDate }
                return@filter (lastCallToHandle?.dateMilliseconds
                    ?: 0) < deletedCall.deleteDate.time
            }
    }
}