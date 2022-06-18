package com.orelzman.mymessages.data.local.interactors.unhandled_calls

import com.orelzman.mymessages.data.dto.UnhandledCall
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.remote.repository.Repository
import javax.inject.Inject

class UnhandledCallsInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : UnhandledCallsInteractor {
    val db = database.unhandledCallDao

    override suspend fun insert(uid: String, unhandledCall: UnhandledCall) {
        db.insert(unhandledCall = unhandledCall)
    }

    override suspend fun update(uid: String, unhandledCall: UnhandledCall) {
        db.update(unhandledCall = unhandledCall)
    }

    override suspend fun getAll(uid: String): List<UnhandledCall> {
        return db.getAll()
    }
}