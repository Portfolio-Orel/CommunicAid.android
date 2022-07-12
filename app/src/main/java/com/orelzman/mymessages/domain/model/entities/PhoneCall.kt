package com.orelzman.mymessages.domain.model.entities

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.orelzman.mymessages.domain.model.dto.body.create.CreatePhoneCallBody
import com.orelzman.mymessages.util.common.CallType
import com.orelzman.mymessages.util.common.CallUtils
import com.orelzman.mymessages.util.extension.inSeconds
import java.util.*

@Entity
data class PhoneCall(
    @PrimaryKey var id: String = "",
    val number: String = "",
    var startDate: Date = Date(),
    var endDate: Date = startDate,
    var name: String = "",
    var isWaiting: Boolean = false,
    var messagesSent: List<MessageSent> = emptyList(),
    var type: String = CallType.INCOMING.name,
    var uploadState: UploadState = UploadState.NotUploaded
) : Loggable {

    override val data: Map<String, Any>
        get() = mapOf(
            "number" to number,
            "start_date" to "startDate.time",
            "end_date" to "endDate.time",
            "name" to name,
            "is_waiting" to isWaiting,
            "messages_sent" to messagesSent.map { it.data },
            "type" to type
        )

    val isAnswered: Boolean
        get() = (startDate.time.inSeconds != endDate.time.inSeconds)

    fun getName(context: Context): String =
        CallUtils.getContactName(number, context)


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

enum class UploadState(val value: String) {
    NotUploaded("NotUploaded"),
    BeingUploaded("BeingUploaded"),
    Uploaded("Uploaded");

    companion object {
        fun fromString(value: String): UploadState {
            values().forEach {
                if (it.value == value) return it
            }
            return NotUploaded
        }
    }
}