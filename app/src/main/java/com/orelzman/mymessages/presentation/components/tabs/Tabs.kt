package com.orelzman.mymessages.presentation.components.tabs

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun Tabs(
    onClick: (Tab) -> Unit,
    modifier: Modifier = Modifier,
    tabs: List<Tab> = emptyList()
) {
    val configuration = LocalConfiguration.current

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
                    .background(
                        if (selected == tab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                    )
                    .onGloballyPositioned { layoutCoordinates ->
                        tabWidth = layoutCoordinates.size.width / tabs.size
                    }
                    .width(tabWidth.dp)
                    .fillMaxHeight()
                    .clickable {
                        selected = tab
                        onClick(tab)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(tab.title),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected == tab) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                )
            }
        }

    }
}

//enum class Tab(@StringRes val title: Int) {
//    All(R.string.all),
//    Months(R.string.months);
//}

data class Tab(
    @StringRes val title: Int,
)