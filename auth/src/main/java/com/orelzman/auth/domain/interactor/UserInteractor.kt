package com.orelzman.auth.domain.interactor

import com.orelzman.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

sealed interface UserInteractor {
    fun save(user: User)
    fun get(): User?
    fun getFlow(): Flow<User?>
    fun clear()
}