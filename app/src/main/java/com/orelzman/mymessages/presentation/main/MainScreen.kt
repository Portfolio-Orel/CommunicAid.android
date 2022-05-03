package com.orelzman.mymessages.presentation.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.orelzman.mymessages.ui.theme.MyMessagesTheme
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
    val boxWidth = ((screen.screenWidthDp) / state.maxMessagesInRow).dp
    val boxHeight = (boxWidth * 1.3f)
    MyMessagesTheme {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    state.callOnTheLine,
                    style = MaterialTheme.typography.h3
                )
            }
            Divider(
                color = MaterialTheme.colors.primary.copy(alpha = 0.05f)
            )
            Row(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                state.folders.forEach {
                    FolderView(
                        folder = it,
                        isSelected = state.selectedFolder.id == it.id,
                        modifier = Modifier
                            .height(50.dp)
                            .width(120.dp)
                            .clickable { viewModel.setSelectedFolder(it) }
                    )
                }
            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 30.dp, end = 30.dp)
                    .fillMaxHeight(0.8F)
                    .fillMaxWidth(0.9F),
                mainAxisSpacing = 16.dp
            ) {
                viewModel.state.messages
                    .getByIds(state.selectedFolder.messages)
                    .forEach {
                        MessageView(
                            message = it,
                            modifier = Modifier
                                .width(boxWidth)
                                .height(boxHeight)
                                .padding(0.dp),
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
}