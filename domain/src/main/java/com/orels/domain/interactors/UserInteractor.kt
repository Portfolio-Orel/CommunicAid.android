package com.orels.domain.interactors

import android.app.Activity
import androidx.annotation.RawRes
import com.orels.domain.model.entities.User
import kotlinx.coroutines.flow.Flow

/**
 * @author Orel Zilberman
 * 28/09/2022
 */
interface UserInteractor {

    suspend fun init(@RawRes configFileResourceId: Int)
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

    suspend fun googleAuth(activity: Activity)

    fun getUser(): User?
    fun getUserFlow(): Flow<User?>

    /**
     * Checks if the user is authorized against the user pool.
     */
    suspend fun isAuthorized(user: User?): Boolean
    /**
     * Checks if the credentials entered are valid
     * according to the policy. TODO
     * @author Orel Zilberman, 19.11.2021
     */
    fun isValidCredentials(email: String, password: String): Boolean
}