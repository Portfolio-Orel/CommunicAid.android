package com.orels.auth.domain.interactor

import androidx.annotation.RawRes
import com.amplifyframework.auth.result.AuthSignUpResult
import com.orels.auth.domain.ResetPasswordStep
import com.orels.auth.domain.SignInStep
import com.orels.auth.domain.SignUpStep
import com.orels.auth.domain.User
import com.orels.auth.domain.exception.*
import kotlinx.coroutines.flow.Flow

/**
 * @author Orel Zilberman
 * 06/12/2022
 */
interface Auth {

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
     * @throws [AuthException.UserNotConfirmedException] if the user is not confirmed.
     * @throws [AuthException.UserNotFoundException] if the user is not found.
     * @throws [AuthException.WrongCredentialsException] if the username or password is wrong.
     * @throws [Exception] if an unknown exception occurred.
     * @return The user.
     */
    @Throws(
        AuthException.UserNotConfirmedException::class,
        AuthException.UserNotFoundException::class,
        AuthException.WrongCredentialsException::class,
        Exception::class
    )
    suspend fun login(username: String, password: String): SignInStep

    /**
     * Called after first login with temporary password to change the password.
     * @param newPassword The new password.
     */
    suspend fun confirmSignInWithNewPassword(newPassword: String): SignInStep

    /**
     * Should be called to logout a user.
     */
    suspend fun logout()

    /**
     * Should be called to register a user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param phoneNumber The phone number of the user.
     * @param email The email of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @return The user.
     */
    suspend fun register(
        username: String,
        password: String,
        phoneNumber: String,
        email: String,
        firstName: String,
        lastName: String
    ): SignUpStep

    /**
     * Should be called to register a user with a phone number.
     * @param phoneNumber The phone number of the user.
     * @param email The email of the user.
     * @throws [AuthException.UsernameExistsException] if the username already exists.
     * @throws [AuthException.CodeDeliveryFailureException] if the code delivery failed.
     */
    suspend fun registerWithPhone(phoneNumber: String, email: String): SignUpStep

    /**
     * Called after a user was registered to confirm the phone number.
     * @param phoneNumber The phone number of the user.
     * @param code The code sent to the user's phone.
     * @throws [AuthException.CodeMismatchException] if the code is wrong.
     * @throws [AuthException.CodeExpiredException] if the code is expired.
     * @throws [AuthException.NotAuthorizedException] if the user is not authorized.
     */
    suspend fun confirmSignUpWithPhone(phoneNumber: String, code: String): SignUpStep

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
     * @throws [AuthException.UserNotFoundException] if the user is not found.
     * @throws [AuthException.LimitExceededException] if the limit of sending emails was exceeded.
     * @throws [AuthException.NotAuthorizedException] if the user is not authorized.
     * @throws [Exception] if an unknown exception occurred.
     */
    @Throws(
        AuthException.LimitExceededException::class,
        AuthException.UserNotFoundException::class,
        AuthException.NotAuthorizedException::class,
        Exception::class
    )
    suspend fun forgotPassword(username: String): ResetPasswordStep

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

    suspend fun refreshToken(): String?

    fun getUser(): User?

    /**
     * Updates user in db and in remote.
     * ONLY updates fields that are not null.
     * @param user The user to update.
     */
    suspend fun updateUser(user: User)

    suspend fun isLoggedIn(): Boolean

    /**
     *  Checks if user in db is not null.
     *  If not null, returns as flow of [UserState.LoggedIn] else returns as flow of [UserState.LoggedOut]
     *  @return Flow of UserState
     */
    suspend fun getUserState(): Flow<UserState>

    suspend fun resendConfirmationCode(phoneNumber: String): AuthSignUpResult

    fun isPasswordValid(password: String): Boolean
}

@Suppress("unused")
enum class UserState {
    Loading,
    LoggedIn,
    LoggedOut,
    Blocked;
}