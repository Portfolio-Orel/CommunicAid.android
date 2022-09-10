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

    override fun equals(other: Any?): Boolean = if (other !is User) false
    else other.userId == userId && other.token == token && other.email == email

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + token.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + state.hashCode()
        return result
    }

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