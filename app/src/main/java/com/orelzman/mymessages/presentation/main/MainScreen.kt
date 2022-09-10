package com.orelzman.mymessages.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.orelzman.mymessages.domain.util.Screen
import com.orelzman.mymessages.presentation.main.components.FoldersContainer
import com.orelzman.mymessages.presentation.main.components.MessagesContainer


@ExperimentalFoundationApi
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(),
) = Content(navController = navController, viewModel = viewModel)

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
            FoldersContainer(
                modifier = Modifier.padding(bottom = 32.dp),
                folders = state.folders,
                onClick = { viewModel.onFolderClick(it) },
                onLongClick = { viewModel.onFolderLongClick(it) },
                addNewFolder = { navController.navigate(Screen.DetailsFolder.route) },
                selected = state.selectedFolder,
                color = MaterialTheme.colorScheme.primary
            )
            if (state.folders.isNotEmpty()) {
                MessagesContainer(
                    messages = state.selectedFoldersMessages,
                    onClick = { viewModel.onMessageClick(it) },
                    onLongClick = { message, context ->
                        viewModel.onMessageLongClick(
                            message,
                            context
                        )
                    },
                    addNewMessage = {
                        navController.navigate(Screen.DetailsMessage.route)
                    },
                    spaceBetweenMessages = spaceBetweenMessages.dp,
                    height = messageHeight,
                    width = messageWidth,
                    borderColor = MaterialTheme.colorScheme.primary
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