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

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {

    val state = viewModel.state
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            modifier =
            Modifier
                .width(128.dp)
                .height(48.dp)

            ,
            onClick = { viewModel.login() }) {
            if(!state.isLoadingLogin) {
                Text("Login")
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = Color.White
                )
            }
        }
        state.user?.firebaseUser?.uid.let {
            Text("User:")
            Text("$it")
        }
    }
}