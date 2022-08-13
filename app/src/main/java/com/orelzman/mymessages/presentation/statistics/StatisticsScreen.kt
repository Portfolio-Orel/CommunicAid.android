package com.orelzman.mymessages.presentation.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.model.BarItem
import com.orelzman.mymessages.domain.model.DonutItem
import com.orelzman.mymessages.presentation.components.charts.bar.BarChart
import com.orelzman.mymessages.presentation.components.charts.donut.DonutChart

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val isRefreshing = viewModel.isRefreshing
    val state = viewModel.state
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
//        Tabs(onClick = {}, modifier =, tabs = listOf())
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
                BarChart(items = createBarItemList(state.messagesSentCount))
            }
        }
    }
}

@Composable
private fun createBarItemList(list: List<Pair<String, Int>>, maxItems: Int = 5): List<BarItem> {
    val barItems = ArrayList<BarItem>()
    list
        .sortedByDescending { it.second }
        .forEachIndexed { index, it ->
            if (index >= maxItems) return@forEachIndexed
            barItems.add(
                BarItem(
                    title = it.first,
                    value = it.second.toFloat(),
                    color = if (index % 2 == 0) MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                    else MaterialTheme.colorScheme.secondary
                )
            )
        }
    return barItems
}