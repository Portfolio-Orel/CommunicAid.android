package com.orels.data.interactor

import com.orels.domain.interactors.UserInteractor
import com.orels.domain.model.entities.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserInteractorImpl @Inject constructor(
    private val db: com.orels.data.local.dao.UserDao
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