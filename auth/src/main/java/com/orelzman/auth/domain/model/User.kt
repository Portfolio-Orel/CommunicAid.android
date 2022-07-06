package com.orelzman.auth.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val userId: String = "",
    val token: String = "",
    val email: String = ""
)