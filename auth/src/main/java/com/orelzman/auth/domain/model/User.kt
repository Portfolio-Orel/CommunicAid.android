package com.orelzman.auth.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val userId: String = "",
    val token: String = "",
    val email: String = "",
    var state: UserState = UserState.NotAuthorized
) {
    companion object {
        fun blocked(): User =
            User(state = UserState.Blocked)

        fun notAuthorized(): User =
            User(state = UserState.NotAuthorized)
    }
}

enum class UserState {
    NotAuthorized,
    Authorized,
    Blocked;
}