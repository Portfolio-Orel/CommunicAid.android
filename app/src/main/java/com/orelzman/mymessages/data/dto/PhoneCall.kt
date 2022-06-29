package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.orelzman.mymessages.domain.service.inSeconds
import java.util.*

@Entity
data class PhoneCall(
    @PrimaryKey val id: String = "",
    val number: String = "",
    var startDate: Date = Date(),
    var endDate: Date = startDate,
    var name: String = "",
    val isIncoming: Boolean = false,
    val isWaiting: Boolean = false,
    var isRejected: Boolean = false,
)  {
    var messagesSent: List<String> = emptyList()

    val isAnswered: Boolean
        get() = (startDate.time.inSeconds != endDate.time.inSeconds)

    fun copy(phoneCall: PhoneCall?): PhoneCall? =
        if(phoneCall == null) null
        else PhoneCall(
            number = phoneCall.number,
            startDate = phoneCall.startDate,
            endDate = phoneCall.endDate,
            name = phoneCall.name,
            isIncoming = phoneCall.isIncoming,
            isWaiting = phoneCall.isIncoming,
            isRejected = phoneCall.isRejected,
        )

    fun missed() {
        isRejected = false
    }

    fun rejected() {
        isRejected = true
    }

    companion object {
        fun waiting(number: String) =
            PhoneCall(number = number, isIncoming = true, isWaiting = true, isRejected = false)

        fun incoming(number: String) =
            PhoneCall(number = number, isIncoming = true, isWaiting = false, isRejected = false)

        fun outgoing(number: String) =
            PhoneCall(number = number, isIncoming = false, isWaiting = false, isRejected = false)
    }
}