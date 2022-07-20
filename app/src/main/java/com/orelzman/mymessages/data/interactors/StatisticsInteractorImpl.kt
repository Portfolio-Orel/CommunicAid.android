package com.orelzman.mymessages.data.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.StatisticsInteractor
import com.orelzman.mymessages.domain.model.entities.Statistics
import com.orelzman.mymessages.domain.model.entities.StatisticsTypes
import com.orelzman.mymessages.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StatisticsInteractorImpl @Inject constructor(
    private val repository: Repository,
    localDB: LocalDatabase
) : StatisticsInteractor {

    val db = localDB.statisticsDao

    override suspend fun getStatistics(): Flow<List<Statistics>> =
        db.getAllFlow()

    override suspend fun getCallsCountByType() {
        initCallsCountByType()
    }

    override suspend fun getMessagesSentCount(){
        initMessagesSentCount()
    }

    private suspend fun initCallsCountByType() {
        val result = repository.getCallsCountByType()
        db.insert(listOf(Statistics(StatisticsTypes.IncomingCount, result.incomingCount),
        Statistics(StatisticsTypes.OutgoingCount, result.outgoingCount),
        Statistics(StatisticsTypes.MissedCount, result.missedCount),
        Statistics(StatisticsTypes.RejectedCalls, result.rejectedCount)))
    }

    private suspend fun initMessagesSentCount() {
        val result = repository.getMessagesSentCount()
        val statisticsList = ArrayList<Statistics>()
        result.forEach {
            statisticsList.add(Statistics(StatisticsTypes.MessagesCount, Pair(it.title, it.count)))
        }
        db.insert(statisticsList)
    }
}