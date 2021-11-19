package com.orelzman.auth.domain.interactor

import com.google.firebase.auth.FirebaseUser
import com.orelzman.auth.domain.model.User
import io.reactivex.rxjava3.core.Single

interface AuthInteractor {

    /**
     * The authenticated user.
     * @author Orel Zilberman, 19.11.2021
     */
    val user: FirebaseUser?

    /**
     * Checks if there is an authenticated user.
     * @return a single with true if authenticated and false otherwise.
     * @author Orel Zilberman, 19.11.2021
     */
    fun isAuth(): Single<Boolean>

    /**
     * Authenticates a user by [email] and [password].
     * @return Single with User entity as type emitted.
     * @author Orel Zilberman, 19.11.2021.
     */
    suspend fun auth(email: String, password: String, isSaveCredentials: Boolean): User

    /**
     * Authenticates a user with Google.
     * @return Single with User entity as type emitted.
     * @author Orel Zilberman, 19.11.2021.
     */
//    suspend fun googleAuth(): Completable

    /**
     * Checks if the credentials entered are valid
     * according to the policy. TODO
     * @author Orel Zilberman, 19.11.2021
     */
    fun isValidCredentials(email: String, password: String): Boolean
}