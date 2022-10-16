package com.orelzman.mymessages.data.remote.dto.body.create

import com.google.gson.annotations.SerializedName
import com.orelzman.mymessages.domain.model.entities.PhoneCall

data class CreatePhoneCallBody(
    @SerializedName("number") val number: String,
    @SerializedName("contact_name") val contactName: String,
    @SerializedName("start_date") val startDate: Long,
    @SerializedName("end_date") val endDate: Long,
    @SerializedName("is_answered") val isAnswered: Boolean,
    @SerializedName("type") val type: String,
    @SerializedName("messages_sent") val messagesSent: List<CreateMessageSentBody>,
    @SerializedName("actual_duration") val actualDuration: Float,
)


fun List<PhoneCall>.createPhoneCallBodyList(): List<CreatePhoneCallBody> {
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
                    type = type,
                    messagesSent = messagesSent.map { messageSent -> messageSent.createMessageSentBody },
                    actualDuration = actualDuration
                )
            )
        }
    }
    return array
}