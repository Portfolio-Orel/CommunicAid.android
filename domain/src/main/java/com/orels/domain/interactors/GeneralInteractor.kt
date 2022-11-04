package com.orels.domain.interactors

interface GeneralInteractor {
    fun clearAllDatabases()
    suspend fun initData()
}