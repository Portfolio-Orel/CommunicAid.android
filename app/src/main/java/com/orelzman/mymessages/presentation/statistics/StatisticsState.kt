package com.orelzman.mymessages.presentation.statistics

data class StatisticsState(
    val incomingCount: Int = 0,
    val outgoingCount: Int = 0,
    val totalCallsCount: Int = incomingCount + outgoingCount,
    val messagesSentCount: List<Pair<String, Int>> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingCallLogSend: Boolean = false
    )