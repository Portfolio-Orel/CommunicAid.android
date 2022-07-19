package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.dto.response.CallsCountResponse
import com.orelzman.mymessages.domain.model.dto.response.MessagesSentCountResponse

interface StatisticsInteractor {
    suspend fun getCallsCountByType(): CallsCountResponse
    suspend fun getMessagesSentCount(): List<MessagesSentCountResponse>
}