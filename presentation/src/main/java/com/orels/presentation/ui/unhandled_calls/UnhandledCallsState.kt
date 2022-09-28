package com.orelzman.mymessages.presentation.unhandled_calls

import com.orelzman.mymessages.domain.model.entities.CallLogEntity

data class UnhandledCallsState(
    val callsToHandle: List<CallLogEntity> = emptyList(),
    val canDeleteCalls: Boolean = true,
    val isLoading: Boolean = false
)