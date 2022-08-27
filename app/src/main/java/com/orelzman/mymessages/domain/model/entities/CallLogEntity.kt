package com.orelzman.mymessages.domain.model.entities

import com.orelzman.mymessages.domain.interactors.CallType
import com.orelzman.mymessages.domain.util.extension.*
import java.util.*

class CallLogEntity(
    var number: String = "",
    val duration: Long = 0,
    val name: String = "",
    var time: Long = 0,
    val callLogType: CallType? = null
) {

    fun isMissed(): Boolean = callLogType == CallType.MISSED

    fun isRejected(): Boolean = callLogType == CallType.REJECTED

    val phoneCall: PhoneCall
        get() =
            PhoneCall(
                number = number,
                startDate = time.toDate(),
                endDate = (time + duration).toDate(),
                type = CallType.INCOMING.name
            )
}

val ArrayList<CallLogEntity>.numbers: List<String>
    get() = map { it.number }

fun List<CallLogEntity>.getUnhandledCalls(countRejectedAsUnhandled: Boolean = false): List<CallLogEntity> =
    filter { it.isMissed() || (countRejectedAsUnhandled && it.isRejected())}

fun ArrayList<CallLogEntity>.addUniqueByNumber(element: CallLogEntity) {
    if (!numbers.containsNumber(element.number)) add(element)
}

fun ArrayList<CallLogEntity>.removeByNumber(element: CallLogEntity) {
    filter { it.number.withoutPrefix() != element.number.withoutPrefix() }
}

fun List<CallLogEntity>.toPhoneCalls(): List<PhoneCall> {
    val array = ArrayList<PhoneCall>()
    forEach {
        array.add(
            PhoneCall(
                number = it.number,
                startDate = Date(it.time.inMilliseconds),
                endDate = Date((it.time.inSeconds + it.duration.inSeconds).inMilliseconds),
                name = it.name,
                type = it.callLogType?.name ?: CallType.INCOMING.name,
            )
        )
    }
    return array
}
