package com.orels.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.orels.domain.interactors.CallType
import com.orels.domain.util.extension.epochTimeInSeconds
import java.util.*

@Entity
data class PhoneCall(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var number: String = "",
    var startDate: Date, // The date the call was registered in the phone
    var endDate: Date, // startDate + call duration
    var name: String? = null,
    var isWaiting: Boolean = false,
    var type: String = CallType.INCOMING.name,
    var actualEndDate: Date? = null, // The time the call ended
    var messagesSent: List<MessageSent> = emptyList(),
) : Loggable, Uploadable() {

    init {
        // Sometimes endDate is a smaller than startDate in a few milliseconds
        // when the call is rejected/missed/blocked
        if (endDate < startDate) {
            endDate = startDate
        }
    }

    fun getNameOrNumber(): String = name ?: number

    override val data: Map<String, Any>
        get() = mapOf(
            "number" to number,
            "start_date" to "startDate.time",
            "end_date" to "endDate.time",
            "name" to (name ?: ""),
            "is_waiting" to isWaiting,
            "messages_sent" to messagesSent.map { it.data },
            "type" to type
        )

    val isAnswered: Boolean
        get() = (startDate.time.epochTimeInSeconds != endDate.time.epochTimeInSeconds)

    fun missed() {
        type = CallType.MISSED.name
    }

    fun rejected() {
        type = CallType.REJECTED.name
    }

    fun stringify(): String? {
        val string = Gson().toJson(this)
        if (string == "null") return null
        return string
    }


    companion object {
        fun waiting(number: String) =
            PhoneCall(
                number = number,
                isWaiting = true,
                type = CallType.INCOMING.name,
                startDate = Date(),
                endDate = Date(),
                actualEndDate = Date()
            )

        fun incoming(number: String) =
            PhoneCall(
                number = number,
                isWaiting = false,
                type = CallType.INCOMING.name,
                startDate = Date(),
                endDate = Date(),
                actualEndDate = Date()
            )

        fun outgoing(number: String) =
            PhoneCall(
                number = number,
                isWaiting = false,
                type = CallType.OUTGOING.name,
                startDate = Date(),
                endDate = Date(),
                actualEndDate = Date()
            )
    }
}

fun String.toPhoneCall(): PhoneCall? {
    return try {
        Gson().fromJson(this, PhoneCall::class.java)
    } catch (e: Exception) {
        null
    }
}
