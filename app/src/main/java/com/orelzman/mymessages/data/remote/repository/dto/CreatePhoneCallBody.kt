package com.orelzman.mymessages.data.remote.repository.dto

import com.google.gson.annotations.SerializedName

data class CreatePhoneCallBody(
    @SerializedName("number") val number: String,
    @SerializedName("contact_name") val contactName: String,
    @SerializedName("start_date") val startDate: Long,
    @SerializedName("end_date") val endDate: Long,
    @SerializedName("is_answered") val isAnswered: Boolean,
    @SerializedName("type") val type: String,
    @SerializedName("messages_sent") val messagesSent: List<String>,
    @SerializedName("user_id") val userId: String,
)