package com.orels.presentation.ui.statistics.components

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.orels.domain.util.common.DateUtils
import com.orels.presentation.R
import java.util.*

@Composable
fun StatisticsTabs(
    onClick: (StatisticsTabs) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = StatisticsTabs.values()
    var selected by remember { mutableStateOf(tabs.first()) }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEach { tab ->
            val isSelected = selected == tab
            val containerColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                animationSpec = tween(durationMillis = 250)
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(durationMillis = 250)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(containerColor)
                    .clickable {
                        selected = tab
                        onClick(tab)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(tab.title),
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor
                )
            }
        }
    }
}

enum class StatisticsTabs(@StringRes val title: Int, val startDate: Date?, val endDate: Date?) {
    Week(
        title = R.string.week,
        startDate = DateUtils.getFirstDayOfWeek(),
        endDate = DateUtils.getLastDayOfWeek()
    ),
    Month(
        title = R.string.month,
        startDate = DateUtils.getFirstDayOfMonth(),
        endDate = DateUtils.getLastDayOfMonth()
    ),
    All(
        title = R.string.all,
        startDate = null,
        endDate = null
    );
}