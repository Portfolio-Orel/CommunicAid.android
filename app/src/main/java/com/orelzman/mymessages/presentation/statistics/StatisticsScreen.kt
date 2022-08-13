package com.orelzman.mymessages.presentation.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.model.BarItem
import com.orelzman.mymessages.domain.model.DonutItem
import com.orelzman.mymessages.domain.util.extension.getDayFormatted
import com.orelzman.mymessages.presentation.components.LtrView
import com.orelzman.mymessages.presentation.components.charts.bar.BarChart
import com.orelzman.mymessages.presentation.components.charts.donut.DonutChart
import java.util.*

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val isRefreshing = viewModel.isRefreshing
    val state = viewModel.state
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
//        Tabs(onClick = {}, modifier =, tabs = listOf())
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
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(30.dp),
                ) {
                    DonutChart(
                        item = DonutItem(
                            title = {
                                Text(
                                    text = stringResource(R.string.incoming_calls),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            textInside = {
                                Text(
                                    text = state.incomingCount.toString(),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground
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
                                    text = stringResource(R.string.outgoing_calls),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            textInside = {
                                Text(
                                    text = state.outgoingCount.toString(),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground
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
                                Text(
                                    text = state.totalCallsCount.toString(),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            outerColor = MaterialTheme.colorScheme.secondary,
                            innerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
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

private fun createBarItemList(
    color: Color,
    list: List<Pair<String, Int>>,
    maxItems: Int = 5,
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
private fun Dates(startDate: Date, endDate: Date) {
    val spacer = "-"
    LtrView {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = startDate.getDayFormatted(withYear = false),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = spacer,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = endDate.getDayFormatted(withYear = false),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
