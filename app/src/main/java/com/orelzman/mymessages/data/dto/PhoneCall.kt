package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.orelzman.mymessages.data.remote.repository.dto.CreatePhoneCallBody
import com.orelzman.mymessages.domain.service.inSeconds
import com.orelzman.mymessages.util.CallType
import java.util.*

@Entity
data class PhoneCall(
    @PrimaryKey val id: String = "",
    val number: String = "",
    var startDate: Date = Date(),
    var endDate: Date = startDate,
    var name: String = "",
    var isWaiting: Boolean = false,
    var messagesSent: List<MessageSent> = emptyList(),
    var type: String = CallType.INCOMING.name
) {

    val isAnswered: Boolean
        get() = (startDate.time.inSeconds != endDate.time.inSeconds)

    fun missed() {
        type = CallType.MISSED.name
    }

    fun rejected() {
        type = CallType.REJECTED.name
    }

    companion object {
        fun waiting(number: String) =
            PhoneCall(number = number, isWaiting = true, type = CallType.INCOMING.name)

        fun incoming(number: String) =
            PhoneCall(number = number, isWaiting = false, type = CallType.INCOMING.name)

        fun outgoing(number: String) =
            PhoneCall(number = number, isWaiting = false, type = CallType.OUTGOING.name)
    }
}

fun List<PhoneCall>.createPhoneCallBodyList(userId: String): List<CreatePhoneCallBody> {
    val array = ArrayList<CreatePhoneCallBody>()
    forEach { it ->
        with(it) {
            array.add(
                CreatePhoneCallBody(
                    number = number,
                    contactName = name,
                    startDate = startDate.time,
                    endDate = endDate.time,
                    isAnswered = isAnswered,
                    type = type,
                    messagesSent = messagesSent.map { messageSent -> messageSent.createMessageSentBody },
                    userId = userId
                )
            )
        }
    }
    return array
}