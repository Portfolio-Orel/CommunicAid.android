package com.orelzman.auth.data.remote

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.core.InitializationStatus
import com.amplifyframework.hub.HubChannel
import com.amplifyframework.kotlin.core.Amplify
import com.orelzman.auth.domain.exception.*
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.auth.domain.interactor.UserInteractor
import com.orelzman.auth.domain.model.User
import com.orelzman.auth.domain.model.UserState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
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
            collectState(configFileResourceId)
            refreshToken()
            return
        } catch (e: AmplifyException) {
            Log.v(TAG, e.localizedMessage ?: "")
        } finally {
            val user = getUser()
            if (Amplify.Auth.getCurrentUser()?.userId != user?.userId) {
                userInteractor.clear()
            }
            if (Amplify.Auth.fetchAuthSession().isSignedIn) {
                userSignInSuccessfully()
            }
        }
    }

    override fun getUser(): User? = userInteractor.get()
    override fun getUserFlow(): Flow<User?> = userInteractor.getFlow()

    override suspend fun isAuthorized(user: User?): Boolean {
        val isLocallyAuthorized = user != null && user.token != "" && user.userId != ""
        val authorizedAgainstServerJob = CoroutineScope(Dispatchers.IO).async {
            val isRemotelyAuthorized = Amplify.Auth.fetchAuthSession().isSignedIn
            return@async isLocallyAuthorized && isRemotelyAuthorized && user?.state == UserState.Authorized
        }
        try {
            return authorizedAgainstServerJob.await()
        } catch (e: Exception) {
            throw e
        }
    }


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
        } catch (e: Exception) {
            when (e) {
                is AuthException.UserNotConfirmedException -> throw UserNotConfirmedException()
                is AuthException.UsernameExistsException -> throw UsernameExistsException()
                is AuthException.InvalidPasswordException -> throw InvalidPasswordException()
                else -> throw e
            }
        }
    }


    @Throws
    override suspend fun confirmUser(username: String, password: String, code: String) {
        try {
            val result = Amplify.Auth.confirmSignUp(username, code)
            Amplify.Auth.signIn(username = username, password = password)
            if (result.isSignUpComplete) {
                userSignInSuccessfully()
                Log.i(TAG, "Signup confirmed")
            } else {
                Log.i(TAG, "Signup confirmation not yet complete")
                throw UnknownRegisterException()
            }
        } catch (e: AuthException) {
            when (e) {
                is AuthException.CodeMismatchException -> {
                    resendConfirmationCode(username)
                    throw CodeMismatchException()
                }
                is AuthException.CodeExpiredException -> throw CodeExpiredException()
                is AuthException.NotAuthorizedException -> throw NotAuthorizedException()
                else -> throw e
            }
        }
    }

    override suspend fun googleAuth(activity: Activity) {
        try {
            val result = Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), activity)
            userSignInSuccessfully()
            Log.i(TAG, "Sign in OK: $result")
        } catch (e: AuthException) {
            Log.e(TAG, "Sign in failed", e)
            throw e
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
                userSignInSuccessfully()
                Log.v(TAG, "Sign in succeeded")
                (Amplify.Auth.fetchAuthSession() as AWSCognitoAuthSession).awsCredentials.error?.localizedMessage?.let {
                    Log.v(
                        TAG,
                        it
                    )
                }
            } else {
                Log.e(TAG, "Sign in not complete")
                throw Exception("Login failed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sign in not complete with error: ${e.localizedMessage}")
            when (e) {
                is AuthException.UserNotConfirmedException -> {
                    resendConfirmationCode(username)
                    throw UserNotConfirmedException()
                }
                is AuthException.UserNotFoundException -> throw UserNotFoundException()
                is AuthException.NotAuthorizedException -> throw WrongCredentialsException()
                else -> throw e
            }
        }
    }

    override suspend fun refreshToken(@RawRes configFileResourceId: Int?) {
        if (configFileResourceId != null) {
            init(configFileResourceId)
        }
        if (!isConfigured) {
            return
        }
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
        try {
            Amplify.Auth.signOut()
        } finally {
            userInteractor.clear()
        }
    }

    private suspend fun resendConfirmationCode(username: String) =
        try {
            Amplify.Auth.resendSignUpCode(username)
        } catch (e: AuthException.LimitExceededException) {
            throw LimitExceededException()
        }

    private suspend fun collectState(@RawRes configFileResourceId: Int?) {
        CoroutineScope(SupervisorJob()).launch {
            Amplify.Hub.subscribe(HubChannel.AUTH).collectLatest {
                when (it.name) {
                    InitializationStatus.SUCCEEDED.toString() ->
                        Log.i(TAG, "Auth successfully initialized")
                    InitializationStatus.FAILED.toString() ->
                        Log.i(TAG, "Auth failed to succeed")
                    else -> when (AuthChannelEventName.valueOf(it.name)) {
                        AuthChannelEventName.SIGNED_IN ->
                            Log.i(TAG, "Auth just became signed in.")
                        AuthChannelEventName.SIGNED_OUT ->
                            Log.i(TAG, "Auth just became signed out.")
                        AuthChannelEventName.SESSION_EXPIRED ->
                            try {
                                refreshToken(configFileResourceId)
                            } catch (e: Exception) {
                                signOut()
                            }
                        AuthChannelEventName.USER_DELETED ->
                            signOut()
                    }
                }
            }
        }
    }

    private suspend fun userSignInSuccessfully() =
        setUser()


    private suspend fun setUser() {
        try {
            val userId = Amplify.Auth.getCurrentUser()?.userId ?: return
            val token =
                (Amplify.Auth.fetchAuthSession() as AWSCognitoAuthSession).userPoolTokens.value?.accessToken
                    ?: return
            val email = Amplify.Auth.fetchUserAttributes().first { it.key == AuthUserAttributeKey.email() }.value
            val user =
                User(userId = userId, token = token, email = email, state = UserState.Authorized)
            userInteractor.save(user)
        } catch (e: Exception) {
            when (e) {
                is AuthException.NotAuthorizedException -> userInteractor.save(User.blocked())
                else -> userInteractor.save(User.notAuthorized())
            }
        }
    }

    override fun isValidCredentials(email: String, password: String): Boolean =
        email.isNotBlank() && password.isNotBlank()

}