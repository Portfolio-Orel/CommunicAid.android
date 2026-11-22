package com.orels.presentation.ui.unhandled_calls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.orels.presentation.R
import com.orels.presentation.ui.components.OnLifecycleEvent
import com.orels.presentation.ui.unhandled_calls.components.UnhandledCallRow


@Composable
fun UnhandledCallsScreen(
    viewModel: UnhandledCallsViewModel = hiltViewModel()
) {
    val isRefreshing = viewModel.isRefreshing

    OnLifecycleEvent(
        onResume = viewModel::onResume
    )

    val state = viewModel.state
    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(44.dp),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }

    } else {
        SwipeRefresh(
            modifier = Modifier.fillMaxSize(),
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { viewModel.refresh(isPullToRefresh = true) },
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val minHeight = maxHeight
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (state.callsToHandle.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = minHeight)
                                .padding(horizontal = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(112.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    modifier = Modifier.size(56.dp),
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Done icon",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                stringResource(R.string.no_unhandled_calls),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            state.callsToHandle.forEach {
                                UnhandledCallRow(
                                    phoneCall = it.phoneCall,
                                    photoUri = it.photoUri,
                                    canDelete = state.canDeleteCalls,
                                    onDelete = { viewModel.onDelete(it) },
                                    onCall = { viewModel.onCall(it) },
                                )
                            }
                        }
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
