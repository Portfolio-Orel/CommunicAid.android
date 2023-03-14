package com.orels.domain.interactors

import com.orels.domain.model.exception.UserNotFoundException

interface UserInteractor {

    @Throws(UserNotFoundException::class)
    suspend fun setUser()
}