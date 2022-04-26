package com.orelzman.mymessages.presentation.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.orelzman.mymessages.data.dto.getByIds
import com.orelzman.mymessages.presentation.destinations.AddFolderScreenDestination
import com.orelzman.mymessages.presentation.destinations.AddMessageScreenDestination
import com.orelzman.mymessages.presentation.main.components.FolderView
import com.orelzman.mymessages.presentation.main.components.MessageView
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun MainScreen(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    val screen = LocalConfiguration.current
    val boxWidth = (screen.screenWidthDp * 0.8F)/state.maxMessagesInRow
    val boxHeight = boxWidth + 5

    Column {
        Text(state.callOnTheLine)
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            state.folders.forEach {
                FolderView(
                    folder = it,
                    isSelected = state.selectedFolder.id == it.id,
                    modifier = Modifier
                        .height(40.dp)
                        .width(120.dp)
                        .clickable { viewModel.setSelectedFolder(it) }
                )
            }
        }

        FlowRow(modifier = Modifier.fillMaxWidth()
            .padding(bottom = 16.dp, start = 30.dp, end = 30.dp)
            .fillMaxHeight(0.8F)
            .fillMaxWidth(0.9F),
        ) {
            viewModel.state.messages
                .getByIds(state.selectedFolder.messages)
                .forEach {
                    MessageView(
                        message = it,
                        modifier = Modifier
                            .width(boxWidth.dp)
                            .height(boxHeight.dp)
                            .padding(8.dp)
                        ,
                        onClick = { message, context ->
                            viewModel.sendMessage(message, context)
                        })
                }
        }

        Button(onClick = {
            navigator.navigate(
                AddFolderScreenDestination()
            )
        }) {
            Text(text = "הוסף תיקייה")
        }

        Button(onClick = {
            navigator.navigate(
                AddMessageScreenDestination()
            )
        }) {
            Text(text = "הוסף הודעה")
        }
    }
}