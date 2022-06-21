package com.orelzman.mymessages.data.local.interactors.unhandled_calls

import android.os.Build
import com.orelzman.mymessages.data.dto.UnhandledCall
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.remote.repository.Repository
import com.orelzman.mymessages.domain.model.CallLogEntity
import com.orelzman.mymessages.util.CallType
import javax.inject.Inject

class UnhandledCallsInteractorImpl @Inject constructor(
    private val repository: Repository?,
    database: LocalDatabase,
) : UnhandledCallsInteractor {
    val db = database.unhandledCallDao

    override suspend fun insert(uid: String, unhandledCall: UnhandledCall) {
        db.insert(unhandledCall = unhandledCall)
    }

    override suspend fun update(uid: String, unhandledCall: UnhandledCall) {
        db.update(unhandledCall = unhandledCall)
    }

    override suspend fun getAll(uid: String): List<UnhandledCall> {
        return db.getAll()
    }

    override fun filterUnhandledCalls(unhandledCalls: List<UnhandledCall>, callLogs: List<CallLogEntity>): List<CallLogEntity> {
        var callsToHandle = callLogs.filter { callLog ->
            return@filter if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                callLog.callLogType == CallType.REJECTED || callLog.callLogType == CallType.MISSED
            } else {
                callLog.callLogType == CallType.MISSED
            }
        }
        callsToHandle = callsToHandle.filter { callLogEntity ->
            val unhandledCall = unhandledCalls.find { unhandledCall ->
                unhandledCall.phoneCall.number == callLogEntity.number
            }
            return@filter unhandledCall?.phoneCall?.startDate?.time ?: 0 < callLogEntity.dateMilliseconds
        }
        return callsToHandle
    }
}