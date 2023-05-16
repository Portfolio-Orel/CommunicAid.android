package com.orels.auth.data.remote

import android.content.Context
import android.telephony.PhoneNumberUtils
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.kotlin.core.Amplify
import com.orels.auth.domain.User
import com.orels.auth.domain.service.AuthService
import com.orels.auth.domain.util.PasswordGenerator
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

    override suspend fun registerWithPhone(phoneNumber: String, email: String): AuthSignUpResult {
        val formattedPhoneNumber = PhoneNumberUtils.formatNumberToE164(phoneNumber, "IL")
        val signUpOptions = AuthSignUpOptions.builder()
            .userAttributes(
                mutableListOf(
                    AuthUserAttribute(AuthUserAttributeKey.email(), email),
                    AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), formattedPhoneNumber)
                )
            )
            .build()
        val step =  Amplify.Auth.signUp(
            username = formattedPhoneNumber.formatToPhoneNumberUsername(),
            password = PasswordGenerator.generateStrongPassword(),
            options = signUpOptions
        )
        Amplify.Auth.resendSignUpCode(formattedPhoneNumber.formatToPhoneNumberUsername())
        return step
    }

    override suspend fun confirmSignUpWithPhone(
        phoneNumber: String,
        code: String
    ): AuthSignUpResult {
        val formattedPhoneNumber = PhoneNumberUtils.formatNumberToE164(phoneNumber, "IL")
        return Amplify.Auth.confirmSignUp(formattedPhoneNumber, code)
    }

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

    override suspend fun resendConfirmationCode(phoneNumber: String): AuthSignUpResult =
        Amplify.Auth.resendSignUpCode(phoneNumber.formatToPhoneNumberUsername())

    override suspend fun isLoggedIn(): Boolean = Amplify.Auth.getCurrentUser() != null

    private fun String.formatToPhoneNumberUsername() = "mu_${
        PhoneNumberUtils.formatNumberToE164(
            this,
            "IL"
        )
    }" // MyMessagesUser
}