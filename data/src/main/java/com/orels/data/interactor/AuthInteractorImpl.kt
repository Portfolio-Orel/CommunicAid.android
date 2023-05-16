package com.orels.data.interactor

import com.orels.auth.domain.exception.AuthException
import com.orels.auth.domain.interactor.Auth
import com.orels.domain.interactors.AuthInteractor
import com.orels.domain.model.ResetPasswordStep
import com.orels.domain.model.SignInStep
import com.orels.domain.model.SignUpStep
import com.orels.domain.model.dto.body.create.CreateUserBody
import com.orels.domain.model.entities.Gender
import com.orels.domain.model.entities.User
import com.orels.domain.model.entities.UserState
import com.orels.domain.model.exception.UnknownException
import com.orels.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthInteractorImpl @Inject constructor(
    val auth: Auth,
    val repository: Repository
) : AuthInteractor {
    override suspend fun initialize(configFileResourceId: Int) =
        try {
            auth.initialize(configFileResourceId)
        } catch (e: Exception) {
            throw convertException(e)
        }


    override suspend fun login(username: String, password: String): SignInStep =
        try {
            auth.login(username, password).toLocalSignInStep()
        } catch (e: Exception) {
            throw convertException(e)
        }


    override suspend fun confirmSignInWithNewPassword(newPassword: String): SignInStep =
        try {
            auth.confirmSignInWithNewPassword(newPassword).toLocalSignInStep()
        } catch (e: Exception) {
            throw convertException(e)
        }

    override suspend fun resendConfirmationCode(phoneNumber: String) {
        try {
            val result = auth.resendConfirmationCode(phoneNumber)
            println()
        } catch (e: Exception) {
            throw convertException(e)
        }
    }


    override suspend fun logout() =
        try {
            auth.logout()
        } catch (e: Exception) {
            throw convertException(e)
        }


    override suspend fun register(
        username: String,
        password: String,
        phoneNumber: String,
        gender: Gender,
        email: String,
        firstName: String,
        lastName: String
    ): SignUpStep {
        try {
            val result = auth.register(username, password, phoneNumber, email, firstName, lastName)

            repository.createUser(
                CreateUserBody(
                    email = email,
                    gender = gender.name,
                    firstName = firstName,
                    lastName = lastName,
                    number = phoneNumber,
                    userId = result.userId ?: throw UnknownException("User id is null")
                )
            )
            return result.toLocalSignUpStep()
        } catch (e: Exception) {
            throw convertException(e)
        }
    }

    override suspend fun registerWithPhone(phoneNumber: String, email: String): SignUpStep {
        try {
            val result = auth.registerWithPhone(phoneNumber, email)
            repository.createUser(
                CreateUserBody(
                    email = email,
                    gender = "",
                    firstName = "",
                    lastName = "",
                    number = phoneNumber,
                    userId = result.userId ?: throw UnknownException("User id is null")
                )
            )
            return result.toLocalSignUpStep()
        } catch (e: Exception) {
            val exception = convertException(e)
            if (exception is AuthException.UsernameExistsException) {
                // TODO: Add user to db if does not exist
//                val user = repository.getUserByPhoneNumber(phoneNumber)
//                if(user == null) {
//                    repository.createUser(...
                //                auth.resendConfirmationCode()
            }
            throw exception
        }
    }

    override suspend fun confirmSignUpWithPhone(phoneNumber: String, code: String): SignUpStep =
        try {
            auth.confirmSignUpWithPhone(phoneNumber, code).toLocalSignUpStep()
        } catch (e: Exception) {
            throw convertException(e)
        }

    override suspend fun confirmUser(username: String, password: String, code: String): SignUpStep =
        try {
            auth.confirmUser(username, password, code).toLocalSignUpStep()
        } catch (e: Exception) {
            throw convertException(e)
        }

    override suspend fun forgotPassword(username: String): ResetPasswordStep =
        try {
            auth.forgotPassword(username).toLocalResetPasswordStep()
        } catch (e: Exception) {
            throw convertException(e)
        }

    override suspend fun resetPassword(code: String, newPassword: String) =
        try {
            auth.resetPassword(code, newPassword)
        } catch (e: Exception) {
            throw convertException(e)
        }

    override suspend fun getToken(): String? =
        try {
            auth.getToken()
        } catch (e: Exception) {
            throw convertException(e)
        }

    override suspend fun refreshToken(): String? =
        try {
            auth.refreshToken()
        } catch (e: Exception) {
            throw convertException(e)
        }

    override fun getUser(): User? =
        try {
            auth.getUser()?.toLocalUser()
        } catch (e: Exception) {
            throw convertException(e)
        }

    override suspend fun updateUser(user: User) =
        try {
            auth.updateUser(user.toRemoteUser())
        } catch (e: Exception) {
            throw convertException(e)
        }

    override suspend fun isLoggedIn(): Boolean =
        try {
            auth.isLoggedIn()
        } catch (e: Exception) {
            throw convertException(e)
        }

    override suspend fun getUserState(): Flow<UserState> =
        try {
            auth.getUserState().map { it.toLocalState() }
        } catch (e: Exception) {
            throw convertException(e)
        }


    override fun isPasswordValid(password: String): Boolean =
        try {
            auth.isPasswordValid(password)
        } catch (e: Exception) {
            throw convertException(e)
        }

    private fun convertException(ex: Exception): Exception =
        if (ex is AuthException) {
            convertAuthException(ex)
        } else {
            UnknownException(ex.message ?: "Unknown error")
        }


    private fun convertAuthException(ex: AuthException): Exception =
        when (ex) {
            is AuthException.UserNotFoundException -> throw com.orels.domain.model.exception.UserNotFoundException()
            is AuthException.UserNotConfirmedException -> throw com.orels.domain.model.exception.UserNotConfirmedException()
            is AuthException.InvalidPasswordException -> throw com.orels.domain.model.exception.InvalidPasswordException()
            is AuthException.CodeMismatchException -> throw com.orels.domain.model.exception.CodeMismatchException()
            is AuthException.NotAuthorizedException -> throw com.orels.domain.model.exception.NotAuthorizedException()
            is AuthException.UsernameExistsException -> throw com.orels.domain.model.exception.UsernameExistsException()
            is AuthException.LimitExceededException -> throw com.orels.domain.model.exception.LimitExceededException()
            is AuthException.UnknownRegisterException -> throw com.orels.domain.model.exception.UnknownRegisterException()
            is AuthException.UsernamePasswordAuthException -> throw com.orels.domain.model.exception.UsernamePasswordAuthException(
                ex
            )
            is AuthException.CodeExpiredException -> throw com.orels.domain.model.exception.CodeExpiredException()
            else -> {
                UnknownException(ex.message ?: "Unknown error")
            }
        }
}

fun com.orels.auth.domain.interactor.UserState.toLocalState() = when (this) {
    com.orels.auth.domain.interactor.UserState.LoggedIn -> UserState.Authorized
    com.orels.auth.domain.interactor.UserState.LoggedOut -> UserState.NotAuthorized
    com.orels.auth.domain.interactor.UserState.Loading -> UserState.Loading
    com.orels.auth.domain.interactor.UserState.Blocked -> UserState.Blocked
}

fun com.orels.auth.domain.SignUpStep.toLocalSignUpStep(): SignUpStep =
    when (this) {
        is com.orels.auth.domain.SignUpStep.ConfirmSignUpWithCode -> SignUpStep.ConfirmSignUpWithCode
        is com.orels.auth.domain.SignUpStep.ConfirmSignUpWithNewPassword -> SignUpStep.ConfirmSignUpWithNewPassword
        is com.orels.auth.domain.SignUpStep.Done -> SignUpStep.Done(this.user?.toLocalUser())
        else -> SignUpStep.Error
    }

fun com.orels.auth.domain.SignInStep.toLocalSignInStep(): SignInStep =
    when (this) {
        com.orels.auth.domain.SignInStep.ConfirmSignUp -> SignInStep.ConfirmSignUp
        com.orels.auth.domain.SignInStep.ConfirmSignInWithNewPassword -> SignInStep.ConfirmSignInWithNewPassword
        is com.orels.auth.domain.SignInStep.Done -> SignInStep.Done(this.user?.toLocalUser())
        is com.orels.auth.domain.SignInStep.Error -> SignInStep.Error(this.error)
        else -> SignInStep.Error(UnknownException("Unknown exception in converting remote SignInStep to local"))
    }

fun com.orels.auth.domain.ResetPasswordStep.toLocalResetPasswordStep(): ResetPasswordStep =
    when (this) {
        com.orels.auth.domain.ResetPasswordStep.ConfirmSignUpWithNewPassword -> ResetPasswordStep.ConfirmSignUpWithNewPassword
        is com.orels.auth.domain.ResetPasswordStep.Done -> ResetPasswordStep.Done(this.user?.toLocalUser())
        else -> ResetPasswordStep.Error
    }


fun UserState.toRemoteState() = when (this) {
    UserState.Authorized -> com.orels.auth.domain.interactor.UserState.LoggedIn
    UserState.NotAuthorized -> com.orels.auth.domain.interactor.UserState.LoggedOut
    UserState.Loading -> com.orels.auth.domain.interactor.UserState.Loading
    UserState.Blocked -> com.orels.auth.domain.interactor.UserState.Blocked
}

fun User.toRemoteUser() =
    com.orels.auth.domain.User(
        userId = userId,
        token = token,
        email = email,
        username = username,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        gender = gender.name,
        state = state.toRemoteState()
    )

fun com.orels.auth.domain.User.toLocalUser() =
    User(
        userId = userId,
        token = token ?: "",
        email = email ?: "",
        username = username ?: "",
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        phoneNumber = phoneNumber ?: "",
        gender = Gender.fromString(gender),
        state = state.toLocalState()
    )