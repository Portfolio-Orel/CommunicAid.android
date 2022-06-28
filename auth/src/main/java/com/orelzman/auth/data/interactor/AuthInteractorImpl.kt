package com.orelzman.auth.data.interactor

import android.content.Context
import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.kotlin.core.Amplify
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.orelzman.auth.data.repository.AuthRepository
import com.orelzman.auth.domain.exception.UsernamePasswordAuthException
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.auth.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository
) : AuthInteractor {

    override suspend fun init() {
        // Add this line, to include the Auth plugin.
        Amplify.addPlugin(AWSCognitoAuthPlugin())
        Amplify.configure(context)
        Amplify.Auth.fetchAuthSession()
    }

    override fun getToken(): String =
        AWSMobileClient.getInstance().tokens.accessToken.tokenString

    @Throws
    override suspend fun signUp(
        email: String,
        username: String,
        password: String,
        isSaveCredentials: Boolean
    ) {
        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), email)
            .build()
        Amplify.Auth.signUp(username, password, options)
    }
    @Throws
    override suspend fun confirmUser(username: String, code: String) {
        try {
            val result = Amplify.Auth.confirmSignUp("username", code)
            if (result.isSignUpComplete) {
                Log.i("AuthQuickstart", "Signup confirmed")
            } else {
                Log.i("AuthQuickstart", "Signup confirmation not yet complete")
            }
        } catch (error: AuthException) {
            Log.e("AuthQuickstart", "Failed to confirm signup", error)
        }
    }

    override suspend fun signIn(username: String, password: String, isSaveCredentials: Boolean) {
        val result = Amplify.Auth.signIn(username, password)
        if (result.isSignInComplete) {
            Log.i("AuthQuickstart", "Sign in succeeded")
        } else {
            Log.e("AuthQuickstart", "Sign in not complete")
        }
    }

    override suspend fun signOut() {
        Amplify.Auth.signOut()
    }

    override val user: User?
        get() = authRepository.user?.uid?.let { User(uid = it) }

    override fun isAuth(): Single<Boolean> = Single.just(authRepository.isAuth())

    override suspend fun auth(
        email: String,
        password: String,
        isSaveCredentials: Boolean
    ): User? {
        try {
            val authResult = authRepository.auth(email, password).await()
            if (isSaveCredentials) authRepository.saveCredentials(email, password)
            return authResult.user?.uid?.let { User(uid = it) }
        } catch (exception: Exception) {
            throw(UsernamePasswordAuthException(exception))
        }
    }

    override suspend fun googleAuth(account: GoogleSignInAccount) {
        val result = account.idToken?.let { authRepository.googleAuth(it) }
            ?.await()
//            ?.getResult(TaskException::class.java)
        println(result)
    }

    override fun isValidCredentials(email: String, password: String): Boolean =
        email.isNotBlank() && password.isNotBlank()

    override val signInRequest: BeginSignInRequest
        get() = authRepository.signInRequest
}