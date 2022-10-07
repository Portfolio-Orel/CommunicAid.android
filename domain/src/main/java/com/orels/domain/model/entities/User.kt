package com.orels.domain.model.entities

data class User(
    val userId: String = "",
    val token: String = "",
    val email: String = "",
    val username: String = "",
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

enum class UserState { // ToDo: Understand if needed
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