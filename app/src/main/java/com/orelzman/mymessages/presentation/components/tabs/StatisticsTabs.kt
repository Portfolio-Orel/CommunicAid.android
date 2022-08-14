package com.orelzman.mymessages.presentation.components.tabs

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.util.common.DateUtils
import java.util.*

@Composable
fun StatisticsTabs(
    onClick: (Date, Date) -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val tabs = StatisticsTabs.values()
    var selected by remember { mutableStateOf(tabs.first()) }
    var tabWidth = configuration.screenWidthDp / tabs.size

    Row(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEach { tab ->
            Box(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        tabWidth = layoutCoordinates.size.width / tabs.size
                    }
                    .width(tabWidth.dp)
                    .fillMaxHeight()
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(4.dp), clip = true)
                    .background(
                        if (selected == tab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                    )
                    .clickable {
                        selected = tab
                        onClick(tab.startDate, tab.endDate)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(tab.title),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected == tab) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                )
            }
        }

    }
}

enum class StatisticsTabs(@StringRes val title: Int, val startDate: Date, val endDate: Date) {
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
        startDate = DateUtils.getFirstDayOfSystem(),
        endDate = DateUtils.getLastDayOfSystem()
    );
}