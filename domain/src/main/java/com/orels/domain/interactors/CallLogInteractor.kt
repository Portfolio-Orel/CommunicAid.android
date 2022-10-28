package com.orels.domain.interactors

import android.provider.CallLog
import com.orels.domain.model.entities.CallLogEntity
import com.orels.domain.model.entities.PhoneCall
import java.util.*

interface CallLogInteractor {

    fun getTodaysCallLog(): ArrayList<CallLogEntity>

    /**
     * Returns all the logs of calls that occurred between [startDate] and [endDate].
     * @param startDate is the date of the first call we're looking for.
     * @param endDate is the date of the last call we're looking for.
     * @author Orel Zilberman
     */
    fun getCallLogsByDate(
        startDate: Date = Date(),
        endDate: Date = Date()
    ): ArrayList<CallLogEntity>

    suspend fun getLastCallLog(delay: Long): CallLogEntity?

    fun getLastCallLog(): CallLogEntity?

    /**
     * Updates values according to the call log
     * *** Test call in background, removed and called again to see if the backlog catches both from the calllog
     * This has to go to the service because the log is added async.
     */
    fun update(phoneCall: PhoneCall): PhoneCall?
}

enum class CallType(val value: Int) {
    MISSED(CallLog.Calls.MISSED_TYPE),
    INCOMING(CallLog.Calls.INCOMING_TYPE),
    OUTGOING(CallLog.Calls.OUTGOING_TYPE),
    REJECTED(CallLog.Calls.REJECTED_TYPE),
    BLOCK(CallLog.Calls.BLOCKED_TYPE);

    companion object {
        fun fromInt(value: Int): CallType = values().first { it.value == value }
    }
}