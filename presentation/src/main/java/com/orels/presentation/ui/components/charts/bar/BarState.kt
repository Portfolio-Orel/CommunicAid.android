package com.orelzman.mymessages.presentation.components.charts.bar

import com.orelzman.mymessages.presentation.components.charts.model.BarItem

data class BarState(
    val isLoading: Boolean = false,
    val maxValue: Int = 1,
    val normalizedItems: List<BarItem> = emptyList()
)