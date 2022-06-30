package com.orelzman.mymessages.presentation.main

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.flowlayout.SizeMode
import com.orelzman.mymessages.R
import com.orelzman.mymessages.presentation.destinations.DetailsFolderScreenDestination
import com.orelzman.mymessages.presentation.destinations.DetailsMessageScreenDestination
import com.orelzman.mymessages.presentation.destinations.UnhandledCallsScreenDestination
import com.orelzman.mymessages.presentation.main.components.FolderView
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
    val spaceBetweenMessages = 26
    val boxWidth = getMessageWidth(screenWidth = screen.screenWidthDp, spaceBetween = spaceBetweenMessages)
    val boxHeight = (boxWidth * 1.5f)
    val messagesOffset = remember { mutableStateOf(0f) }
    val foldersOffset = remember { mutableStateOf(0f) }


    if (state.messageToEdit != null) {
        navigator.navigate(
            DetailsMessageScreenDestination(state.messageToEdit.id)
        )
    }

    MyMessagesTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    state.callOnTheLine,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Divider(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            )
            Row(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                state.folders.forEach { folder ->
                    FolderView(
                        folder = folder,
                        isSelected = state.selectedFolder.id == folder.id,
                        modifier = Modifier
                            .height(50.dp)
                            .width(120.dp),
                        onClick = { viewModel.setSelectedFolder(it) },
                        onLongClick = { viewModel.onFolderLongClick(it) }
                    )
                }
            }

            FlowRow(
                modifier = Modifier
                    .padding(start = 10.dp, end = 5.dp)
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.8F)
                    .fillMaxWidth(0.9F)
                    .scrollable(
                        orientation = Orientation.Vertical,
                        state = rememberScrollableState { delta ->
                            messagesOffset.value = messagesOffset.value + delta
                            delta
                        }
                    ),
                mainAxisSpacing = spaceBetweenMessages.dp,
                mainAxisAlignment = MainAxisAlignment.Start,
                mainAxisSize = SizeMode.Wrap
            ) {
//                state.messages
//                    .getByIds(state.selectedFolder.messageIds)
//                    .forEach {
//                        MessageView(
//                            message = it,
//                            modifier = Modifier
//                                .width(boxWidth)
//                                .height(boxHeight)
//                                .padding(0.dp)
//                                .scrollable(
//                                    orientation = Orientation.Horizontal,
//                                    state = rememberScrollableState { delta ->
//                                        foldersOffset.value = foldersOffset.value + delta
//                                        delta
//                                    }
//                                ),
//                            onClick = { message, context ->
//                                viewModel.onMessageClick(message, context)
//                            },
//                            onLongClick = { message, context ->
//                                viewModel.onMessageLongClick(message, context)
//                            }
//                        )
//                    }
            }
            Button(onClick = {
                navigator.navigate(
                    DetailsFolderScreenDestination()
                )
            }) {
                Text(text = stringResource(R.string.add_folder))
            }

            Button(onClick = {
                navigator.navigate(
                    DetailsMessageScreenDestination()
                )
            }) {
                Text(text = stringResource(R.string.add_message))
            }

            Button(onClick = {
                navigator.navigate(
                        UnhandledCallsScreenDestination()
                )
            }) {
                Text(text = stringResource(R.string.unhandled_calls))
            }
        }
    }
}

private fun getMessageWidth(screenWidth: Int, messagesInRow: Int = 4, spaceBetween: Int = 16): Dp {
    val spacesCount = messagesInRow + 1 // Amount of spaces between each message
    val spaceForMesages = screenWidth - spacesCount * spaceBetween
    return (spaceForMesages / messagesInRow).dp
}

private fun calculateMinIndexForSecondFlowRow(itemsCount: Int, maxItems: Int): Int =
    itemsCount - itemsCount % maxItems
