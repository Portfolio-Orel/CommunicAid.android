package com.orelzman.mymessages.data.remote.dto.body.create

import com.google.gson.annotations.SerializedName

data class CreateDeletedCallBody(
    @SerializedName("number") val number: String,
    @SerializedName("deleted_at") val deleteDate: Long
)