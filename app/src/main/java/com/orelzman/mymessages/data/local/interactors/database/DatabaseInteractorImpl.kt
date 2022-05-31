package com.orelzman.mymessages.data.local.interactors.database

import com.orelzman.mymessages.data.local.LocalDatabase
import javax.inject.Inject

class DatabaseInteractorImpl @Inject constructor(
    private val database: LocalDatabase,
) : DatabaseInteractor {

    override suspend fun clear() {
        database.clearAllTables()
    }
}