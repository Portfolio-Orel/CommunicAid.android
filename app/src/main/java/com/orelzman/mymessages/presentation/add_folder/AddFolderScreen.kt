package com.orelzman.mymessages.presentation.add_folder

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.presentation.destinations.MainScreenDestination
import com.orelzman.mymessages.ui.theme.MyMessagesTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun AddFolderScreen(
    navigator: DestinationsNavigator,
    viewModel: AddFolderViewModel = hiltViewModel()
) {
    val state = viewModel.state

    if(state.isFolderAdded) {
        navigator.navigate(MainScreenDestination)
    }
    MyMessagesTheme {
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
            Row {
                Button(
                    onClick = { viewModel.saveFolder() },
                    modifier = Modifier.padding(start = 32.dp, bottom = 32.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(bottom = 12.dp),
                            color = Color.White
                        )
                    } else {
                        Text("שמור")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { navigator.navigate(MainScreenDestination) },
                    modifier = Modifier.padding(end = 32.dp, bottom = 32.dp)
                ) {
                    Text("בטל")
                }
            }
        }
    }
}