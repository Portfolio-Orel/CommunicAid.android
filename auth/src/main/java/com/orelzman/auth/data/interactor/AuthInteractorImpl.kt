package com.orelzman.auth.data.interactor

import android.content.Context
import android.util.Log
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
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

    override fun init() {
        // Add this line, to include the Auth plugin.
        Amplify.addPlugin(AWSCognitoAuthPlugin())
        Amplify.configure(context)
        Amplify.Auth.fetchAuthSession(
            { Log.i("AmplifyQuickstart", "Auth session = $it") },
            { error -> Log.e("AmplifyQuickstart", "Failed to fetch auth session", error) }
        )
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