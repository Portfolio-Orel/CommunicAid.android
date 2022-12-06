package com.orels.auth.domain.model

/**
 * @author Orel Zilberman
 * 06/12/2022
 */
data class User(
    var userId: String = "",
    var token: String? = null,
    val username: String? = null,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null
)