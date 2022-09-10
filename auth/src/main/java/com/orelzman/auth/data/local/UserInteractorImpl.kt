package com.orelzman.auth.data.local

import com.orelzman.auth.data.dao.UserDao
import com.orelzman.auth.domain.interactor.UserInteractor
import com.orelzman.auth.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserInteractorImpl @Inject constructor(
    private val db: UserDao
) : UserInteractor {
    override fun save(user: User) {
        val currentUser = db.get()
        if (currentUser != user) {
            if(currentUser?.userId == user.userId) {
                db.update(user)
            } else {
                db.clear()
                db.insert(user)
            }
        }
    }


    override fun get(): User? =
        db.get()

    override fun getFlow(): Flow<User?> = db.getUserFlow()

    override fun clear() {
        db.clear()
    }

}