package com.orelzman.mymessages.domain.interactors

interface GeneralInteractor {
    fun clearAllDatabases()
    suspend fun initData()
}