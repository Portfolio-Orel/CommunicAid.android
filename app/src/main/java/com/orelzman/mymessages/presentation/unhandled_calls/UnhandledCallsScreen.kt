package com.orelzman.mymessages.presentation.unhandled_calls

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
    val state = viewModel.state
    viewModel.initCalls(LocalContext.current)
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        state.callsToHandle.forEach {
            Box(
                modifier = Modifier
                    .height(60.dp)
                    .padding(),
            ) {
                UnhandledCallRow(
                    phoneCall = it.phoneCall
                )
            }
        }
    }
}

@Preview
@Composable
fun UnhandledCallsScreen_Preview() {
    UnhandledCallsScreen(navigator = DefaultDestinationNavigator())
}