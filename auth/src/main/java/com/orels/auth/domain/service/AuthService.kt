package com.orels.auth.domain.service

import androidx.annotation.RawRes
import com.amplifyframework.auth.result.AuthResetPasswordResult
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.orels.auth.domain.User
import com.orels.auth.domain.exception.*

/**
 * @author Orel Zilberman
 * 06/12/2022
 */
interface AuthService {
    /*
        initializes the auth services.
        MUST BE CALLED BEFORE ANY OTHER FUNCTION
        @param configFileResourceId - the id of the config file
     */
    suspend fun initialize(@RawRes configFileResourceId: Int)

    /**
     * Should be called to login a user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The user.
     */
    suspend fun login(username: String, password: String): AuthSignInResult

    /**
     * Confirms user first sign in with temporary password.
     * @param newPassword The new password of the user.
     */
    suspend fun confirmSignInWithNewPassword(newPassword: String): AuthSignInResult

    suspend fun logout()

    /**
     * Should be called to register a user.
     * @param email The email of the user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @return The user.
     */
    suspend fun register(email: String, username: String, password: String, firstName: String, lastName: String): AuthSignUpResult

    /**
     * Should be called to register a user with a phone number.
     * @param phoneNumber The phone number of the user.
     * @param email The email of the user.
     * @throws [UsernameExistsException] if the username already exists.
     * @throws [CodeDeliveryFailureException] if the code delivery failed.
     */
    suspend fun registerWithPhone(phoneNumber: String, email: String): AuthSignUpResult

    /**
     * Called after a user was registered to confirm the phone number.
     * @param phoneNumber The phone number of the user.
     * @param code The code sent to the user's phone.
     * @throws [CodeMismatchException] if the code is wrong.
     * @throws [CodeExpiredException] if the code is expired.
     * @throws [NotAuthorizedException] if the user is not authorized.
     */
    suspend fun confirmSignUpWithPhone(phoneNumber: String, code: String): AuthSignUpResult

    /**
     * Called after a user was registered to confirm the email.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param code The code sent to the user's email.
     * @return The user.
     */
    suspend fun confirmUserRegistration(username: String, password: String, code: String): AuthSignUpResult

    /**
     * Should be called to restore the user's password with a code in the email.
     * @param username The username of the user.
     */
    suspend fun forgotPassword(username: String): AuthResetPasswordResult

    /**
     * Should be called after forgotPassword to restore the user's password with a code in the email.
     * @param code The code sent to the user's email.
     * @param newPassword The new password.
     */
    suspend fun resetPassword(code: String, newPassword: String)

    /**
     * Should be called to get the user's token.
     * @return The user's token.
     */
    suspend fun getToken(): String?

    suspend fun getUser(): User?

    /**
     * Resends a confirmation code to the user's phone number.
     * @param phoneNumber The phone number of the user.
     */
    suspend fun resendConfirmationCode(phoneNumber: String): AuthSignUpResult

    /**
     * Checks if the user the user is logged in.
     * @return True if the user is logged in, false otherwise.
     */
    suspend fun isLoggedIn(): Boolean
}