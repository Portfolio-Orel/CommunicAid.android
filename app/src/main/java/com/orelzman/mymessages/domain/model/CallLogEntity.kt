package com.orelzman.mymessages.domain.model

import android.os.Build
import com.orelzman.mymessages.data.dto.PhoneCall
import com.orelzman.mymessages.util.CallType
import com.orelzman.mymessages.util.extension.toDate
import java.util.*

class CallLogEntity(
    val number: String = "",
    val duration: Long = 0,
    val name: String = "",
    var dateMilliseconds: Long = 0,
    val callLogType: CallType? = null
) {

    fun isUnhandled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            callLogType == CallType.REJECTED || callLogType == CallType.MISSED
        } else {
            callLogType == CallType.MISSED
        }
    }

    val phoneCall: PhoneCall
        get() =
            PhoneCall(
                number = number,
                startDate = Date(),
                endDate = dateMilliseconds.toLong().toDate()
            )
}

val ArrayList<CallLogEntity>.numbers: List<String>
    get() = map { it.number }

val List<CallLogEntity>.unhandledCalls: List<CallLogEntity>
    get() = filter { it.isUnhandled() }

fun ArrayList<CallLogEntity>.addUniqueByNumber(element: CallLogEntity) {
    if (!numbers.contains(element.number)) add(element)
}

fun ArrayList<CallLogEntity>.removeByNumber(element: CallLogEntity) {
    filter{it.number != element.number}
}
