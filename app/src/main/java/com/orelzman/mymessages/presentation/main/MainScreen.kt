package com.orelzman.mymessages.presentation.main

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.domain.util.Screen
import com.orelzman.mymessages.domain.util.extension.Logger
import com.orelzman.mymessages.presentation.components.OnLifecycleEvent
import com.orelzman.mymessages.presentation.main.components.FolderView
import com.orelzman.mymessages.presentation.main.components.MessageView
import kotlin.system.measureTimeMillis


@ExperimentalFoundationApi
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(),
) {
    OnLifecycleEvent(
        onResume = viewModel::onResume,
    )

    val timeToBuildContent = measureTimeMillis {
        Content(navController = navController, viewModel = viewModel)
    }
    Logger.v("Time to build Main content: ${timeToBuildContent}ms")
}

@Composable
private fun Content(
    navController: NavController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state
    val screen = LocalConfiguration.current
    val spaceBetweenMessages = 18
    val messageWidth =
        getMessageWidth(screenWidth = screen.screenWidthDp, spaceBetween = spaceBetweenMessages)

    val messageHeight = (messageWidth * 1.5f)

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
            FoldersList(
                modifier = Modifier.padding(bottom = 32.dp),
                folders = state.folders,
                onClick = { viewModel.onFolderClick(it) },
                onLongClick = { viewModel.onFolderLongClick(it) },
                isSelected = { state.selectedFolder?.id == it.id }
            )

            MessagesList(
                messages = viewModel.getFoldersMessages(),
                onClick = { viewModel.onMessageClick(it) },
                onLongClick = { message, context ->
                    viewModel.onMessageLongClick(
                        message,
                        context
                    )
                },
                spaceBetweenMessages = spaceBetweenMessages.dp,
                height = messageHeight,
                width = messageWidth
            )

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FoldersList(
    folders: List<Folder>,
    onClick: (Folder) -> Unit,
    onLongClick: (Folder) -> Unit,
    isSelected: (Folder) -> Boolean,
    modifier: Modifier = Modifier
) {
    val state = rememberScrollState()

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides
                null
    ) {
        LazyRow(
            modifier = modifier
                .scrollable(state = state, Orientation.Horizontal)
                .fillMaxWidth(),
        ) {
            items(folders) { folder ->
                FolderView(
                    modifier = Modifier
                        .height(50.dp)
                        .width(120.dp),
                    folder = folder,
                    isSelected = isSelected(folder),
                    onClick = onClick,
                    onLongClick = onLongClick
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessagesList(
    messages: List<Message>,
    onClick: (Message) -> Unit,
    onLongClick: (Message, Context) -> Unit,
    spaceBetweenMessages: Dp,
    height: Dp,
    width: Dp
) {
    LazyVerticalGrid(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(1F)
            .fillMaxHeight(0.9F),
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(spaceBetweenMessages),
        verticalArrangement = Arrangement.spacedBy(spaceBetweenMessages)
    ) {
        items(
            count = messages.size,
            key = { index ->
                // Return a stable + unique key for the item
                index
            }
        ) { index ->
            MessageView(
                message = messages[index],
                modifier = Modifier
                    .height(height)
                    .width(width)
                    .padding(0.dp),
                onClick = onClick,
                onLongClick = onLongClick
            )
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
    if (messagesInRow == 0) return 0.dp
    val spacesCount = messagesInRow + 1 // Amount of spaces between each message
    val spaceForMessages = screenWidth - spacesCount * spaceBetween
    return (spaceForMessages / messagesInRow).dp
}