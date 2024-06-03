package com.orels.presentation.ui.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.orels.domain.model.entities.StatisticsTypes
import com.orels.domain.util.extension.getDayFormatted
import com.orels.presentation.R
import com.orels.presentation.ui.components.LtrView
import com.orels.presentation.ui.components.OnLifecycleEvent
import com.orels.presentation.ui.components.charts.bar.BarChart
import com.orels.presentation.ui.components.charts.donut.DonutChart
import com.orels.presentation.ui.components.charts.model.BarItem
import com.orels.presentation.ui.components.charts.model.DonutItem
import com.orels.presentation.ui.statistics.components.StatisticsTabs
import java.util.*

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val isRefreshing = viewModel.isRefreshing
    val state = viewModel.state

    OnLifecycleEvent(onResume = viewModel::onResume)

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        StatisticsTabs(
            onClick = viewModel::tabSelected,
            modifier = Modifier
                .padding(bottom = 24.dp, top = 12.dp)
                .padding(horizontal = 8.dp)
                .height(40.dp)
                .fillMaxWidth()
        )
        Dates(state.startDate, state.endDate)
        SwipeRefresh(
            modifier = Modifier.fillMaxSize(),
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                viewModel.refreshData()
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                CallTypesStatistics(
                    incomingCount = state.incomingCount,
                    outgoingCount = state.outgoingCount,
                    totalCallsCount = state.totalCallsCount,
                    isLoading = state.isLoading
                )
                if (state.isLoading) {
                    Loading()
                } else {
                    BarChart(
                        items = createBarItemList(
                            color = MaterialTheme.colorScheme.primary,
                            state.messagesSentCount
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun CallTypesStatistics(
    incomingCount: Int,
    outgoingCount: Int,
    totalCallsCount: Int,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        DonutChart(
            item = DonutItem(
                title = {
                    Text(
                        text = stringResource(id = StatisticsTypes.IncomingCount.label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                textInside = {
                    DonutText(
                        text = incomingCount.toString(),
                        isLoading = isLoading
                    )
                },
                outerColor = MaterialTheme.colorScheme.secondary,
                innerColor = MaterialTheme.colorScheme.background
            )
        )
        DonutChart(
            item = DonutItem(
                title = {
                    Text(
                        text = stringResource(StatisticsTypes.OutgoingCount.label),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                textInside = {
                    DonutText(
                        text = outgoingCount.toString(),
                        isLoading = isLoading
                    )
                },
                outerColor = MaterialTheme.colorScheme.secondary,
                innerColor = MaterialTheme.colorScheme.background
            )
        )
        DonutChart(
            item = DonutItem(
                title = {
                    Text(
                        text = stringResource(R.string.total),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                textInside = {
                    DonutText(
                        text = totalCallsCount.toString(),
                        isLoading = isLoading
                    )
                },
                outerColor = MaterialTheme.colorScheme.secondary,
                innerColor = MaterialTheme.colorScheme.background
            )
        )
    }
}

private fun createBarItemList(
    color: Color,
    list: List<Pair<String, Int>>,
    maxItems: Int = 15,
): List<BarItem> {
    val barItems = ArrayList<BarItem>()
    list
        .sortedByDescending { it.second }
        .forEachIndexed { index, it ->
            if (index >= maxItems) return@forEachIndexed
            barItems.add(
                BarItem(
                    title = it.first,
                    value = it.second.toFloat(),
                    color = color
                )
            )
        }
    return barItems
}

@Composable
private fun Dates(startDate: Date?, endDate: Date?) {
    val spacer = if (startDate == null || endDate == null) {
        ""
    } else {
        " - "
    }

    LtrView {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = startDate?.getDayFormatted(withYear = false) ?: "",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = spacer,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = endDate?.getDayFormatted(withYear = false) ?: "",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun Loading(
    size: Dp = 48.dp
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .height(size)
                .width(size),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun DonutText(text: String, isLoading: Boolean = false) {
    if (isLoading) {
        Loading(size = 24.dp)
    } else {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}