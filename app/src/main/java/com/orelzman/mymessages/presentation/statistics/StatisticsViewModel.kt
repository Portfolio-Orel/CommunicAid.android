package com.orelzman.mymessages.presentation.statistics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orelzman.mymessages.domain.interactors.StatisticsInteractor
import com.orelzman.mymessages.domain.model.entities.StatisticsTypes
import com.orelzman.mymessages.util.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsInteractor: StatisticsInteractor,
) : ViewModel() {
    var state by mutableStateOf(StatisticsState())

    var isRefreshing by mutableStateOf(false)

    init {
        initData()
        observeStatistics()
    }

    private fun observeStatistics() {
        viewModelScope.launch(Dispatchers.Main) {
            statisticsInteractor.getStatistics().collectLatest { statistics ->
                var incomingCount = 0
                var outgoingCount = 0
                var rejectedCount = 0
                var missedCount = 0
                val messagesSentCount = ArrayList<Pair<String, Int>>()
                statistics.forEach {
                    when (it.key) {
                        StatisticsTypes.IncomingCount -> incomingCount +=
                            it.value.toString().toFloatOrNull()?.toInt() ?: 0
                        StatisticsTypes.OutgoingCount -> outgoingCount =
                            it.value.toString().toFloatOrNull()?.toInt() ?: 0
                        StatisticsTypes.RejectedCalls -> rejectedCount +=
                            it.value.toString().toFloatOrNull()?.toInt() ?: 0
                        StatisticsTypes.MissedCount -> missedCount +=
                            it.value.toString().toFloatOrNull()?.toInt() ?: 0
                        StatisticsTypes.MessagesCount -> {
                            val value = it.value as? Map<*, *> ?: return@forEach
                            val messageTitle = value["title"].toString()
                            val timesSent =
                                value["count"].toString().toFloatOrNull()?.toInt() ?: return@forEach
                            messagesSentCount.add(Pair(messageTitle, timesSent))
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
    }

    fun refreshData() {
        isRefreshing = true
        initData()
    }

    private fun initData() {
        state = state.copy(isLoading = true)
        val job = viewModelScope.async {
                statisticsInteractor.getCallsCountByType()
                statisticsInteractor.getMessagesSentCount()
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
}