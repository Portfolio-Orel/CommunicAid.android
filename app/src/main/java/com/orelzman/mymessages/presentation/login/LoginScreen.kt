package com.orelzman.mymessages.presentation.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        Button(onClick = { viewModel.login() }) {

        }
        state.user?.firebaseUser?.uid.let {
            Text("User:")
            Text("$it")
        }
    }
}