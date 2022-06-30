package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.orelzman.mymessages.data.remote.repository.dto.CreatePhoneCallBody
import com.orelzman.mymessages.domain.service.inSeconds
import java.util.*

@Entity
data class PhoneCall(
    @PrimaryKey val id: String = "",
    val number: String = "",
    var startDate: Date = Date(),
    var endDate: Date = startDate,
    var name: String = "",
    var type: CallType
) {
    var messagesSent: List<String> = emptyList()

    val isAnswered: Boolean
        get() = (startDate.time.inSeconds != endDate.time.inSeconds)

    fun missed() {
        type = CallType.MISSED
    }

    fun rejected() {
        type = CallType.REJECTED
    }

    companion object {
        fun waiting(number: String) =
            PhoneCall(number = number, type = CallType.WAITING)

        fun incoming(number: String) =
            PhoneCall(number = number, type = CallType.INCOMING)

        fun outgoing(number: String) =
            PhoneCall(number = number, type = CallType.OUTGOING)
    }
}

enum class CallType(name: String) {
    INCOMING("incoming"),
    OUTGOING("outgoing"),
    WAITING("waiting"),
    REJECTED("rejected"),
    MISSED("missed")
}

fun List<PhoneCall>.createPhoneCallBodyList(userId: String): List<CreatePhoneCallBody> {
    val array = ArrayList<CreatePhoneCallBody>()
    forEach {
        with(it) {
            array.add(
                CreatePhoneCallBody(
                    number = number,
                    contactName = name,
                    startDate = startDate.time,
                    endDate = endDate.time,
                    isAnswered = isAnswered,
                    type = type.name,
                    messagesSent = messagesSent,
                    userId = userId
                )
            )
        }
    }
    return array
}