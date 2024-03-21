package com.orels.domain.model.dto.response

import com.google.gson.annotations.SerializedName
import com.orels.domain.model.entities.User

data class GetUserResponse(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String,
    @SerializedName("number") val number: String,
    @SerializedName("id") val userId: String,
)

fun GetUserResponse.toUser(): User = User(
    firstName = firstName,
    lastName = lastName,
    email = email,
    phoneNumber = number,
    userId = userId,
)