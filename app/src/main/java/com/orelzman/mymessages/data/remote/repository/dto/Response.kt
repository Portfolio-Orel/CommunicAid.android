package com.orelzman.mymessages.data.remote.repository.dto

import com.google.gson.annotations.SerializedName

data class Response<T>(
    @SerializedName("body") val body: T
)