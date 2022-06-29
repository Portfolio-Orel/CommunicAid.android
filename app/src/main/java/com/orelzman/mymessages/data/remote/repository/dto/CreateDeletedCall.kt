package com.orelzman.mymessages.data.remote.repository.dto

import com.google.gson.annotations.SerializedName

data class CreateDeletedCall(
    @SerializedName("number") val number: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("deleted_at_date") val deleteDate: Long
)