package com.orels.domain.interactors

import com.orels.domain.model.entities.User
import kotlinx.coroutines.flow.Flow

interface UserInteractor {
    fun save(user: User)
    fun get(): User?
    fun getFlow(): Flow<User?>
    fun clear()
}