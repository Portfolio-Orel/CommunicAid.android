package com.orelzman.mymessages.data.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.*
import javax.inject.Inject

class GeneralInteractorImpl @Inject constructor(
    database: LocalDatabase,
    private val messageInteractor: MessageInteractor,
    private val folderInteractor: FolderInteractor,
    private val deletedCallsInteractor: DeletedCallsInteractor,
    private val DataSourceCallsInteractor: DataSourceCallsInteractor,
    private val settingsInteractor: SettingsInteractor
) : GeneralInteractor {

    val db = database

    override suspend fun clearAllDatabases() {
        db.clearAllTables()
    }

    override suspend fun initData() {
        clearAllDatabases()
        messageInteractor.initWithMessagesInFolders()
        folderInteractor.init()
        deletedCallsInteractor.init()
        DataSourceCallsInteractor.init()
        settingsInteractor.init()
    }

}