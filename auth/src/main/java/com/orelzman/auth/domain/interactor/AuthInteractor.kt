package com.orelzman.auth.domain.interactor

import android.app.Activity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.orelzman.auth.domain.model.User
import io.reactivex.rxjava3.core.Single

interface AuthInteractor {

    /**
     * Used for google authentication.
     * @author Orel Zilberman, 28.4.2021
     */
    val signInRequest: BeginSignInRequest

    /**
     * Inits AWS
     */
    suspend fun init()


    /**
     * The authenticated user.
     * @author Orel Zilberman, 19.11.2021
     */
    suspend fun getUser(): User?
    fun getToken(): String
    suspend fun signOut()
    suspend fun signUp(
        email: String = "ezpz0nic@gmail.com",
        username: String = "user123",
        password: String = "password123",
        isSaveCredentials: Boolean = false
    )
    suspend fun signIn(
        username: String = "user123",
        password: String = "password123",
        isSaveCredentials: Boolean = false
    )

    /**
     * Used after sign up.
     */
    suspend fun confirmUser(
        username: String = "user123",
        code: String = "162774"
    )

    suspend fun googleAuth(activity: Activity)

    /**
     * Checks if there is an authenticated user.
     * @return a single with true if authenticated and false otherwise.
     * @author Orel Zilberman, 19.11.2021
     */
    fun isAuth(): Single<Boolean>

    /**
     * Authenticates a user by [email] and [password].
     * @author Orel Zilberman, 19.11.2021.
     */
    suspend fun auth(
        email: String,
        password: String,
        isSaveCredentials: Boolean = false
    ): User?

    /**
     * Authenticates a user with his mail.
     * @author Orel Zilberman, 15.4.2021
     */
//    suspend fun loginWithGmail(): FirebaseUser

    /**
     * Saves a gmail sign in session.
     * @author Orel Zilberman, 19.11.2021.
     */
    suspend fun googleAuth(account: GoogleSignInAccount)

    /**
     * Checks if the credentials entered are valid
     * according to the policy. TODO
     * @author Orel Zilberman, 19.11.2021
     */
    fun isValidCredentials(email: String, password: String): Boolean
}