package com.orelzman.mymessages.presentation.statistics

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
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.model.entities.StatisticsTypes
import com.orelzman.mymessages.domain.util.extension.getDayFormatted
import com.orelzman.mymessages.presentation.components.LtrView
import com.orelzman.mymessages.presentation.components.OnLifecycleEvent
import com.orelzman.mymessages.presentation.components.charts.bar.BarChart
import com.orelzman.mymessages.presentation.components.charts.donut.DonutChart
import com.orelzman.mymessages.presentation.components.charts.model.BarItem
import com.orelzman.mymessages.presentation.components.charts.model.DonutItem
import com.orelzman.mymessages.presentation.statistics.components.StatisticsTabs
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
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(30.dp),
                ) {
                    DonutChart(
                        item = DonutItem(
                            title = {
                                Text(
                                    text = stringResource(id = StatisticsTypes.IncomingCount.label),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            textInside = {
                                DonutText(
                                    text = state.incomingCount.toString(),
                                    isLoading = state.isLoading
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
                                    text = state.outgoingCount.toString(),
                                    isLoading = state.isLoading
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
                                    text = state.totalCallsCount.toString(),
                                    isLoading = state.isLoading
                                )
                            },
                            outerColor = MaterialTheme.colorScheme.secondary,
                            innerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
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

private fun createBarItemList(
    color: Color,
    list: List<Pair<String, Int>>,
    maxItems: Int = 8,
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
        "-"
    }

    LtrView {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = startDate?.getDayFormatted(withYear = false) ?: "",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = spacer,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = endDate?.getDayFormatted(withYear = false) ?: "",
                style = MaterialTheme.typography.labelLarge,
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
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
