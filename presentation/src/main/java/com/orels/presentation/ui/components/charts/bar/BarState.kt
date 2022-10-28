package com.orels.presentation.ui.components.charts.bar

import com.orels.presentation.ui.components.charts.model.BarItem

data class BarState(
    val isLoading: Boolean = false,
    val maxValue: Int = 1,
    val normalizedItems: List<BarItem> = emptyList()
)