package com.orelzman.mymessages.presentation.stats

import com.orelzman.mymessages.domain.model.dto.response.GetMessagesSentCountResponse

data class StatisticsState(
    val incomingCount: Int = 0,
    val outgoingCount: Int = 0,
    val messagesSentCount: List<Pair<String, Int>> = emptyList(),
    val messageSent: List<GetMessagesSentCountResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingCallLogSend: Boolean = false
    )