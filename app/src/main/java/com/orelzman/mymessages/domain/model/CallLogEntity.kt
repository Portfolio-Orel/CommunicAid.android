package com.orelzman.mymessages.domain.model

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

    val phoneCall: PhoneCall
        get() =
            PhoneCall(
                number = number,
                startDate = Date(),
                endDate = dateMilliseconds.toLong().toDate()
            )

}
