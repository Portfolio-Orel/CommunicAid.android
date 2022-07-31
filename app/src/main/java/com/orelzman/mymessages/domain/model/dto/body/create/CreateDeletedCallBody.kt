package com.orelzman.mymessages.domain.model.dto.body.create

import com.google.gson.annotations.SerializedName

data class CreateDeletedCallBody(
    @SerializedName("number") val number: String,
    @SerializedName("deleted_at") val deleteDate: Long
)