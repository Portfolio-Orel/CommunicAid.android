package com.orelzman.mymessages.domain.model.dto.body.create

import com.google.gson.annotations.SerializedName

data class CreateOrUpdateSettingsBody(
    @SerializedName("key") val key: String,
    @SerializedName("value") val value: String,
    @SerializedName("user_id") val userId: String
)