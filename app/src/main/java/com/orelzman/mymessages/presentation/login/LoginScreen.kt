package com.orelzman.mymessages.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.presentation.destinations.MainScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination(start = true)
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val state = viewModel.state
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if(viewModel.state.user != null) {
            navigator.navigate(MainScreenDestination)
        } else {
            Text(text = "!LoggedIn")
        }
        Button(
            modifier =
            Modifier
                .width(128.dp)
                .height(48.dp)

            ,
            onClick = {
                viewModel.onEvent(LoginEvents.AuthWithEmailAndPassowrd())
            }) {
            if(!state.isLoading) {
                Text("Login")
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = Color.White
                )
            }
        }
    }
}