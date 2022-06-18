package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.orelzman.mymessages.domain.service.inSeconds
import java.util.*

@Entity
data class PhoneCall(
    val number: String = "",
    @PrimaryKey var startDate: Date = Date(),
    var endDate: Date = startDate,
    val isIncoming: Boolean = false,
    val isWaiting: Boolean = false,
    var isRejected: Boolean = false,
    var messagesSent: List<String> = emptyList()
) : DTO {

    val isAnswered: Boolean
        get() = (startDate.time.inSeconds != endDate.time.inSeconds)

    var name: String = ""
        get() = number

    fun copy(phoneCall: PhoneCall?): PhoneCall? =
        if (phoneCall == null) null
        else PhoneCall(
            number = phoneCall.number,
            startDate = phoneCall.startDate,
            endDate = phoneCall.endDate,
            isIncoming = phoneCall.isIncoming,
            isWaiting = phoneCall.isIncoming,
            isRejected = phoneCall.isRejected,
            messagesSent = phoneCall.messagesSent
        )

    override val data: Map<String, Any> =
        mapOf(
            "phoneNumber" to number,
            "contactName" to name,
            "startDate" to startDate,
            "endDate" to endDate,
            "isAnswered" to isAnswered,
            "isIncoming" to isIncoming,
            "isWaiting" to isWaiting,
            "isRejected" to isRejected,
            "messagesSent" to messagesSent
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

val List<Map<String, Any>?>.phoneCalls: List<PhoneCall>
    get() {
        val phoneCalls = ArrayList<PhoneCall>()
        for (item in this) {
            phoneCalls.add(
                PhoneCall(
                    number = item?.get("phoneNumber") as? String ?: "",
                    startDate = item?.get("startDate") as? Date ?: Date(),
                    endDate = item?.get("endDate") as? Date ?: Date(),
                )
            )
        }
        return phoneCalls
    }