package com.orelzman.mymessages.presentation.statistics

import com.orelzman.mymessages.domain.util.common.DateUtils.getFirstDayOfWeek
import com.orelzman.mymessages.domain.util.common.DateUtils.getLastDayOfWeek
import java.util.*

data class StatisticsState(
    val incomingCount: Int = 0,
    val outgoingCount: Int = 0,
    val totalCallsCount: Int = incomingCount + outgoingCount,
    val messagesSentCount: List<Pair<String, Int>> = emptyList(),

    val startDate: Date = getFirstDayOfWeek(),
    val endDate: Date = getLastDayOfWeek(),

    val isLoading: Boolean = false,
    val isLoadingCallLogSend: Boolean = false
    )