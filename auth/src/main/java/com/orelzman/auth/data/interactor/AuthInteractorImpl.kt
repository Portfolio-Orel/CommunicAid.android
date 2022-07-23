package com.orelzman.auth.data.interactor

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.kotlin.core.Amplify
import com.orelzman.auth.domain.exception.*
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.auth.domain.interactor.UserInteractor
import com.orelzman.auth.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AuthInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userInteractor: UserInteractor,
) : AuthInteractor {
    companion object {
        var isConfigured: Boolean = false
        const val TAG = "AuthAWS:::"
    }

    override suspend fun init(@RawRes configFileResourceId: Int?) {
        if (isConfigured) return
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            val configFile =
                configFileResourceId?.let { AmplifyConfiguration.fromConfigFile(context, it) }
            if (configFile != null) {
                Amplify.configure(configFile, context)
            } else {
                Amplify.configure(context)
            }
            isConfigured = true
            Log.v(TAG, "AWS configured")
            return
        } catch (exception: AmplifyException) {
            Log.v(TAG, exception.localizedMessage ?: "")
        } finally {
            if (Amplify.Auth.fetchAuthSession().isSignedIn) {
                userSignInSuccessfully()
            }
        }
    }

    override fun getUser(): User? = userInteractor.get()
    override fun getUserFlow(): Flow<User?> = userInteractor.getFlow()


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
                is AuthException.UsernameExistsException -> throw UsernameExistsException()
                else -> throw exception
            }
        }
    }


    @Throws
    override suspend fun confirmUser(username: String, code: String) {
        try {
            val result = Amplify.Auth.confirmSignUp(username, code)
            if (result.isSignUpComplete) {
                userSignInSuccessfully()
                Log.i(TAG, "Signup confirmed")
            } else {
                Log.i(TAG, "Signup confirmation not yet complete")
            }
        } catch (error: AuthException) {
            when (error) {
                is AuthException.CodeMismatchException -> {
                    resendConfirmationCode(username)
                    throw CodeMismatchException()
                }
                is AuthException.CodeExpiredException -> throw CodeExpiredException()
                is AuthException.NotAuthorizedException -> throw NotAuthorizedException()

                else -> throw error
            }
        }
    }

    override suspend fun googleAuth(activity: Activity) {
        try {
            val result = Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), activity)
            userSignInSuccessfully()
            Log.i(TAG, "Sign in OK: $result")
        } catch (error: AuthException) {
            Log.e(TAG, "Sign in failed", error)
        }
    }

    override suspend fun signIn(
        username: String,
        password: String,
        isSaveCredentials: Boolean
    ) {
        try {
            val result = Amplify.Auth.signIn(username, password)
            setUser()
            if (result.isSignInComplete) {
                userSignInSuccessfully()
                Log.v(TAG, "Sign in succeeded")
            } else {
                Log.e(TAG, "Sign in not complete")
                throw Exception("Login failed")
            }
        } catch (exception: Exception) {
            Log.e(TAG, "Sign in not complete with error: ${exception.localizedMessage}")
            when (exception) {
                is AuthException.UserNotConfirmedException -> {
                    resendConfirmationCode(username)
                    throw UserNotConfirmedException()
                }
                is AuthException.UserNotFoundException -> throw UserNotFoundException()
                is AuthException.NotAuthorizedException -> throw WrongCredentialsException()
                else -> throw exception
            }
        }
    }

    override suspend fun refreshToken() {
        val session = Amplify.Auth.fetchAuthSession()
        userInteractor.get()?.let {
            val user = User(
                userId = it.userId,
                token = (session as AWSCognitoAuthSession).userPoolTokens.value?.accessToken
                    ?: throw CouldNotRefreshTokenException(),
                email = it.email
            )
            Log.v(TAG, "Refreshed Token: ${user.token}")
            userInteractor.save(user)
        }
    }

    override suspend fun signOut() {
        Amplify.Auth.signOut()
        userInteractor.clear()
    }

    private suspend fun resendConfirmationCode(username: String) =
        try {
            Amplify.Auth.resendSignUpCode(username)
        } catch (e: AuthException.LimitExceededException) {
            throw LimitExceededException()
        }


    private suspend fun userSignInSuccessfully() {
        setUser()
    }

    private suspend fun setUser() {
        try {
            val userId = Amplify.Auth.getCurrentUser()?.userId ?: return
            val token =
                (Amplify.Auth.fetchAuthSession() as AWSCognitoAuthSession).userPoolTokens.value?.accessToken
                    ?: return
            var email = ""
            Amplify.Auth.fetchUserAttributes().forEach {
                if (it.key == AuthUserAttributeKey.email()) {
                    email = it.value
                }
            }
            val user = User(userId = userId, token = token, email = email)
            userInteractor.save(user)
        } catch (e: Exception) {
            Log.v(TAG, "error")
        }
    }

    override fun isValidCredentials(email: String, password: String): Boolean =
        email.isNotBlank() && password.isNotBlank()

}