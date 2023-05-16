package com.orels.data.interactor

import com.orels.auth.domain.interactor.Auth
import com.orels.domain.interactors.UserInteractor
import com.orels.auth.domain.User
import com.orels.domain.model.dto.response.toUser
import com.orels.domain.model.exception.UserNotFoundException
import com.orels.domain.repository.Repository
import javax.inject.Inject

class UserInteractorImpl @Inject constructor(
    private val repository: Repository,
    private val auth: Auth,
): UserInteractor {

    override suspend fun setUser() {
        val user = repository.getUser()?.toUser() ?: throw UserNotFoundException()
        val authUser = User(
            userId = user.userId,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
        )
        auth.updateUser(authUser)
    }
}