package com.orels.domain.model.entities

import com.orels.domain.interactors.CallType
import com.orels.domain.util.extension.*
import java.util.*

class CallLogEntity(
    var number: String = "",
    var duration: Long = 0,
    var name: String = "",
    var time: Long = 0,
    var callLogType: CallType? = null
) {

    fun isMissed(): Boolean = callLogType == CallType.MISSED

    fun isRejected(): Boolean = callLogType == CallType.REJECTED

    val phoneCall: PhoneCall
        get() =
            PhoneCall(
                number = number,
                name = name,
                startDate = time.toDate(),
                endDate = (time + duration).toDate(),
                actualEndDate = (time + duration).toDate(),
                type = CallType.INCOMING.name
            )
}

val ArrayList<CallLogEntity>.numbers: List<String>
    get() = map { it.number }

fun List<CallLogEntity>.getUnhandledCalls(countRejectedAsUnhandled: Boolean = false): List<CallLogEntity> =
    filter { it.isMissed() || (countRejectedAsUnhandled && it.isRejected()) }

fun ArrayList<CallLogEntity>.addUniqueByNumber(element: CallLogEntity) {
    if (!numbers.containsNumber(element.number)) add(element)
}

fun ArrayList<CallLogEntity>.removeByNumber(element: CallLogEntity) {
    filter { it.number.withoutPrefix() != element.number.withoutPrefix() }
}

fun List<CallLogEntity>.toPhoneCalls(): List<PhoneCall> {
    val array = ArrayList<PhoneCall>()
    forEach {
        val endDate =
            Date((it.time.epochTimeInSeconds + it.duration.epochTimeInSeconds).epochTimeInMilliseconds)
        array.add(
            PhoneCall(
                number = it.number,
                startDate = Date(it.time.epochTimeInMilliseconds),
                endDate = endDate,
                actualEndDate = endDate,
                name = it.name,
                type = it.callLogType?.name ?: CallType.INCOMING.name,
            )
        )
    }
    return array
}
