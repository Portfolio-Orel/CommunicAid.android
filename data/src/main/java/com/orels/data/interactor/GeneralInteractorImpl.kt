package com.orels.data.interactor

import com.orels.data.local.LocalDatabase
import com.orels.domain.interactors.DeletedCallsInteractor
import com.orels.domain.interactors.FolderInteractor
import com.orels.domain.interactors.GeneralInteractor
import com.orels.domain.interactors.MessageInteractor
import com.orels.domain.interactors.SettingsInteractor
import com.orels.domain.model.entities.Settings
import com.orels.domain.model.entities.SettingsKey
import com.orels.domain.util.common.DateUtils
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class GeneralInteractorImpl @Inject constructor(
    database: LocalDatabase,
    private val messageInteractor: MessageInteractor,
    private val folderInteractor: FolderInteractor,
    private val deletedCallsInteractor: DeletedCallsInteractor,
    private val settingsInteractor: SettingsInteractor,
) : GeneralInteractor {

    val db = database

    override fun clearAllDatabases() {
        db.clearAllTables()
    }

    override suspend fun initData() {
        supervisorScope {
//        clearAllDatabases()
            messageInteractor.initWithMessagesInFolders(false)
            folderInteractor.init()
//        DataSourceCallsInteractor.init()
            settingsInteractor.init()
            settingsInteractor.saveSettings(Settings(SettingsKey.IsDataInit, true.toString()))
            deletedCallsInteractor.init(DateUtils.getStartOfDay())
        }
    }
}