package com.orels.domain.model.dto.body.create

import com.google.gson.annotations.SerializedName

data class CreateUserBody(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String,
    @SerializedName("number") val number: String,
    @SerializedName("user_id") val userId: String,
)