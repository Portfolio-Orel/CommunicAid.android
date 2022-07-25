package com.orelzman.mymessages.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.flowlayout.SizeMode
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.presentation.components.scrollable_flowrow.ScrollableFlowRow
import com.orelzman.mymessages.presentation.main.components.FolderView
import com.orelzman.mymessages.presentation.main.components.MessageView
import com.orelzman.mymessages.util.Screen


@ExperimentalFoundationApi
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(),
) {
    Content(navController = navController, viewModel = viewModel)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(
    navController: NavController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state
    val screen = LocalConfiguration.current
    val spaceBetweenMessages = 18
    val boxWidth =
        getMessageWidth(screenWidth = screen.screenWidthDp, spaceBetween = spaceBetweenMessages)

    val boxHeight = (boxWidth * 1.5f)

    LaunchedEffect(key1 = viewModel) {
        viewModel.init()
    }

    LaunchedEffect(key1 = state.screenToShow) {
        when (state.screenToShow) {
            MainScreens.DetailsFolder -> {
                navController.navigate(Screen.DetailsFolder.withArgs(state.folderToEdit?.id))
            }
            MainScreens.DetailsMessage -> {
                navController.navigate(Screen.DetailsMessage.withArgs(state.messageToEdit?.id))
            }
            else -> {}
        }
        viewModel.navigated()
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .height(32.dp)
                    .width(32.dp),
                strokeWidth = 1.dp,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.callInBackground != null) {
                WaitingCallBar(
                    activeCall = state.activeCall,
                    callOnTheLine = state.callOnTheLine,
                    callInBackground = state.callInBackground,
                    setCallInBackground = {
                        viewModel.setBackgroundCallActive()
                    },
                    setCallOnTheLine = {
                        viewModel.setCallOnTheLineActive()
                    }
                )
            }

            LazyRow(
                modifier = Modifier
                    .padding(bottom = 8.dp),
                userScrollEnabled = true,
            ) {
                items(
                    state.folders
                ) { folder ->
                    FolderView(
                        folder = folder,
                        isSelected = state.selectedFolder?.id == folder.id,
                        modifier = Modifier
                            .height(50.dp)
                            .width(120.dp),
                        onClick = { viewModel.setSelectedFolder(it) },
                        onLongClick = { viewModel.onFolderLongClick(it) }
                    )
                }
            }

            ScrollableFlowRow(
                modifier = Modifier
                    .padding(start = 3.dp, end = 2.dp)
                    .fillMaxWidth(0.95F)
                    .fillMaxHeight(0.9F),
                mainAxisSpacing = spaceBetweenMessages.dp,
                mainAxisAlignment = MainAxisAlignment.SpaceEvenly,
                mainAxisSize = SizeMode.Expand
            ) {
                viewModel.getFoldersMessages() // ToDo: Problematic?
                    .sortedByDescending { it.timesUsed }
                    .forEach {
                        MessageView(
                            message = it,
                            modifier = Modifier
                                .width(boxWidth)
                                .height(boxHeight)
                                .padding(0.dp),
                            onClick = { message, context ->
                                viewModel.onMessageClick(message, context)
                            },
                            onLongClick = { message, context ->
                                viewModel.onMessageLongClick(message, context)
                            }
                        )
                    }
            }
        }
    }
}

@Composable
fun WaitingCallBar(
    activeCall: PhoneCall?,
    callOnTheLine: PhoneCall?,
    callInBackground: PhoneCall,
    setCallInBackground: () -> Unit,
    setCallOnTheLine: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        modifier = Modifier
                            .border(
                                1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(32.dp)
                            )
                            .width(150.dp)
                            .height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                            if (activeCall == callOnTheLine) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        ),
                        onClick = { setCallOnTheLine() }) {
                        Text(
                            callOnTheLine?.number ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (activeCall == callOnTheLine) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Button(
                        modifier = Modifier
                            .border(
                                1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(32.dp)
                            )
                            .width(150.dp)
                            .height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                            if (activeCall == callInBackground) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        ),
                        onClick = { setCallInBackground() }) {
                        Text(
                            callInBackground.number,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (activeCall == callInBackground) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onBackground
                        )

                    }
                }
                Divider(
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            }
        }
    }
}

private fun getMessageWidth(
    screenWidth: Int,
    messagesInRow: Int = 4,
    spaceBetween: Int = 4
): Dp {
    val spacesCount = messagesInRow + 1 // Amount of spaces between each message
    val spaceForMessages = screenWidth - spacesCount * spaceBetween
    return (spaceForMessages / messagesInRow).dp
}