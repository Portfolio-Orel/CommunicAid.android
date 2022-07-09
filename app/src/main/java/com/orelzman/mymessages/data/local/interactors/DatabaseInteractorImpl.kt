package com.orelzman.mymessages.data.local.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.DatabaseInteractor
import com.orelzman.mymessages.domain.interactors.FolderInteractor
import com.orelzman.mymessages.domain.interactors.MessageInteractor
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import javax.inject.Inject

class DatabaseInteractorImpl @Inject constructor(
    private val database: LocalDatabase,
    private val messageInteractor: MessageInteractor,
    private val folderInteractor: FolderInteractor,
    private val settingsInteractor: SettingsInteractor
) : DatabaseInteractor {

    override suspend fun clear() {
        database.clearAllTables()
    }
}