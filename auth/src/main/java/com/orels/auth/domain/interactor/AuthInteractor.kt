package com.orels.auth.domain.interactor

import androidx.annotation.RawRes
import com.orels.auth.domain.model.ResetPasswordStep
import com.orels.auth.domain.model.SignInStep
import com.orels.auth.domain.model.SignUpStep
import com.orels.auth.domain.model.User
import com.orels.auth.domain.model.exception.*
import kotlinx.coroutines.flow.Flow
import java.security.InvalidParameterException

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
     * @throws [UserNotConfirmedException] if the user is not confirmed.
     * @throws [UserNotFoundException] if the user is not found.
     * @throws [InvalidParameterException] if the username or password is invalid.
     * @throws [WrongCredentialsException] if the username or password is wrong.
     * @throws [Exception] if an unknown exception occurred.
     * @return The user.
     */
    @Throws(
        UserNotConfirmedException::class,
        UserNotFoundException::class,
        InvalidParameterException::class,
        WrongCredentialsException::class,
        Exception::class
    )
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
     * @throws [UserNotFoundException] if the user is not found.
     * @throws [LimitExceededException] if the limit of sending emails was exceeded.
     * @throws [NotAuthorizedException] if the user is not authorized.
     * @throws [Exception] if an unknown exception occurred.
     */
    @Throws(
        LimitExceededException::class,
        UserNotFoundException::class,
        NotAuthorizedException::class,
        Exception::class
    )
    suspend fun forgotPassword(username: String): ResetPasswordStep

    /**
     * Should be called after forgotPassword to restore the user's password with a code in the email.
     * @param username The username of the user.
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

    suspend fun isLoggedIn(): Boolean

    /**
     *  Checks if user in db is not null.
     *  If not null, returns as flow of [UserState.LoggedIn] else returns as flow of [UserState.LoggedOut]
     *  @return Flow of UserState
     */
    suspend fun getUserState(): Flow<UserState>

    fun isPasswordValid(password: String): Boolean
}

enum class UserState {
    Loading,
    LoggedIn,
    LoggedOut,
    Blocked;
}