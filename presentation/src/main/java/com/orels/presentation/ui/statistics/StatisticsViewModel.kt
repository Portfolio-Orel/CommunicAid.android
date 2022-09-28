package com.orelzman.mymessages.presentation.statistics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orels.domain.interactors.AnalyticsIdentifiers
import com.orels.domain.interactors.AnalyticsInteractor
import com.orelzman.mymessages.domain.interactors.StatisticsInteractor
import com.orelzman.mymessages.domain.model.entities.Statistics
import com.orelzman.mymessages.domain.model.entities.StatisticsTypes
import com.orelzman.mymessages.domain.util.extension.log
import com.orelzman.mymessages.presentation.statistics.components.StatisticsTabs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsInteractor: StatisticsInteractor,
    private val analyticsInteractor: AnalyticsInteractor
) : ViewModel() {
    var state by mutableStateOf(StatisticsState())

    var isRefreshing by mutableStateOf(false)

    fun init() {
        analyticsInteractor.track(AnalyticsIdentifiers.StatisticsScreenShow)
        initData()
        observeData()
    }

    fun onResume() {
        init()
    }

    fun refreshData() {
        isRefreshing = true
        fetchData()
    }

    fun tabSelected(statisticsTab: StatisticsTabs) {
        when (statisticsTab) {
            StatisticsTabs.Week -> analyticsInteractor.track(AnalyticsIdentifiers.StatisticsScreenShowWeeks)
            StatisticsTabs.Month -> analyticsInteractor.track(AnalyticsIdentifiers.StatisticsScreenShowMonths)
            StatisticsTabs.All -> analyticsInteractor.track(AnalyticsIdentifiers.StatisticsScreenShowAll)
        }
        setDates(startDate = statisticsTab.startDate, endDate = statisticsTab.endDate)
    }

    private fun fetchData() {
        val job = viewModelScope.async {
            statisticsInteractor.init(
                startDate = state.startDate,
                endDate = state.endDate
            )
        }
        viewModelScope.launch(Dispatchers.Main) {
            try {
                state = state.copy(isLoading = true)
                job.await()
            } catch (e: Exception) {
                e.log()
            } finally {
                isRefreshing = false
                state = state.copy(isLoading = false)
            }
        }
    }

    private fun setDates(startDate: Date?, endDate: Date?) {
        state = state.copy(isLoading = true, startDate = startDate, endDate = endDate)
        val getStatisticsJob = viewModelScope.async {
            statisticsInteractor.init(startDate = startDate, endDate = endDate)
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getStatisticsJob.await()
            } catch (e: Exception) {
                e.log()
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    private fun initData() {
        val statistics = statisticsInteractor.getStatisticsOnce()
        if (statistics.isEmpty()) {
            fetchData()
        } else {
            setStatisticsData(statistics)
        }
    }

    private fun observeData() {
        viewModelScope.launch(Dispatchers.Main) {
            statisticsInteractor.getStatistics().collectLatest { statistics ->
                setStatisticsData(statistics = statistics)
            }
        }
    }

    private fun setStatisticsData(statistics: List<Statistics>) {
        var incomingCount = 0
        var outgoingCount = 0
        var rejectedCount = 0
        var missedCount = 0
        val messagesSentCount = ArrayList<Pair<String, Int>>()
        statistics.forEach {
            when (it.key) {
                StatisticsTypes.IncomingCount -> incomingCount += it.getRealValue() ?: 0
                StatisticsTypes.OutgoingCount -> outgoingCount = it.getRealValue() ?: 0
                StatisticsTypes.RejectedCalls -> rejectedCount += it.getRealValue() ?: 0
                StatisticsTypes.MissedCount -> missedCount += it.getRealValue() ?: 0
                StatisticsTypes.MessagesCount -> {
                    try {
                        val messageTitleToTimesSent: Map<String, Any> =
                            it.getRealValue() ?: return@forEach

                        val messageTitle = messageTitleToTimesSent["title"].toString()
                        val timesSent =
                            messageTitleToTimesSent["count"]?.toString()?.toFloatOrNull()?.toInt()
                                ?: return@forEach
                        messagesSentCount.add(Pair(messageTitle, timesSent))
                    } catch (e: Exception) {
                        e.log()
                    }
                }
                else -> {}
            }
        }
        state = state.copy(
            incomingCount = incomingCount + missedCount + rejectedCount,
            outgoingCount = outgoingCount,
            totalCallsCount = outgoingCount + incomingCount + missedCount + rejectedCount,
            messagesSentCount = messagesSentCount
        )
    }
}