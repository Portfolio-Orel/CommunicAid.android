package com.orels.presentation.ui.login.forgot_password

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.auth.domain.interactor.AuthInteractor
import com.orels.auth.domain.model.ResetPasswordStep
import com.orels.auth.domain.model.exception.CodeMismatchException
import com.orels.auth.domain.model.exception.LimitExceededException
import com.orels.auth.domain.model.exception.NotAuthorizedException
import com.orels.auth.domain.model.exception.UserNotFoundException
import com.orels.domain.util.extension.log
import com.orels.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
) : ViewModel() {
    var state by mutableStateOf(ForgotPasswordState())

    fun onForgotPassword(username: String) {
        state = state.copy(state = State.ForgotPassword(true))
        @StringRes var error: Int? = null

        val forgotPasswordJob: Deferred<ResetPasswordStep> = viewModelScope.async {
            return@async authInteractor.forgotPassword(username)
        }
        viewModelScope.launch {
            try {
                val resetPasswordStep = forgotPasswordJob.await()
                withContext(Dispatchers.Main) {
                    state = when (resetPasswordStep) {
                        is ResetPasswordStep.ConfirmSignUpWithNewPassword -> {
                            state.copy(state = State.ResetPassword(false))
                        }
                        is ResetPasswordStep.Done -> {
                            state.copy(state = State.Done)
                        }
                        else -> {
                            state.copy(error = R.string.error_unknown)
                        }
                    }
                }
            } catch (e: LimitExceededException) {
                error = R.string.error_limit_exceeded_operation
            } catch (e: UserNotFoundException) {
                error = R.string.error_username_does_not_exist
            } catch (e: NotAuthorizedException) {
                error = R.string.error_unknown
            } catch (e: Exception) {
                error = R.string.error_unknown
                e.log()
            } finally {
                withContext(Dispatchers.Main) {
                    if (error != null) {
                        state = state.copy(state = State.ForgotPassword(false), error = error)
                    }
                }
            }
        }
    }

    fun onResetPassword(code: String, password: String, confirmPassword: String) {
        state = state.copy(state = State.ResetPassword(true))
        @StringRes var error: Int? = null

        if (password.isPasswordNotValid() || confirmPassword.isPasswordNotValid()) {
            state = state.copy(state = State.ResetPassword(false),
                error = R.string.error_invalid_passwords)
            return
        }
        if (password != confirmPassword) {
            state = state.copy(state = State.ResetPassword(false),
                error = R.string.error_passwords_mismatch)
            return
        }

        val resetPasswordJob = viewModelScope.async {
            authInteractor.resetPassword(code = code, newPassword = password)
        }

        viewModelScope.launch {
            try {
                resetPasswordJob.await()
                withContext(Dispatchers.Main) {
                    state = state.copy(state = State.Done)
                }
            } catch (e: LimitExceededException) {
                error = R.string.error_limit_exceeded_operation
            } catch (e: UserNotFoundException) {
                error = R.string.error_username_does_not_exist
            } catch (e: CodeMismatchException) {
                error = R.string.error_code_mismatch
            } catch (e: Exception) {
                error = R.string.error_unknown
                e.log()
            } finally {
                withContext(Dispatchers.Main) {
                    if(error != null) {
                        state = state.copy(state = State.ResetPassword(false), error = error)
                    }
                }
            }
        }
    }

    private fun String.isPasswordNotValid() = !authInteractor.isPasswordValid(password = this)
}