package com.orelzman.mymessages.domain.managers.unhandled_calls

import com.orelzman.mymessages.domain.model.entities.*
import com.orelzman.mymessages.domain.util.extension.compareNumberTo
import com.orelzman.mymessages.domain.util.extension.containsNumber
import com.orelzman.mymessages.domain.util.extension.distinctNumbers
import javax.inject.Inject

class UnhandledCallsManagerImpl @Inject constructor() : UnhandledCallsManager {

    override fun filterUnhandledCalls(
        deletedCalls: List<DeletedCall>,
        callLogs: List<CallLogEntity>
    ): List<CallLogEntity> {
        val handledByDeletionCalls = ArrayList(
            filterByDeletedCalls(
                deletedCalls = deletedCalls,
                callLogs = callLogs
            )
        )
        val handledByCallBackCalls = ArrayList(filterByCallsHandled(callLogs = callLogs))
        val handledNumbers =
            (handledByCallBackCalls.numbers + handledByDeletionCalls.numbers).distinctNumbers()
        return callLogs.unhandledCalls.filter {
            !handledNumbers.containsNumber(it.phoneCall.number)
        }
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
                if (!handledCalls.numbers.containsNumber(it.number)) { // The call was not handled.
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
     * @return a list of all handled by deletion calls
     */
    private fun filterByDeletedCalls(
        deletedCalls: List<DeletedCall>,
        callLogs: List<CallLogEntity>
    ): List<DeletedCall> {

        return deletedCalls
            .filter { deletedCall ->
                val potentiallyUnhandledCall = callLogs.unhandledCalls.filter { callToHandle ->
                    callToHandle.number.compareNumberTo(deletedCall.number)
                }.maxByOrNull { it.phoneCall.endDate }
                return@filter (potentiallyUnhandledCall?.time
                    ?: 0) < deletedCall.deleteDate
            }
    }

}