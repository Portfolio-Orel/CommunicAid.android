package com.orels.data.interactors

import android.app.Activity
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.auth.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @author Orel Zilberman
 * 28/09/2022
 */
class UserInteractorImpl @Inject constructor(

): AuthInteractor {
    override suspend fun init(configFileResourceId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun signOut() {
        TODO("Not yet implemented")
    }

    override suspend fun signUp(
        email: String,
        username: String,
        password: String,
        isSaveCredentials: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun signIn(username: String, password: String, isSaveCredentials: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun refreshToken() {
        TODO("Not yet implemented")
    }

    override suspend fun confirmUser(username: String, password: String, code: String) {
        TODO("Not yet implemented")
    }

    override suspend fun googleAuth(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun getUser(): User? {
        TODO("Not yet implemented")
    }

    override fun getUserFlow(): Flow<User?> {
        TODO("Not yet implemented")
    }

    override suspend fun isAuthorized(user: User?): Boolean {
        TODO("Not yet implemented")
    }

    override fun isValidCredentials(email: String, password: String): Boolean {
        TODO("Not yet implemented")
    }
}