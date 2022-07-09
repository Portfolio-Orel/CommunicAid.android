package com.orelzman.mymessages.presentation.stats

data class StatsState(
    val callsCountToday: Int = 0,
    val callsNotUploaded: Int = 0,
    val callsUploaded: Int = 0,
    val callsBeingUploaded: Int = 0,
    val lastUpdateDate: String = "לא עודכן",
    val isLoadingCallLogSend: Boolean = false
    )