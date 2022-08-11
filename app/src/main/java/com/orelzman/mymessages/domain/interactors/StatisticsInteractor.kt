package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.Statistics
import kotlinx.coroutines.flow.Flow

interface StatisticsInteractor {
    suspend fun getCallsCountByType()
    suspend fun getMessagesSentCount()
    suspend fun init()
    suspend fun getStatistics(): Flow<List<Statistics>>
    fun getStatisticsOnce(): List<Statistics>
}