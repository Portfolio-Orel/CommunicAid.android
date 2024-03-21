package com.orels.auth.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author Orel Zilberman
 * 06/12/2022
 */
@Entity
data class User(
    @PrimaryKey var userId: String = "",
    var token: String? = null,
    val username: String? = null,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
) {

    override fun equals(other: Any?): Boolean = other is User && (userId == other.userId
            && token == other.token
            && username == other.username
            && email == other.email
            && firstName == other.firstName
            && lastName == other.lastName)

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + (token?.hashCode() ?: 0)
        result = 31 * result + (username?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (firstName?.hashCode() ?: 0)
        result = 31 * result + (lastName?.hashCode() ?: 0)
        return result
    }


    companion object {
        val LOGGED_OUT_USER = User()
    }
}