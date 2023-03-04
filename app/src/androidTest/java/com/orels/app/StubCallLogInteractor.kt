package com.orels.app

import com.orels.domain.interactors.CallDetailsInteractor
import com.orels.domain.model.entities.CallLogEntity
import com.orels.domain.model.entities.PhoneCall
import kotlinx.coroutines.delay
import java.util.*

class StubCallLogInteractor : CallDetailsInteractor {

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

    override fun getLastCallLog(): CallLogEntity? {
        TODO("Not yet implemented")
    }

    override fun update(phoneCall: PhoneCall): PhoneCall? = null
    override fun getContactName(number: String): String {
        TODO("Not yet implemented")
    }

}