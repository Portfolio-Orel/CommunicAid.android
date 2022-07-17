package com.orelzman.mymessages

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.presentation.destinations.MainScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun MyMessagesApp(
    navigator: DestinationsNavigator,
    viewModel: MyMessagesViewModel = hiltViewModel()
) {
    if(viewModel.isAuthorized) {
        navigator.navigate(MainScreenDestination)
    } else {

    }
}