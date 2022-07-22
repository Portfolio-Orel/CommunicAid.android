package com.orelzman.mymessages.domain.model.entities

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.orelzman.mymessages.domain.interactors.CallType
import com.orelzman.mymessages.domain.model.dto.body.create.CreatePhoneCallBody
import com.orelzman.mymessages.util.common.ContactsUtil
import com.orelzman.mymessages.util.extension.inSeconds
import java.util.*

@Entity
data class PhoneCall(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    val number: String = "",
    var startDate: Date,
    var endDate: Date,
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
        ContactsUtil.getContactName(number, context)


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
            PhoneCall(number = number, isWaiting = true, type = CallType.INCOMING.name, startDate = Date(), endDate = Date())

        fun incoming(number: String) =
            PhoneCall(number = number, isWaiting = false, type = CallType.INCOMING.name, startDate = Date(), endDate = Date())

        fun outgoing(number: String) =
            PhoneCall(number = number, isWaiting = false, type = CallType.OUTGOING.name, startDate = Date(), endDate = Date())
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

fun String.toPhoneCall(): PhoneCall? =
    Gson().fromJson(this, PhoneCall::class.java)
