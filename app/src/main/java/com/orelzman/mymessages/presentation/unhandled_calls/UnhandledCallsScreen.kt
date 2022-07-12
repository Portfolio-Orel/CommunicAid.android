package com.orelzman.mymessages.presentation.unhandled_calls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.orelzman.mymessages.R
import com.orelzman.mymessages.presentation.unhandled_calls.components.UnhandledCallRow
import com.orelzman.mymessages.util.extension.DefaultDestinationNavigator
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun UnhandledCallsScreen(
    navigator: DestinationsNavigator,
    viewModel: UnhandledCallsViewModel = hiltViewModel()
) {
    val isRefreshing by viewModel.isRefreshing.collectAsState(false)

    val onBack = { navigator.navigateUp() }
    val state = viewModel.state

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(2f),
            horizontalArrangement = Arrangement.End,
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Go back from unhandled calls",
                modifier = Modifier
                    .padding(12.dp)
                    .size(48.dp)
                    .clickable(
                        onClick = { onBack() },
                    ),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Divider(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
        SwipeRefresh(
            modifier = Modifier.fillMaxSize(),
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { viewModel.refresh() },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (state.callsToHandle.isEmpty()) {
                    Spacer(Modifier.weight(1f))
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(92.dp)
                                .padding(8.dp),
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            stringResource(R.string.no_unhandled_calls),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(Modifier.weight(1f))
                } else {
                    state.callsToHandle.forEach {
                        Box(
                            modifier = Modifier
                                .height(60.dp)
                                .padding(),
                        ) {
                            UnhandledCallRow(
                                phoneCall = it.phoneCall,
                                onDelete = { viewModel.onDelete(it) },
                                onCall = { viewModel.onCall(it) },
                                onClick = { viewModel.onCall(it) }
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview
@Composable
fun UnhandledCallsScreen_Preview() {
    UnhandledCallsScreen(navigator = DefaultDestinationNavigator())
}