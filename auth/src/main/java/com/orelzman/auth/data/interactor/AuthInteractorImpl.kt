package com.orelzman.auth.data.interactor

import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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

    override suspend fun googleAuth(context: Context) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("670361895848-0jildiu2ebiip55tqnkdtuhm1oq5mujc.apps.googleusercontent.com")
                .requestEmail()
                .build()
            val client =  GoogleSignIn.getClient(context, gso).signInIntent
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

    override val signInRequest: BeginSignInRequest
        get() = authRepository.signInRequest
}