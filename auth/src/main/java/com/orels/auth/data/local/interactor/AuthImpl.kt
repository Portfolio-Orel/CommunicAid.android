package com.orels.auth.data.local.interactor

import android.telephony.PhoneNumberUtils
import androidx.annotation.RawRes
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.options.AWSCognitoAuthSignInOptions
import com.amplifyframework.auth.cognito.options.AuthFlowType
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.auth.result.step.AuthResetPasswordStep
import com.amplifyframework.auth.result.step.AuthSignInStep
import com.amplifyframework.auth.result.step.AuthSignUpStep
import com.amplifyframework.kotlin.core.Amplify
import com.orels.auth.data.local.AuthDatabase
import com.orels.auth.domain.ResetPasswordStep
import com.orels.auth.domain.SignInStep
import com.orels.auth.domain.SignUpStep
import com.orels.auth.domain.User
import com.orels.auth.domain.exception.AuthException.*
import com.orels.auth.domain.interactor.Auth
import com.orels.auth.domain.interactor.UserState
import com.orels.auth.domain.service.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * @author Orel Zilberman
 * 06/12/2022
 */
class AuthImpl @Inject constructor(
    private val service: AuthService,
    localDatabase: AuthDatabase
) : Auth {

    val db = localDatabase.userDao()
    private val _userStateFlow = MutableStateFlow(UserState.Loading)
    private val userStateFlow = _userStateFlow.asStateFlow()

    override suspend fun initialize(@RawRes configFileResourceId: Int) {
        service.initialize(configFileResourceId = configFileResourceId)
        val user = db.get()
        service.isLoggedIn().let { isLoggedIn ->
            _userStateFlow.value = if (isLoggedIn) UserState.LoggedIn else UserState.LoggedOut
        }
        val userFromService = service.getUser() ?: User.LOGGED_OUT_USER
        if (userFromService != user) {
            db.updateFieldsNotNull(userFromService)
        }
    }

    override suspend fun login(username: String, password: String): SignInStep {
        try {
            val loginResult = service.login(username = username, password = password)
            return when (loginResult.nextStep.signInStep) {
                AuthSignInStep.DONE -> {
                    val user = service.getUser()
                    if (user != null) {
                        user.state = UserState.LoggedIn
                        db.deleteAndInsert(user)
                        _userStateFlow.value = UserState.LoggedIn
                        SignInStep.Done(user = user)
                    } else {
                        SignInStep.Error(AuthException("Unknown exception", "Try to login again"))
                    }
                }
                AuthSignInStep.CONFIRM_SIGN_IN_WITH_NEW_PASSWORD -> SignInStep.ConfirmSignInWithNewPassword
                AuthSignInStep.CONFIRM_SIGN_UP -> SignInStep.ConfirmSignUp
                else -> SignInStep.Error(AuthException("Unknown exception", "Try to login again"))
            }
        } catch (e: Exception) {
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

    private suspend fun loginWithPhone(phoneNumber: String) {
        try {
            val formattedPhoneNumber = PhoneNumberUtils.formatNumberToE164(phoneNumber, "IL")
            val authSignInOptions = AWSCognitoAuthSignInOptions.builder()
                .authFlowType(AuthFlowType.CUSTOM_AUTH)
                .build()
            Amplify.Auth.signIn("mu_$formattedPhoneNumber", options = authSignInOptions)
        } catch (error: AmplifyException) {
            println()
        } catch (error: Exception) {
            println()
        }
    }

    override suspend fun confirmSignInWithNewPassword(newPassword: String): SignInStep {
        val confirmationResult = service.confirmSignInWithNewPassword(newPassword)
        return when (confirmationResult.nextStep.signInStep) {
            AuthSignInStep.DONE -> {
                val user = service.getUser()
                if (user != null) {
                    user.state = UserState.LoggedIn
                    db.insert(user)
                    _userStateFlow.value = UserState.LoggedIn
                    SignInStep.Done(user = user)
                } else {
                    SignInStep.Error(AuthException("Unknown exception", "Try to login again"))
                }
            }
            else -> SignInStep.Error(AuthException("Unknown exception", "Try to login again"))
        }
    }

    override suspend fun logout() {
        service.logout()
        db.insert(User.LOGGED_OUT_USER)
        _userStateFlow.value = UserState.LoggedOut
    }

    override suspend fun register(
        username: String,
        password: String,
        phoneNumber: String,
        email: String,
        firstName: String,
        lastName: String,
    ): SignUpStep {
        try {
            val registrationResult = service.register(
                username = username,
                password = password,
                email = email,
                firstName = firstName,
                lastName = lastName
            )
            return when (registrationResult.nextStep.signUpStep) {
                AuthSignUpStep.CONFIRM_SIGN_UP_STEP -> SignUpStep.ConfirmSignUpWithNewPassword(
                    userId = registrationResult.user?.userId
                )
                AuthSignUpStep.DONE -> SignUpStep.Done(user = service.getUser())
                else -> SignUpStep.Error
            }
        } catch (e: Exception) {
            when (e) {
                is AuthException.UserNotConfirmedException -> throw UserNotConfirmedException()
                is AuthException.UsernameExistsException -> throw UsernameExistsException()
                is AuthException.InvalidPasswordException -> throw InvalidPasswordException()
                else -> throw e
            }
        }
    }

    override suspend fun registerWithPhone(phoneNumber: String, email: String): SignUpStep =
        try {
            val result = service.registerWithPhone(phoneNumber, email)
            when (result.nextStep.signUpStep) {
                AuthSignUpStep.CONFIRM_SIGN_UP_STEP -> SignUpStep.ConfirmSignUpWithCode(userId = result.user?.userId)
                AuthSignUpStep.DONE -> SignUpStep.Done(user = service.getUser())
                else -> SignUpStep.Error
            }
        } catch (e: AuthException) {
            when (e) {
                is AuthException.CodeDeliveryFailureException -> throw CodeDeliveryFailureException()
                is AuthException.UsernameExistsException -> throw UsernameExistsException()
                else -> throw e
            }
        }

    override suspend fun confirmSignUpWithPhone(phoneNumber: String, code: String): SignUpStep =
        try {
            val result = service.confirmSignUpWithPhone(phoneNumber, code)
            when (result.nextStep.signUpStep) {
                AuthSignUpStep.CONFIRM_SIGN_UP_STEP -> SignUpStep.ConfirmSignUpWithNewPassword(
                    userId = result.user?.userId
                )
                AuthSignUpStep.DONE -> SignUpStep.Done(user = service.getUser())
                else -> SignUpStep.Error
            }
        } catch (e: AuthException) {
            when (e) {
                is AuthException.CodeMismatchException -> throw CodeMismatchException()
                is AuthException.CodeExpiredException -> throw CodeExpiredException()
                is AuthException.NotAuthorizedException -> throw NotAuthorizedException()
                else -> throw e
            }
        }


    override suspend fun confirmUser(username: String, password: String, code: String): SignUpStep {
        try {
            val result = service.confirmUserRegistration(
                username = username,
                password = password,
                code = code
            )
            return when (result.nextStep.signUpStep) {
                AuthSignUpStep.CONFIRM_SIGN_UP_STEP -> SignUpStep.ConfirmSignUpWithNewPassword(
                    userId = result.user?.userId
                )
                AuthSignUpStep.DONE -> SignUpStep.Done(user = service.getUser())
                else -> SignUpStep.Error
            }
        } catch (e: AuthException) {
            when (e) {
                is AuthException.CodeMismatchException -> throw CodeMismatchException()
                is AuthException.CodeExpiredException -> throw CodeExpiredException()
                is AuthException.NotAuthorizedException -> throw NotAuthorizedException()
                else -> throw e
            }
        }
    }

    override suspend fun forgotPassword(username: String): ResetPasswordStep {
        try {
            val resetPasswordResult = service.forgotPassword(username = username)
            return when (resetPasswordResult.nextStep.resetPasswordStep) {
                AuthResetPasswordStep.CONFIRM_RESET_PASSWORD_WITH_CODE -> ResetPasswordStep.ConfirmSignUpWithNewPassword
                AuthResetPasswordStep.DONE -> ResetPasswordStep.Done(user = service.getUser())
                else -> ResetPasswordStep.Error
            }
        } catch (e: AuthException) {
            when (e) {
                is AuthException.UserNotFoundException -> throw UserNotFoundException()
                is AuthException.NotAuthorizedException -> throw NotAuthorizedException()
                is AuthException.LimitExceededException -> throw LimitExceededException()
                else -> throw e
            }
        } catch (e: LimitExceededException) {
            throw LimitExceededException()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun resetPassword(code: String, newPassword: String) {
        try {
            service.resetPassword(code = code, newPassword = newPassword)
        } catch (e: AuthException) {
            when (e) {
                is AuthException.CodeMismatchException -> throw CodeMismatchException()
                is AuthException.CodeExpiredException -> throw CodeExpiredException()
                is AuthException.NotAuthorizedException -> throw NotAuthorizedException()
                else -> throw e
            }
        }
    }

    override suspend fun getToken(): String? = service.getToken()

    override suspend fun refreshToken(): String {
        val token = service.getToken() ?: throw CouldNotRefreshTokenException()
        db.updateToken(token = token)
        return token
    }

    override suspend fun resendConfirmationCode(phoneNumber: String): AuthSignUpResult =
        try {
            service.resendConfirmationCode(phoneNumber = phoneNumber)
        } catch (e: AuthException.LimitExceededException) {
            throw LimitExceededException()
        } catch (e: AuthException.UserNotFoundException) {
            throw UserNotFoundException()
        } catch (e: Exception) {
            throw e
        }

    override fun getUser(): User? = db.get()
    override suspend fun updateUser(user: User) {
        db.updateFieldsNotNull(user)
    }

    override suspend fun isLoggedIn(): Boolean = service.isLoggedIn()

    override suspend fun getUserState(): Flow<UserState> = userStateFlow
    override fun isPasswordValid(password: String): Boolean = password.isNotBlank()
}