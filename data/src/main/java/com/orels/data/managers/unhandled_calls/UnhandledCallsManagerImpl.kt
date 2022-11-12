package com.orels.data.managers.unhandled_calls

import com.orels.domain.managers.unhandled_calls.UnhandledCallsManager
import com.orels.domain.model.entities.*
import com.orels.domain.util.extension.compareNumberTo
import com.orels.domain.util.extension.containsNumber
import com.orels.domain.util.extension.distinctNumbers
import com.orels.domain.util.extension.withoutPrefix
import javax.inject.Inject

class UnhandledCallsManagerImpl @Inject constructor() : UnhandledCallsManager {

    override fun filterUnhandledCalls(
        deletedCalls: List<DeletedCall>,
        callLogs: List<CallLogEntity>,
        countRejectedAsUnhandled: Boolean
    ): List<CallLogEntity> {
        val handledByDeletionCalls = ArrayList(
            filterByDeletedCalls(
                deletedCalls = deletedCalls,
                callLogs = callLogs,
                countRejectedAsUnhandled = countRejectedAsUnhandled
            )
        )
        val handledByCallBackCalls = ArrayList(
            filterByCallsHandled(
                callLogs = callLogs,
                countRejectedAsUnhandled = countRejectedAsUnhandled
            )
        )
        val handledNumbers =
            (handledByCallBackCalls.numbers + handledByDeletionCalls.numbers).distinctNumbers()
        return callLogs.getUnhandledCalls(countRejectedAsUnhandled = countRejectedAsUnhandled).filter {
            !handledNumbers.containsNumber(it.phoneCall.number)
        }
            .sortedByDescending { it.time }
            .distinctBy { it.number.withoutPrefix() }
    }

    /**
     * Filters calls that were handled because the user has called them back
     * or they called back and were answered.
     */
    private fun filterByCallsHandled(
        callLogs: List<CallLogEntity>,
        countRejectedAsUnhandled: Boolean
    ): List<CallLogEntity> {
        val handledCalls = ArrayList<CallLogEntity>()
        val unhandledCalls = ArrayList<CallLogEntity>()
        callLogs.sortedByDescending { it.time }.forEach {
            if (it.isMissed() || (countRejectedAsUnhandled && it.isRejected())) {
                if (!handledCalls.numbers.containsNumber(it.number)) { // The call was not handled.
                    unhandledCalls.addUniqueByNumber(it)
                    handledCalls.removeByNumber(it)
                }
            } else if (!unhandledCalls.numbers.containsNumber(it.number)) {
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
        callLogs: List<CallLogEntity>,
        countRejectedAsUnhandled: Boolean
    ): List<DeletedCall> {

        return deletedCalls
            .filter { deletedCall ->
                val potentiallyUnhandledCall = callLogs.getUnhandledCalls(countRejectedAsUnhandled = countRejectedAsUnhandled).filter { callToHandle ->
                    callToHandle.number.compareNumberTo(deletedCall.number)
                }.maxByOrNull { it.phoneCall.endDate }
                return@filter (potentiallyUnhandledCall?.time
                    ?: 0) < deletedCall.deleteDate
            }
    }

}