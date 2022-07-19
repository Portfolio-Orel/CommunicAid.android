package com.orelzman.mymessages.presentation.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.orelzman.mymessages.presentation.main.components.ActionButton

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val isRefreshing by viewModel.isRefreshing.collectAsState(false)
    val state = viewModel.state

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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "שיחות היום: ", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = state.callsCountToday.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "שיחות שלא הועלו: ", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = state.callsNotUploaded.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "שיחות שהועלו: ", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = state.callsUploaded.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "שיחות תקועות: ", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = state.callsBeingUploaded.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            ActionButton(
                onClick = { viewModel.sendCallLogs() },
                text = "שלח יומן",
                isLoading = state.isLoadingCallLogSend
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "עודכן לאחרונה: ", style = MaterialTheme.typography.titleSmall)
                Text(text = state.lastUpdateDate, style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}