package com.orels

import com.orels.app.domain.interactors.CallLogInteractor
import com.orels.app.domain.model.entities.CallLogEntity
import com.orels.app.domain.model.entities.PhoneCall
import kotlinx.coroutines.delay
import java.util.*

class StubCallLogInteractor : CallLogInteractor {

    private val callLog: ArrayList<CallLogEntity> = ArrayList()

    fun addToCallLog(callLogEntity: CallLogEntity) =
        callLog.add(callLogEntity)


    fun addToCallLog(callLogEntity: List<CallLogEntity>) =
        callLog.addAll(callLogEntity)


    override fun getTodaysCallLog(): ArrayList<CallLogEntity> = ArrayList()

    override fun getCallLogsByDate(startDate: Date, endDate: Date): ArrayList<CallLogEntity> =
        ArrayList()

    override suspend fun getLastCallLog(delay: Long): CallLogEntity {
        delay(delay)
        return callLog.last()
    }

    override fun update(phoneCall: PhoneCall): PhoneCall? = null

}