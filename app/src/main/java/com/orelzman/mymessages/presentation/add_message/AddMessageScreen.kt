package com.orelzman.mymessages.presentation.add_message

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
            value = state.shortTitle,
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
            isError = state.body == ""
        )
//        DropdownMenu(expanded = true, onDismissRequest = { print("dismiss") }) {
//            viewModel.state.folders.forEach {
//                    Text(it.folderTitle)
//            }
//        }
        Button(onClick = { viewModel.addMessage() }) {
            
        }
    }
}