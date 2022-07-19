package com.orelzman.mymessages.presentation.stats

import com.orelzman.mymessages.domain.model.dto.response.CallsCountResponse
import com.orelzman.mymessages.domain.model.dto.response.MessagesSentCountResponse

data class StatisticsState(
    val callsCountToday: Int = 0,
    val callsNotUploaded: Int = 0,
    val callsUploaded: Int = 0,
    val callsBeingUploaded: Int = 0,
    val callsCount: CallsCountResponse = CallsCountResponse(),
    val messageSent: List<MessagesSentCountResponse> = emptyList(),
    val lastUpdateDate: String = "לא עודכן",
    val isLoadingCallLogSend: Boolean = false
    )