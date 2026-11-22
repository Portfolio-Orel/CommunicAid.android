package com.orels.presentation.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.orels.domain.model.entities.StatisticsTypes
import com.orels.domain.util.extension.getDayFormatted
import com.orels.presentation.R
import com.orels.presentation.theme.AppTheme
import com.orels.presentation.ui.components.LtrView
import com.orels.presentation.ui.components.OnLifecycleEvent
import com.orels.presentation.ui.components.charts.bar.BarChart
import com.orels.presentation.ui.components.charts.donut.DonutChart
import com.orels.presentation.ui.components.charts.model.BarItem
import com.orels.presentation.ui.components.charts.model.DonutItem
import com.orels.presentation.ui.statistics.components.StatisticsTabs
import java.util.*

private val StatsCardShape = RoundedCornerShape(24.dp)

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val isRefreshing = viewModel.isRefreshing
    val state = viewModel.state

    OnLifecycleEvent(onResume = viewModel::onResume)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        StatisticsTabs(
            onClick = viewModel::tabSelected,
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp)
                .height(48.dp)
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard {
                    CallTypesStatistics(
                        incomingCount = state.incomingCount,
                        outgoingCount = state.outgoingCount,
                        totalCallsCount = state.totalCallsCount,
                        isLoading = state.isLoading
                    )
                }
                if (state.isLoading) {
                    StatsCard {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Loading()
                        }
                    }
                } else if (state.messagesSentCount.isNotEmpty()) {
                    StatsCard {
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
}

@Composable
private fun StatsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = StatsCardShape,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            )
            .clip(StatsCardShape)
            .background(AppTheme.extended.cardSurface)
            .border(1.dp, AppTheme.extended.cardBorder, StatsCardShape)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun CallTypesStatistics(
    incomingCount: Int,
    outgoingCount: Int,
    totalCallsCount: Int,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        DonutChart(
            item = DonutItem(
                title = { DonutTitle(text = stringResource(id = StatisticsTypes.IncomingCount.label)) },
                textInside = {
                    DonutText(
                        text = incomingCount.toString(),
                        isLoading = isLoading
                    )
                },
                outerSize = 72f,
                innerSize = 64f,
                outerColor = MaterialTheme.colorScheme.primary,
                innerColor = AppTheme.extended.cardSurface
            )
        )
        DonutChart(
            item = DonutItem(
                title = { DonutTitle(text = stringResource(StatisticsTypes.OutgoingCount.label)) },
                textInside = {
                    DonutText(
                        text = outgoingCount.toString(),
                        isLoading = isLoading
                    )
                },
                outerSize = 72f,
                innerSize = 64f,
                outerColor = MaterialTheme.colorScheme.tertiary,
                innerColor = AppTheme.extended.cardSurface
            )
        )
        DonutChart(
            item = DonutItem(
                title = { DonutTitle(text = stringResource(R.string.total)) },
                textInside = {
                    DonutText(
                        text = totalCallsCount.toString(),
                        isLoading = isLoading
                    )
                },
                outerSize = 72f,
                innerSize = 64f,
                outerColor = MaterialTheme.colorScheme.secondary,
                innerColor = AppTheme.extended.cardSurface
            )
        )
    }
}

@Composable
private fun DonutTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        maxLines = 2
    )
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
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = spacer,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = endDate?.getDayFormatted(withYear = false) ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            color = MaterialTheme.colorScheme.primary
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
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
