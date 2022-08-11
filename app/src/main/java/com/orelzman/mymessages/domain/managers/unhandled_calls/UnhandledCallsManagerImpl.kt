package com.orelzman.mymessages.domain.managers.unhandled_calls

import com.orelzman.mymessages.domain.model.entities.*
import javax.inject.Inject

class UnhandledCallsManagerImpl @Inject constructor(): UnhandledCallsManager {
    override fun filterUnhandledCalls(
        deletedCalls: List<DeletedCall>,
        callLogs: List<CallLogEntity>
    ): List<CallLogEntity> {
        val actualDeletedUnhandledCalls = ArrayList(
            filterByDeletedCalls(
                deletedCalls = deletedCalls,
                callLogs = callLogs
            )
        )
        val actualCallsHandled = ArrayList(filterByCallsHandled(callLogs = callLogs))
        val allActualCallsHandledNumbers =
            (actualCallsHandled.numbers + actualDeletedUnhandledCalls.numbers).distinct()
        return callLogs.unhandledCalls.filter { !allActualCallsHandledNumbers.contains(it.phoneCall.number) }
            .sortedByDescending { it.time }
            .distinctBy { it.number }
    }

    /**
     * Filters calls that were handled because the user has called them back
     * or they called back and were answered.
     */
    private fun filterByCallsHandled(callLogs: List<CallLogEntity>): List<CallLogEntity> {
        val handledCalls = ArrayList<CallLogEntity>()
        val unhandledCalls = ArrayList<CallLogEntity>()
        callLogs.sortedByDescending { it.time }.forEach {
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
    private fun filterByDeletedCalls(
        deletedCalls: List<DeletedCall>,
        callLogs: List<CallLogEntity>
    ): List<DeletedCall> {
        val callsToHandle = callLogs.filter { callLog ->
            return@filter callLog.isUnhandled()
        }
        return deletedCalls
            .filter { deletedCall ->
                val lastCallToHandle = callsToHandle.filter { callToHandle ->
                    callToHandle.number == deletedCall.number
                }.maxByOrNull { it.phoneCall.endDate }
                return@filter (lastCallToHandle?.time
                    ?: 0) < deletedCall.deleteDate
            }
    }

}