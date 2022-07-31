package com.orelzman.mymessages.domain.managers.unhandled_calls

import com.orelzman.mymessages.domain.model.entities.CallLogEntity
import com.orelzman.mymessages.domain.model.entities.DeletedCall

interface UnhandledCallsManager {
    fun filterUnhandledCalls(deletedCalls: List<DeletedCall>, callLogs: List<CallLogEntity>): List<CallLogEntity>
}