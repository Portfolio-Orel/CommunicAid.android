package com.orels.presentation.ui.unhandled_calls

import com.orels.domain.model.entities.CallLogEntity

data class UnhandledCallsState(
    val callsToHandle: List<CallLogEntity> = emptyList(),
    val canDeleteCalls: Boolean = true,
    val isLoading: Boolean = false
)