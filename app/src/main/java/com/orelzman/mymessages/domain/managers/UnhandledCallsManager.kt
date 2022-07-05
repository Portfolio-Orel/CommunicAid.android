package com.orelzman.mymessages.domain.managers

import com.orelzman.mymessages.domain.model.entities.CallLogEntity
import com.orelzman.mymessages.domain.model.entities.DeletedCalls

interface UnhandledCallsManager {
    fun filterUnhandledCalls(deletedCalls: List<DeletedCalls>, callLogs: List<CallLogEntity>): List<CallLogEntity>
}