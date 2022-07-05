package com.orelzman.mymessages.domain.model.dto.response

import com.google.gson.annotations.SerializedName

data class GetUserResponse(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String,
    @SerializedName("number") val number: String,
    @SerializedName("id") val userId: String,
)