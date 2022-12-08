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
)