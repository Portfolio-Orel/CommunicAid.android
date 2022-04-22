package com.orelzman.mymessages.data.dto

import java.util.*

data class PhoneCall(
    val number: String = "",
    var startDate: Date = Date(),
    var endDate: Date = Date(),
    var name: String = "",
    val isIncoming: Boolean = false,
    val isWaiting: Boolean = false,
    var isRejected: Boolean = false,
    val messagesSent: List<String> = emptyList()
) : DTO {

    override val data: Map<String, Any> =
        mapOf(
            "phoneNumber" to number,
            "contactName" to name,
            "startDate" to startDate,
            "endDate" to endDate,
            "isAnswered" to (startDate != endDate),
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