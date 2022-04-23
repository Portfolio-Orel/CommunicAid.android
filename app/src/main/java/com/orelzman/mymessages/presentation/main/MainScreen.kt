package com.orelzman.mymessages.presentation.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.data.dto.getByIds
import com.orelzman.mymessages.presentation.destinations.AddFolderScreenDestination
import com.orelzman.mymessages.presentation.destinations.AddMessageScreenDestination
import com.orelzman.mymessages.presentation.main.components.FolderView
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun MainScreen(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val state = viewModel.state

    Column {
            Text(state.callOnTheLine)
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            state.folders.forEach {
                FolderView(
                    folder = it,
                    isSelected =state.selectedFolder.id == it.id,
                    modifier = Modifier
                        .height(40.dp)
                        .width(120.dp)
                        .clickable { viewModel.setSelectedFolder(it) }
                )
            }
        }

        viewModel.state.messages
            .getByIds(state.selectedFolder.messages)
            .forEach {
                Text(it.messageTitle)
            }

        Button(onClick = {
            navigator.navigate(
                AddFolderScreenDestination()
            )
        }) {
            Text(text = "Add Folder")
        }

        Button(onClick = { navigator.navigate(
            AddMessageScreenDestination()
        ) }) {
            Text(text = "Add Message")
        }
    }
}