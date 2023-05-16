package com.orels.presentation.ui.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.domain.interactors.AuthInteractor
import com.orels.domain.model.entities.User
import com.orels.domain.model.exception.*
import com.orels.domain.util.Validators
import com.orels.domain.util.extension.log
import com.orels.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authInteractor: AuthInteractor
) : ViewModel() {
    var state by mutableStateOf(RegisterState())

    fun onEvent(event: RegisterEvent) =
        when (event) {
            is RegisterEvent.SetPhoneNumber -> state = state.copy(phoneNumber = event.phoneNumber)
            is RegisterEvent.SetFirstName -> state = state.copy(firstName = event.firstName)
            is RegisterEvent.SetLastName -> state = state.copy(lastName = event.lastName)
            is RegisterEvent.SetEmail -> state = state.copy(email = event.email)
            is RegisterEvent.SetGender -> state = state.copy(gender = event.gender)
            is RegisterEvent.SetDateOfBirth -> state = state.copy(dateOfBirth = event.dateOfBirth)
            is RegisterEvent.Register -> {
                state = state.copy(error = null)
                register()
            }
            is RegisterEvent.CompleteRegistration -> {
                state = state.copy(error = null)
                completeRegistration()
            }
            is RegisterEvent.ConfirmCode -> {
                state = state.copy(error = null)
                confirmCode(event.code)
            }
            is RegisterEvent.PreviousStage -> {
                state = state.copy(error = null)
                state = state.copy(stage = state.stage.previous())
            }
        }


    private fun confirmCode(code: String) {
        state = state.copy(isLoading = true, code = code)
        val confirmSignUpJob = viewModelScope.async {
            authInteractor.confirmSignUpWithPhone(
                phoneNumber = state.phoneNumber,
                code = code
            )
            withContext(Dispatchers.Main) {
                state = state.copy(isLoading = false, stage = state.stage.next())
            }
        }
        viewModelScope.launch {
            try {
                confirmSignUpJob.await()
            } catch (e: CodeMismatchException) {
                authInteractor.resendConfirmationCode(state.phoneNumber)
                state =
                    state.copy(isLoading = false, error = R.string.error_code_mismatch, code = "")
            } catch (e: CodeExpiredException) {
                authInteractor.resendConfirmationCode(state.phoneNumber)
                state =
                    state.copy(isLoading = false, error = R.string.error_code_expired, code = "")
            } catch (e: NotAuthorizedException) {
                state = state.copy(
                    isLoading = false,
                    error = R.string.error_username_does_not_exist
                )
            } catch (e: LimitExceededException) {
                state = state.copy(
                    isLoading = false,
                    error = R.string.error_limit_exceeded_operation
                )
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = R.string.error_unknown)
            }
        }
    }

    private fun completeRegistration() {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            authInteractor.updateUser(
                user = User(
                    firstName = state.firstName,
                    lastName = state.lastName,
                    gender = state.gender
                ),
            )
            withContext(Dispatchers.Main) {
                state = state.copy(isLoading = false, stage = state.stage.next())
            }
        }
    }

    private fun register() {
        val isEmailValid = Validators.isEmailValid(state.email)
        val isPhoneNumberValid = Validators.isPhoneNumberValid(state.phoneNumber)
        if (!isEmailValid || !isPhoneNumberValid) {
            state = state.copy(
                error = if (!isEmailValid) R.string.error_invalid_email else R.string.error_invalid_phone_number
            )
            return
        }
        val registerJob = viewModelScope.async {
            val user = User(
                phoneNumber = state.phoneNumber,
                email = state.email
            )

            state = state.copy(isLoading = true, user = user)
            authInteractor.registerWithPhone(phoneNumber = state.phoneNumber, email = state.email)
            withContext(Dispatchers.Main) {
                state = state.copy(isLoading = false, stage = state.stage.next())
            }
        }

        viewModelScope.launch {
            try {
                registerJob.await()
            } catch (e: Exception) {
                val error: Int = when (e) {
                    is UsernameExistsException -> R.string.error_user_exists
                    is CodeDeliveryFailureException -> R.string.error_code_delivery_failed
                    else -> R.string.error_unknown
                }
                state = state.copy(isLoading = false, error = error)
                e.log()
            }
        }
    }
}