package com.orels.auth.data.remote

import android.content.Context
import com.amplifyframework.auth.AuthUser
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.cognito.options.AWSCognitoAuthSignInOptions
import com.amplifyframework.auth.options.AuthSignInOptions
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.kotlin.core.Amplify
import com.orels.auth.domain.model.User
import com.orels.auth.domain.service.AuthService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * @author Orel Zilberman
 * 06/12/2022
 */
class AuthServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AuthService {
    companion object {
        var isConfigured: Boolean = false
    }

    override suspend fun initialize(configFileResourceId: Int) {
        if (isConfigured) return
        Amplify.addPlugin(AWSCognitoAuthPlugin())
        Amplify.configure(
            AmplifyConfiguration.fromConfigFile(context, configFileResourceId),
            context
        )
        isConfigured = true
    }

    override suspend fun login(username: String, password: String): AuthSignInResult =
        Amplify.Auth.signIn(username, password)

    override suspend fun confirmSignInWithNewPassword(
        newPassword: String
    ): AuthSignInResult =
        Amplify.Auth.confirmSignIn(newPassword)


    override suspend fun logout() = Amplify.Auth.signOut()


    override suspend fun register(
        email: String,
        username: String,
        password: String,
        firstName: String,
        lastName: String,
    ): AuthSignUpResult = Amplify.Auth.signUp(
        username,
        password,
        AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), email)
            .userAttribute(AuthUserAttributeKey.name(), firstName)
            .userAttribute(AuthUserAttributeKey.familyName(), lastName)
            .build()
    )

    override suspend fun confirmUserRegistration(
        username: String,
        password: String,
        code: String,
    ): AuthSignUpResult = Amplify.Auth.confirmSignUp(
        username,
        code
    )

    override suspend fun forgotPassword(username: String) = Amplify.Auth.resetPassword(username)

    override suspend fun resetPassword(code: String, newPassword: String) =
        Amplify.Auth.confirmResetPassword(
            newPassword = newPassword,
            confirmationCode = code
        )

    override suspend fun getToken(): String? = runCatching {
        (Amplify.Auth.fetchAuthSession() as AWSCognitoAuthSession).userPoolTokens.value?.accessToken
    }.getOrNull()

    override suspend fun getUser(): User? {
        val userId = Amplify.Auth.getCurrentUser()?.userId ?: return null
        val username = Amplify.Auth.getCurrentUser()?.username ?: return null
        val token = getToken() ?: return null
        val attributes = Amplify.Auth.fetchUserAttributes()

        val email = runCatching {
            attributes.first { it.key == AuthUserAttributeKey.email() }.value
        }.getOrNull()

        val firstName = runCatching {
            attributes.first { it.key == AuthUserAttributeKey.givenName() }.value
        }.getOrNull()

        val lastName = runCatching {
            attributes.first { it.key == AuthUserAttributeKey.familyName() }.value
        }.getOrNull()

        return User(
            userId = userId,
            username = username,
            token = token,
            email = email,
            firstName = firstName,
            lastName = lastName,
        )
    }

    override suspend fun resendConfirmationCode(username: String): AuthSignUpResult =
        Amplify.Auth.resendSignUpCode(username)

    override suspend fun isLoggedIn(): Boolean = Amplify.Auth.getCurrentUser() != null
}