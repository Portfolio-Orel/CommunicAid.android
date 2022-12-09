package com.orels.auth.data.local.interactor

import androidx.annotation.RawRes
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.result.step.AuthResetPasswordStep
import com.amplifyframework.auth.result.step.AuthSignInStep
import com.amplifyframework.auth.result.step.AuthSignUpStep
import com.orels.auth.data.local.AuthDatabase
import com.orels.auth.domain.interactor.AuthInteractor
import com.orels.auth.domain.interactor.UserState
import com.orels.auth.domain.model.ResetPasswordStep
import com.orels.auth.domain.model.SignInStep
import com.orels.auth.domain.model.SignUpStep
import com.orels.auth.domain.model.User
import com.orels.auth.domain.model.exception.*
import com.orels.auth.domain.service.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * @author Orel Zilberman
 * 06/12/2022
 */
class AuthInteractorImpl @Inject constructor(
    private val service: AuthService,
    localDatabase: AuthDatabase,
) : AuthInteractor {

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
            db.upsert(userFromService)
        }
    }

    override suspend fun login(username: String, password: String): SignInStep {
        try {
            val loginResult = service.login(username = username, password = password)
            return when (loginResult.nextStep.signInStep) {
                AuthSignInStep.DONE -> {
                    val user = service.getUser()
                    if (user != null) {
                        db.upsert(user)
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

    override suspend fun logout() {
        try {
            service.logout()
            db.insert(User.LOGGED_OUT_USER)
            _userStateFlow.value = UserState.LoggedOut
        } catch (e: Exception) {
            println()
        }
    }

    override suspend fun register(
        username: String,
        password: String,
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
                AuthSignUpStep.CONFIRM_SIGN_UP_STEP -> SignUpStep.ConfirmSignUpWithNewPassword
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

    override suspend fun confirmUser(username: String, password: String, code: String): SignUpStep {
        try {
            val registrationResult = service.confirmUserRegistration(username = username,
                password = password,
                code = code)
            return when (registrationResult.nextStep.signUpStep) {
                AuthSignUpStep.CONFIRM_SIGN_UP_STEP -> SignUpStep.ConfirmSignUpWithNewPassword
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

    private suspend fun resendConfirmationCode(username: String) {
        try {
            service.resendConfirmationCode(username = username)
        } catch (e: AuthException.LimitExceededException) {
            throw LimitExceededException()
        } catch (e: AuthException.UserNotFoundException) {
            throw UserNotFoundException()
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getUser(): User? = db.get()

    override suspend fun isLoggedIn(): Boolean = service.isLoggedIn()

    override suspend fun getUserState(): Flow<UserState> = userStateFlow
    override fun isPasswordValid(password: String): Boolean = password.isNotBlank()
}