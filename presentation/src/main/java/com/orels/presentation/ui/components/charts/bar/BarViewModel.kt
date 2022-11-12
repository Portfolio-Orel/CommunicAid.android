package com.orels.presentation.ui.components.charts.bar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.orels.domain.util.extension.normalizeBetween0AndMax
import com.orels.presentation.ui.components.charts.model.BarItem

class BarViewModel(items: List<BarItem>) : ViewModel() {
    var state by mutableStateOf(BarState())

    init {
        normalizeItems(items)
    }

    private fun normalizeItems(items: List<BarItem>) {
        if(items.isEmpty()) return
        val normalizedItems = ArrayList<BarItem>()
        val maxValue = items.maxOfOrNull { it.value }
        items.forEach {
            val normalizedValue = it.value.normalizeBetween0AndMax(0f, maxValue ?: 0f, state.maxValue.toFloat())
            normalizedItems.add(BarItem(it.title, normalizedValue, it.color))
        }
        state = state.copy(normalizedItems = normalizedItems)
    }

    fun getNormalizedValue(item: BarItem): Float =
        state.normalizedItems.first { it.title == item.title }.value
}