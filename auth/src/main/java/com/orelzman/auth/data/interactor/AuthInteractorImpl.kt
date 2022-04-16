package com.orelzman.auth.data.interactor

import com.google.firebase.auth.FirebaseUser
import com.orelzman.auth.data.repository.AuthRepository
import com.orelzman.auth.domain.exception.UsernamePasswordAuthException
import com.orelzman.auth.domain.interactor.AuthInteractor
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthInteractorImpl @Inject constructor(
    private val authRepository: AuthRepository
) : AuthInteractor {

    override val user: FirebaseUser?
        get() = authRepository.user

    override fun isAuth(): Single<Boolean> = Single.just(authRepository.isAuth())

    override suspend fun auth(
        email: String,
        password: String,
        isSaveCredentials: Boolean
    ): FirebaseUser? {
        try {
            val authResult = authRepository.auth(email, password).await()
            if (isSaveCredentials) authRepository.saveCredentials(email, password)
            return authResult.user
        } catch (exception: Exception) {
            throw(UsernamePasswordAuthException(exception))
        }
    }
//
//    override suspend fun loginWithGmail(): FirebaseUser {
//        try {
////            val authResult = authRepository.googleAuth().await()
//
//        } catch (exception: Exception) {
//            throw(exception)
//        }
//    }

//    override suspend fun googleAuth(): Completable =
//        authRepository.googleAuth().doOnComplete { }

    override fun isValidCredentials(email: String, password: String): Boolean =
        email.isNotBlank() && password.isNotBlank()
}