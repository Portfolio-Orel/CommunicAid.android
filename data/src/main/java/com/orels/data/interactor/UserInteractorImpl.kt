package com.orels.data.interactor

import com.orels.auth.domain.interactor.AuthInteractor
import com.orels.domain.interactors.UserInteractor
import com.orels.domain.model.dto.response.toUser
import com.orels.domain.model.exception.UserNotFoundException
import com.orels.domain.repository.Repository
import javax.inject.Inject

class UserInteractorImpl @Inject constructor(
    private val repository: Repository,
    private val authInteractor: AuthInteractor,
): UserInteractor {

    override suspend fun setUser() {
        val user = repository.getUser()?.toUser() ?: throw UserNotFoundException()
        val authUser = com.orels.auth.domain.model.User(
            userId = user.userId,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
        )
        authInteractor.updateUser(authUser)
    }
}