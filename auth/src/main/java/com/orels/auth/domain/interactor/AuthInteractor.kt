package com.orels.auth.domain.interactor

import androidx.annotation.RawRes
import com.orels.auth.domain.model.ResetPasswordStep
import com.orels.auth.domain.model.SignInStep
import com.orels.auth.domain.model.SignUpStep
import com.orels.auth.domain.model.User

/**
 * @author Orel Zilberman
 * 06/12/2022
 */
interface AuthInteractor {

    /**
     * Initialize the authentication service
     * MUST BE CALLED BEFORE ANY OTHER METHOD
     * @param configFileResourceId The resource id of the configuration file
     */
    suspend fun initialize(@RawRes configFileResourceId: Int)

    /**
     * Should be called to login a user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The user.
     */
    suspend fun login(username: String, password: String): SignInStep

    /**
     * Should be called to logout a user.
     */
    suspend fun logout()

    /**
     * Should be called to register a user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param email The email of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @return The user.
     */
    suspend fun register(username: String, password: String, email: String, firstName: String, lastName: String): SignUpStep

    /**
     * Called after a user was registered to confirm the email.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param code The code sent to the user's email.
     * @return The user.
     */
    suspend fun confirmUser(username: String, password: String, code: String): SignUpStep

    /**
     * Should be called to restore the user's password with a code in the email.
     * @param username The username of the user.
     */
    suspend fun forgotPassword(username: String): ResetPasswordStep

    /**
     * Should be called after forgotPassword to restore the user's password with a code in the email.
     * @param username The username of the user.
     * @param code The code sent to the user's email.
     * @param newPassword The new password.
     */
    suspend fun resetPassword(username: String, code: String, newPassword: String)

    /**
     * Should be called to get the user's token.
     * @return The user's token.
     */
    suspend fun getToken(): String?

    fun getUser(): User?

}