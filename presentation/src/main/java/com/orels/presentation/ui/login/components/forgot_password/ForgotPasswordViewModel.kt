package com.orelzman.mymessages.presentation.login.components.forgot_password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.auth.domain.exception.LimitExceededException
import com.orelzman.auth.domain.exception.UserNotFoundException
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.auth.domain.model.ResetPasswordStep
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.util.extension.addUniqueIf
import com.orelzman.mymessages.domain.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author Orel Zilberman
 * 28/09/2022
 */

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    val authInteractor: AuthInteractor
) : ViewModel() {

    var state by mutableStateOf(ForgotPassswordState())

    fun forgotPassword() {
        state = state.copy(event = ForgotPasswordEvent.InsertUsername)
    }

    fun insertUsername(username: String) {
        if (username.isEmpty()) {
            state = state.copy(errorFields = listOf(ForgotPasswordFields.Username))
            return
        }
        state = state.copy(isLoading = true, error = null)
        val resetPasswordJob = viewModelScope.async {
            state = when (authInteractor.forgotPassword(username = username)) {
                ResetPasswordStep.ConfirmResetPasswordWithCode ->
                    state.copy(
                        isLoading = false,
                        event = ForgotPasswordEvent.InsertCodeAndPassword,
                        username = username
                    )
                ResetPasswordStep.Done ->
                    state.copy(
                        isLoading = false,
                        event = ForgotPasswordEvent.PasswordResetSuccessfully,
                        username = username
                    )
                ResetPasswordStep.Error ->
                    state.copy(
                        isLoading = false,
                        error = R.string.error_unknown,
                        username = username
                    )
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                resetPasswordJob.await()
            } catch (e: UserNotFoundException) {
                e.log()
                state = state.copy(
                    isLoading = false,
                    error = R.string.error_username_does_not_exist,
                    errorFields = listOf(ForgotPasswordFields.Username)
                )
            } catch (e: LimitExceededException) {
                e.log()
                state = state.copy(
                    isLoading = false,
                    error = R.string.error_limit_exceeded_operation,
                    errorFields = listOf(ForgotPasswordFields.Username)
                )
            } catch (e: Exception) {
                e.log()
                state = state.copy(
                    isLoading = false,
                    error = R.string.error_unknown,
                    errorFields = listOf(ForgotPasswordFields.Username)
                )
            }
        }
    }

    fun insertCodeAndPasswords(code: String, password: String, confirmPassword: String) {
        val errorFields = ArrayList<ForgotPasswordFields>()
        state = state.copy(isLoading = true, error = null)

        errorFields.addUniqueIf(
            ForgotPasswordFields.Password
        ) { password.isEmpty() }
        errorFields.addUniqueIf(
            ForgotPasswordFields.ConfirmPassword
        ) { confirmPassword.isEmpty() }
        errorFields.addUniqueIf(
            ForgotPasswordFields.Code
        ) { code.isEmpty() }

        if (errorFields.isNotEmpty()) {
            state = state.copy(isLoading = false, errorFields = errorFields)
            return
        }

        if (password != confirmPassword) {
            state = state.copy(
                isLoading = false,
                errorFields = listOf(
                    ForgotPasswordFields.Password,
                    ForgotPasswordFields.ConfirmPassword
                ), error = R.string.error_passwords_mismatch
            )
        } else {
            val confirmCodeAndPasswordsJob = viewModelScope.async {
                authInteractor.confirmResetPassword(code = code, password = password)
                authInteractor.signIn(username = state.username, password = password)
                state = state.copy(
                    isLoading = false,
                    event = ForgotPasswordEvent.PasswordResetSuccessfully
                )
            }
            viewModelScope.launch(Dispatchers.IO) {
                state = try {
                    confirmCodeAndPasswordsJob.await()
                    state
                } catch (e: Exception) {
                    e.log()
                    state.copy(
                        isLoading = false,
                        error = R.string.error_wrong_code,
                        errorFields = listOf(ForgotPasswordFields.Code)
                    )
                }
            }
        }
    }
}