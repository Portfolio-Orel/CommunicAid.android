package com.orels.domain.interactors

import com.orels.domain.model.entities.Statistics
import kotlinx.coroutines.flow.Flow
import java.util.*

interface StatisticsInteractor {
    suspend fun init(startDate: Date? = null, endDate: Date? = null)
    suspend fun getStatistics(): Flow<List<Statistics>>
    fun getStatisticsOnce(): List<Statistics>
}