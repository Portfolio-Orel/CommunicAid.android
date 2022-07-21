package com.orelzman.mymessages.presentation.stats

data class StatisticsState(
    val incomingCount: Int = 0,
    val outgoingCount: Int = 0,
    val messagesSentCount: List<Pair<String, Int>> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingCallLogSend: Boolean = false
    )