package com.orelzman.auth.domain.model

data class User(
    val userId: String = "",
    val token: String = "",
    val email: String = ""
)
