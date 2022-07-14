package com.orelzman.auth.domain.interactor

import com.orelzman.auth.domain.model.User

interface UserInteractor {
    fun save(user: User)
    fun get(): User?
    fun clear()
}