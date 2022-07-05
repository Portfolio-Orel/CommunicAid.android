package com.orelzman.auth.data.interactor

import android.app.Activity
import android.content.Context
import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify.AlreadyConfiguredException
import com.amplifyframework.kotlin.core.Amplify
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.orelzman.auth.data.repository.AuthRepository
import com.orelzman.auth.domain.exception.*
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.auth.domain.interactor.UserInteractor
import com.orelzman.auth.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository,
    private val userInteractor: UserInteractor,
) : AuthInteractor {
    companion object {
        var isConfigured: Boolean = false
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initAWS()
        }
    }

    override suspend fun initAWS() {
        if (isConfigured) return
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(context)
            isConfigured = true
        } catch (exception: AlreadyConfiguredException) {
            isConfigured = true
            return
        } finally {
            if (Amplify.Auth.fetchAuthSession().isSignedIn) {
                getUser()?.let {
                    if(userInteractor.get() == null) {
                        userInteractor.insert(it)
                    }
                }
            }
        }
    }

    override suspend fun getUser(): User? {
        if (!Amplify.Auth.fetchAuthSession().isSignedIn) {
            return null
        }
        val user = userInteractor.get()
        if (user != null) {
            return user
        }
        val userId = Amplify.Auth.getCurrentUser()?.userId ?: ""
        var email = ""
        Amplify.Auth.fetchUserAttributes().forEach {
            if (it.key == AuthUserAttributeKey.email()) {
                email = it.value
            }
        }
        val token = getToken()
        return User(userId = userId, token = token, email = email)
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
        try {
            Amplify.Auth.signUp(username, password, options)
        } catch (exception: Exception) {
            when (exception) {
                is AuthException.UserNotConfirmedException -> throw UserNotConfirmedException()
                else -> throw exception
            }
        }
    }


    @Throws
    override suspend fun confirmUser(username: String, code: String) {
        try {
            val result = Amplify.Auth.confirmSignUp(username, code)
            if (result.isSignUpComplete) {
                Log.i("AuthQuickstart", "Signup confirmed")
            } else {
                Log.i("AuthQuickstart", "Signup confirmation not yet complete")
            }
        } catch (error: AuthException) {
            when (error) {
                is AuthException.CodeMismatchException -> {
                    Amplify.Auth.resendSignUpCode(username)
                    throw CodeMismatchException()
                }
                is AuthException.NotAuthorizedException -> {
                    throw NotAuthorizedException()
                }
                else -> throw error
            }
        }
    }

    override suspend fun googleAuth(activity: Activity) {
        try {
            val result = Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), activity)
            Log.i("AuthQuickstart", "Sign in OK: $result")
        } catch (error: AuthException) {
            Log.e("AuthQuickstart", "Sign in failed", error)
        }
    }

    override suspend fun signIn(
        username: String,
        password: String,
        isSaveCredentials: Boolean
    ) {
        try {
            val result = Amplify.Auth.signIn(username, password)
            if (result.isSignInComplete) {
                Log.i("AuthQuickstart", "Sign in succeeded")
            } else {
                Log.e("AuthQuickstart", "Sign in not complete")
                throw Exception("Login failed")
            }
        } catch (exception: Exception) {
            when (exception) {
                is AuthException.UserNotConfirmedException -> throw UserNotConfirmedException()
                is AuthException.UserNotFoundException -> throw UserNotFoundException()
                else -> throw exception
            }
        }
    }

    override suspend fun signOut() {
        Amplify.Auth.signOut()
    }

    override fun isAuth(): Single<Boolean> = Single.just(authRepository.isAuth())

    override suspend fun auth(
        email: String,
        password: String,
        isSaveCredentials: Boolean
    ): User? {
        try {
            val authResult = authRepository.auth(email, password).await()
            if (isSaveCredentials) authRepository.saveCredentials(email, password)
            return authResult.user?.uid?.let { User(userId = it) }
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