package com.orelzman.auth.domain.interactor

import android.app.Activity
import androidx.annotation.RawRes
import com.orelzman.auth.domain.model.AuthLogger
import com.orelzman.auth.domain.model.ResetPasswordStep
import com.orelzman.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthInteractor {

    suspend fun init(@RawRes configFileResourceId: Int, authLogger: AuthLogger? = null)
    suspend fun signOut()
    suspend fun signUp(
        email: String,
        username: String,
        password: String,
        isSaveCredentials: Boolean = false
    )
    suspend fun signIn(
        username: String = "",
        password: String = "",
        isSaveCredentials: Boolean = false
    )

    suspend fun refreshToken()

    /**
     * Used after sign up.
     */
    suspend fun confirmUser(
        username: String,
        password: String,
        code: String
    )

    suspend fun forgotPassword(
        username: String
    ): ResetPasswordStep

    suspend fun confirmResetPassword(
        code: String,
        password: String
    )

    suspend fun googleAuth(activity: Activity)

    fun getUser(): User?
    fun getUserFlow(): Flow<User?>

    /**
     * Checks if the user is authorized against the user pool.
     */
    @Throws(Exception::class)
    suspend fun isAuthorized(user: User?): Boolean
    /**
     * Checks if the credentials entered are valid
     * according to the policy. TODO
     * @author Orel Zilberman, 19.11.2021
     */
    fun isValidCredentials(email: String, password: String): Boolean
}