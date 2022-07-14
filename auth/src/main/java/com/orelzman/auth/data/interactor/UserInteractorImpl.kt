package com.orelzman.auth.data.interactor

import com.orelzman.auth.data.dao.UserDao
import com.orelzman.auth.domain.interactor.UserInteractor
import com.orelzman.auth.domain.model.User
import javax.inject.Inject

class UserInteractorImpl @Inject constructor(
    private val userDB: UserDao
): UserInteractor {
    override fun save(user: User) {
        userDB.clear()
        userDB.insert(user)
    }

    override fun get(): User? =
        userDB.get()

    override fun clear() =
        userDB.clear()

}