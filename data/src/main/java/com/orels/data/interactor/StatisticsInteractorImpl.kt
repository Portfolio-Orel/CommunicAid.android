package com.orels.data.interactor

import com.orels.data.local.LocalDatabase
import com.orels.domain.interactors.StatisticsInteractor
import com.orels.domain.model.dto.response.GetCallsCountResponse
import com.orels.domain.model.dto.response.GetMessagesSentCountResponse
import com.orels.domain.model.entities.Statistics
import com.orels.domain.model.entities.StatisticsTypes
import com.orels.domain.repository.Repository
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
        initCallsCountByType(
            callsCountResponse = callsCountResponse,
            startDate = startDate,
            endDate = endDate
        )
        initMessagesSentCount(
            messagesSentCountResponse = messagesSentCountResponse,
            startDate = startDate,
            endDate = endDate
        )
    }

    private fun initCallsCountByType(
        callsCountResponse: GetCallsCountResponse,
        startDate: Date?,
        endDate: Date?
    ) {
        db.insert(
            listOf(
                Statistics(
                    key = StatisticsTypes.IncomingCount,
                    value = callsCountResponse.incomingCount,
                    startDate = startDate,
                    endDate = endDate
                ),
                Statistics(
                    key = StatisticsTypes.OutgoingCount,
                    value = callsCountResponse.outgoingCount,
                    startDate = startDate,
                    endDate = endDate
                ),
                Statistics(
                    key = StatisticsTypes.MissedCount,
                    value = callsCountResponse.missedCount,
                    startDate = startDate,
                    endDate = endDate
                ),
                Statistics(
                    key = StatisticsTypes.RejectedCalls,
                    value = callsCountResponse.rejectedCount,
                    startDate = startDate,
                    endDate = endDate
                )
            )
        )
    }

    private fun initMessagesSentCount(
        messagesSentCountResponse: List<GetMessagesSentCountResponse>?,
        startDate: Date?,
        endDate: Date?
    ) {
        val statisticsList = ArrayList<Statistics>()
        messagesSentCountResponse?.forEach {
            val value = mapOf("title" to it.title, "count" to it.count)
            statisticsList.add(
                Statistics(
                    key = StatisticsTypes.MessagesCount,
                    value = value,
                    extraIdentifier = it.title,
                    startDate = startDate,
                    endDate = endDate
                )
            )
        }

        db.insert(statisticsList)
    }
}