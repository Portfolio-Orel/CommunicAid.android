package com.orels.data.interactor

import android.app.Activity
import android.content.Context
import androidx.annotation.RawRes
import com.amazonaws.mobileconnectors.cognitoauth.util.JWTParser
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.step.AuthResetPasswordStep
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.core.InitializationStatus
import com.amplifyframework.hub.HubChannel
import com.amplifyframework.kotlin.core.Amplify
import com.orels.domain.interactors.AuthInteractor
import com.orels.domain.interactors.AuthLogger
import com.orels.domain.interactors.UserInteractor
import com.orels.domain.model.ResetPasswordStep
import com.orels.domain.model.entities.User
import com.orels.domain.model.entities.UserState
import com.orels.domain.model.exception.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import java.security.InvalidParameterException
import javax.inject.Inject


class AuthInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userInteractor: UserInteractor,
) : AuthInteractor {
    companion object {
        var isConfigured: Boolean = false
        var authLogger: AuthLogger? = null
        const val TAG = "AuthAWS:::"
    }

    @Throws(NullConfigurationFile::class, Exception::class)
    override suspend fun init(@RawRes configFileResourceId: Int, authLogger: AuthLogger?) {
        Companion.authLogger = authLogger
        if (isConfigured) return
        Amplify.addPlugin(AWSCognitoAuthPlugin())
        Amplify.configure(
            AmplifyConfiguration.fromConfigFile(context, configFileResourceId),
            context
        )
        isConfigured = true
        collectState()
        refreshUserData()
    }

    override fun getUser(): User? = userInteractor.get()

    override fun getUserFlow(): Flow<User?> = userInteractor.getFlow()

    override suspend fun isAuthorized(user: User?, whoCalled: String): Boolean {
        refreshUserData()
        val isLocallyAuthorized = user != null && user.token != "" && user.userId != ""
        val isRemotelyAuthorized = Amplify.Auth.fetchAuthSession().isSignedIn
        return isLocallyAuthorized && isRemotelyAuthorized
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
                refreshUserData()
                authLogger?.info(TAG, "Signup confirmed")
            } else {
                authLogger?.info(TAG, "Signup confirmation not yet complete")
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

    override suspend fun forgotPassword(username: String): ResetPasswordStep {
        return try {
            val result = Amplify.Auth.resetPassword(username = username)
            when (result.nextStep.resetPasswordStep) {
                AuthResetPasswordStep.CONFIRM_RESET_PASSWORD_WITH_CODE -> {
                    ResetPasswordStep.ConfirmResetPasswordWithCode
                }
                AuthResetPasswordStep.DONE -> {
                    ResetPasswordStep.Done
                }
                else -> {
                    ResetPasswordStep.Error
                }
            }

        } catch (e: InvalidParameterException) {
            throw UserNotFoundException()
        } catch (e: LimitExceededException) {
            throw LimitExceededException()
        } catch (e: AuthException.UserNotFoundException) {
            throw UserNotFoundException()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun confirmResetPassword(code: String, password: String) {
        Amplify.Auth.confirmResetPassword(newPassword = password, confirmationCode = code)
    }

    override suspend fun googleAuth(activity: Activity) {
        try {
            val result = Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), activity)
            refreshUserData()
            authLogger?.info(TAG, "Sign in OK: $result")
        } catch (e: AuthException) {
            authLogger?.error(TAG, "Sign in failed", e)
            throw e
        }
    }

    override suspend fun signIn(
        username: String,
        password: String,
        isSaveCredentials: Boolean
    ) {
        try {
            Amplify.Auth.signOut()
            val result = Amplify.Auth.signIn(username, password)
            if (result.isSignInComplete) {
                refreshUserData()
                authLogger?.info(TAG, "Sign in succeeded")
                (Amplify.Auth.fetchAuthSession() as AWSCognitoAuthSession).awsCredentials.error?.localizedMessage?.let {
                    authLogger?.info(
                        TAG,
                        it
                    )
                }
            } else {
                authLogger?.error(TAG, "Sign in not complete", Exception())
                throw Exception("Login failed")
            }
        } catch (e: Exception) {
            authLogger?.error(TAG, "Sign in not complete with error: ${e.localizedMessage}", e)
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

    override suspend fun refreshToken() {
        refreshUserData()
    }

    override suspend fun signOut() {
        try {
            if(userInteractor.get() == null) return
            Amplify.Auth.signOut()
        } catch(e: Exception) {
            throw e
        } finally {
            userInteractor.clear()
        }
    }

    override fun isValidCredentials(email: String, password: String): Boolean =
        email.isNotBlank() && password.isNotBlank()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun collectState() {
        CoroutineScope(SupervisorJob()).launch {
            Amplify.Hub.subscribe(HubChannel.AUTH).collectLatest {
                when (it.name) {
                    InitializationStatus.SUCCEEDED.toString() ->
                        authLogger?.info(TAG, "Auth successfully initialized")
                    InitializationStatus.FAILED.toString() ->
                        authLogger?.info(TAG, "Auth failed to succeed")
                    else -> when (AuthChannelEventName.valueOf(it.name)) {
                        AuthChannelEventName.SIGNED_IN ->
                            refreshUserData()
                        AuthChannelEventName.SIGNED_OUT ->
                            userInteractor.clear()
                        AuthChannelEventName.SESSION_EXPIRED ->
                            try {
                                refreshToken()
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

    private suspend fun resendConfirmationCode(username: String) =
        try {
            Amplify.Auth.resendSignUpCode(username)
        } catch (e: AuthException.LimitExceededException) {
            throw LimitExceededException()
        }

    private suspend fun refreshUserData() {
        try {
            val userId = Amplify.Auth.getCurrentUser()?.userId
            val token =
                (Amplify.Auth.fetchAuthSession() as AWSCognitoAuthSession).userPoolTokens.value?.accessToken
            if (userId == null || token == null) {
                signOut()
                return
            }
            var email = userInteractor.get()?.email ?: ""
            runCatching {
                email = Amplify.Auth.fetchUserAttributes()
                    .first { it.key == AuthUserAttributeKey.email() }.value
            }
            val username = JWTParser.getClaim(token, "username")
            val user =
                User(
                    userId = userId,
                    token = token,
                    email = email,
                    username = username,
                    state = UserState.Authorized
                )
            userInteractor.save(user)
        } catch (e: Exception) {
            when (e) {
                is AuthException.NotAuthorizedException -> userInteractor.save(User.blocked())
                else -> userInteractor.save(User.notAuthorized())
            }
            throw e
        }
    }

}