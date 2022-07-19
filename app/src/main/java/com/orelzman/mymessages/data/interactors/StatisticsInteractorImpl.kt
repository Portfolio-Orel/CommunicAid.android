package com.orelzman.mymessages.data.interactors

import com.orelzman.mymessages.domain.interactors.StatisticsInteractor
import com.orelzman.mymessages.domain.model.dto.response.CallsCountResponse
import com.orelzman.mymessages.domain.model.dto.response.MessagesSentCountResponse
import com.orelzman.mymessages.domain.repository.Repository
import javax.inject.Inject

class StatisticsInteractorImpl @Inject constructor(
    private val repository: Repository
) : StatisticsInteractor {
    override suspend fun getCallsCountByType(): CallsCountResponse = repository.getCallsCountByType()

    override suspend fun getMessagesSentCount(): List<MessagesSentCountResponse> = repository.getMessagesSentCount()
}