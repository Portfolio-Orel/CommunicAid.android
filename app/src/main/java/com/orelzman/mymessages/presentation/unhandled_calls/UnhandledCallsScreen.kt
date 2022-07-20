package com.orelzman.mymessages.presentation.unhandled_calls

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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


@Composable
fun UnhandledCallsScreen(
    viewModel: UnhandledCallsViewModel = hiltViewModel()
) {
    val isRefreshing = viewModel.isRefreshing

    val state = viewModel.state
    if(state.isLoading){
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .height(48.dp)
                    .width(48.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

    } else {
        Column(modifier = Modifier.fillMaxSize()) {
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
}

@Preview
@Composable
fun UnhandledCallsScreen_Preview() {
    UnhandledCallsScreen()
}