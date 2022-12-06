package com.orels.auth.data.remote

import com.orels.auth.domain.model.User
import com.orels.auth.domain.service.AuthService

/**
 * @author Orel Zilberman
 * 06/12/2022
 */
class AuthServiceImpl: AuthService {
    override suspend fun login(email: String, password: String): User {
        TODO("Not yet implemented")
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }

    override suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): User {
        TODO("Not yet implemented")
    }

    override suspend fun confirmUser(email: String, password: String, code: String): User {
        TODO("Not yet implemented")
    }

    override suspend fun forgotPassword(email: String) {
        TODO("Not yet implemented")
    }

    override suspend fun resetPassword(email: String, code: String, newPassword: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getToken(): String? {
        TODO("Not yet implemented")
    }
}