package com.orels.domain.managers.unhandled_calls

import com.orels.domain.model.entities.CallLogEntity
import com.orels.domain.model.entities.DeletedCall

interface UnhandledCallsManager {

    /**
     * Filters unhandled calls from [callLogs] minus deleted calls.
     * Unhandled calls are Missed/Rejected(if [countRejectedAsUnhandled] is true)
     */
    fun filterUnhandledCalls(
        deletedCalls: List<DeletedCall>,
        callLogs: List<CallLogEntity>,
        countRejectedAsUnhandled: Boolean,
    ): List<CallLogEntity>
}