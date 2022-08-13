package com.orelzman.mymessages.data.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.StatisticsInteractor
import com.orelzman.mymessages.domain.model.dto.response.GetCallsCountResponse
import com.orelzman.mymessages.domain.model.dto.response.GetMessagesSentCountResponse
import com.orelzman.mymessages.domain.model.entities.Statistics
import com.orelzman.mymessages.domain.model.entities.StatisticsTypes
import com.orelzman.mymessages.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class StatisticsInteractorImpl @Inject constructor(
    private val repository: Repository,
    localDB: LocalDatabase
) : StatisticsInteractor {

    val db = localDB.statisticsDao

    override suspend fun getStatistics(): Flow<List<Statistics>> =
        db.getAllFlow()

    override fun getStatisticsOnce(): List<Statistics> = db.getAll()

    override suspend fun init(startDate: Date?, endDate: Date?) {
        val callsCountResponse =
            repository.getCallsCountByType(startDate = startDate, endDate = endDate)
        val messagesSentCountResponse =
            repository.getMessagesSentCount(startDate = startDate, endDate = endDate)
        db.clear()
        initCallsCountByType(callsCountResponse = callsCountResponse)
        initMessagesSentCount(messagesSentCountResponse = messagesSentCountResponse)
    }

    private fun initCallsCountByType(callsCountResponse: GetCallsCountResponse) {
        db.insert(
            listOf(
                Statistics(StatisticsTypes.IncomingCount, callsCountResponse.incomingCount),
                Statistics(StatisticsTypes.OutgoingCount, callsCountResponse.outgoingCount),
                Statistics(StatisticsTypes.MissedCount, callsCountResponse.missedCount),
                Statistics(StatisticsTypes.RejectedCalls, callsCountResponse.rejectedCount)
            )
        )
    }

    private fun initMessagesSentCount(messagesSentCountResponse: List<GetMessagesSentCountResponse>?) {
        val statisticsList = ArrayList<Statistics>()
        messagesSentCountResponse?.forEach {
            statisticsList.add(
                Statistics(
                    StatisticsTypes.MessagesCount,
                    mapOf("title" to it.title, "count" to it.count),
                    extraIdentifier = it.title
                )
            )
        }

        db.insert(statisticsList)
    }
}