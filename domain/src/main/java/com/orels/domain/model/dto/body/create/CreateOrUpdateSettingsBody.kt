package com.orels.domain.model.dto.body.create

import com.google.gson.annotations.SerializedName

data class CreateOrUpdateSettingsBody(
    @SerializedName("key") val key: String,
    @SerializedName("value") val value: String
)