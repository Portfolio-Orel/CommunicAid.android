package com.orels.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey var userId: String = "",
    var token: String = "",
    var email: String = "",
    var username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
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

    @Suppress("unused")
    companion object {
        fun blocked(): User =
            User(state = UserState.Blocked)

        fun notAuthorized(): User =
            User(state = UserState.NotAuthorized)
    }
}

@Suppress("unused")
enum class UserState {
    NotAuthorized,
    Authorized,
    Blocked;
}

/**
 * @author Orel Zilberman
 * 19/08/2022
 */
interface DropdownItem {
    fun getIdentifier(): String
    fun getValue(): String
}