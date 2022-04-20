package com.orelzman.mymessages.presentation.add_message

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.presentation.destinations.MainScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun AddMessageScreen(
    navigator: DestinationsNavigator,
    viewModel: AddMessageViewModel = hiltViewModel()
) {
    val state = viewModel.state
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = state.title,
            onValueChange = { viewModel.setTitle(it) },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = "כותרת")
            },
        )
        OutlinedTextField(
            value = viewModel.state.shortTitle,
            onValueChange = { viewModel.setShortTitle(it) },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = "כותרת קצרה")
            }
        )
        OutlinedTextField(
            value = state.body,
            onValueChange = { viewModel.setBody(it) },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = "טקסט")
            },
            maxLines = 30,
        )
        Row() {
            Button(onClick = { viewModel.addMessage() }) {
                Text("שמור")
            }
            Button(onClick = { navigator.navigate(MainScreenDestination)}) {
                Text("בטל")
            }
        }
    }
}