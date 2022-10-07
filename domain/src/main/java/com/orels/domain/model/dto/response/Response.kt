package com.orels.domain.model.dto.response

import com.google.gson.annotations.SerializedName

data class Response<T>(
    @SerializedName("body") val body: T,
    @SerializedName("message") val message: String,
)