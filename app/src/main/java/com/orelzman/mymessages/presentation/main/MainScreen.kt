package com.orelzman.mymessages.presentation.main

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = hiltViewModel()
) {
    if(viewModel.state.isLoggedIn) {
        Text(text = "LoggedIn")
    } else {
        Text(text = "!LoggedIn")
    }
}