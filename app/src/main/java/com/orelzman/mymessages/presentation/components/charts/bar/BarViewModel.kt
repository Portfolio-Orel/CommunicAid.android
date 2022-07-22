package com.orelzman.mymessages.presentation.components.charts.bar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.orelzman.mymessages.domain.model.BarItem
import com.orelzman.mymessages.presentation.components.charts.domain.extension.normalizeBetween0AndMax

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