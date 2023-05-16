package com.orels.domain.model.dto.body.create

import com.google.gson.annotations.SerializedName
import com.orels.domain.model.entities.User

data class CreateUserBody(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String,
    @SerializedName("number") val number: String,
    @SerializedName("user_id") val userId: String,
) {
    constructor(user: User) : this(
        firstName = user.firstName,
        lastName = user.lastName,
        gender = user.gender.name,
        email = user.email,
        number = user.phoneNumber,
        userId = user.userId
    )
}