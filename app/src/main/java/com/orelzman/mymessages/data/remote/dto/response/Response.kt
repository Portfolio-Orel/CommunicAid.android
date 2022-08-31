package com.orelzman.mymessages.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class Response<T>(
    @SerializedName("body") val body: T,
    @SerializedName("message") val message: String,
)