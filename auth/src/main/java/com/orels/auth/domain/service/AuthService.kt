package com.orels.auth.domain.service

import com.orels.auth.domain.model.User

/**
 * @author Orel Zilberman
 * 06/12/2022
 */
interface AuthService {
    /**
     * Should be called to login a user.
     * @param email The email of the user.
     * @param password The password of the user.
     * @return The user.
     */
    suspend fun login(email: String, password: String): User

    /**
     * Should be called to logout a user.
     */
    suspend fun logout()

    /**
     * Should be called to register a user.
     * @param email The email of the user.
     * @param password The password of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @return The user.
     */
    suspend fun register(email: String, password: String, firstName: String, lastName: String): User

    /**
     * Called after a user was registered to confirm the email.
     * @param email The email of the user.
     * @param password The password of the user.
     * @param code The code sent to the user's email.
     * @return The user.
     */
    suspend fun confirmUser(email: String, password: String, code: String): User

    /**
     * Should be called to restore the user's password with a code in the email.
     * @param email The email of the user.
     */
    suspend fun forgotPassword(email: String)

    /**
     * Should be called after forgotPassword to restore the user's password with a code in the email.
     * @param email The email of the user.
     * @param code The code sent to the user's email.
     * @param newPassword The new password.
     */
    suspend fun resetPassword(email: String, code: String, newPassword: String)

    /**
     * Should be called to get the user's token.
     * @return The user's token.
     */
    suspend fun getToken(): String?
}