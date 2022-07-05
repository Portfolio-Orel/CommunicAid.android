package com.orelzman.auth.domain.interactor

import com.orelzman.auth.domain.model.User

interface UserInteractor {
    fun insert(user: User)
    fun get(): User?
}