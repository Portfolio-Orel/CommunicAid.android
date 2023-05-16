package com.orels.presentation.ui.register

import com.orels.domain.model.entities.Gender


sealed class RegisterEvent {
    data class SetPhoneNumber(val phoneNumber: String): RegisterEvent()
    data class SetFirstName(val firstName: String): RegisterEvent()
    data class SetLastName(val lastName: String): RegisterEvent()
    data class SetEmail(val email: String): RegisterEvent()
        data class SetGender(val gender: Gender): RegisterEvent()
    data class SetDateOfBirth(val dateOfBirth: Long): RegisterEvent()
    data class ConfirmCode(val code: String): RegisterEvent()
    object PreviousStage: RegisterEvent()
    object CompleteRegistration: RegisterEvent()
    object Register: RegisterEvent() }