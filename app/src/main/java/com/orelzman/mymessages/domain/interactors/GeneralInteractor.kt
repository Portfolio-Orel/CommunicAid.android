package com.orelzman.mymessages.domain.interactors

interface GeneralInteractor {
    suspend fun clearAllDatabases()
    suspend fun initData()
}