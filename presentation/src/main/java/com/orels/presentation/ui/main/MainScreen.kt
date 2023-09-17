package com.orels.presentation.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.orels.domain.util.Screen
import com.orels.presentation.ui.main.components.FoldersContainer
import com.orels.presentation.ui.main.components.MessagesContainer


@ExperimentalFoundationApi
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(),
) {
    Content(navController = navController, viewModel = viewModel)
}

@Composable
private fun Content(
    navController: NavController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val state = viewModel.state
    val screen = LocalConfiguration.current
    val screenHeight = screen.screenHeightDp

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

//    OnLifecycleEvent(onResume = viewModel::onResume)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (screenHeight > 500) {
            FoldersContainer(
                folders = state.folders,
                onClick = { viewModel.onFolderClick(it) },
                onEditClick = { viewModel.editFolder(it) },
                onDropdownClick = viewModel::onFoldersDropdownClick,
                addNewFolder = { navController.navigate(Screen.DetailsFolder.route) },
                selected = state.selectedFolder,
                color = MaterialTheme.colorScheme.onBackground,
                isLoading = state.isLoading
            )
        }
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
            messageHeight = messageHeight,
            messageWidth = messageWidth,
            borderColor = MaterialTheme.colorScheme.onBackground,
            isLoading = state.isLoading
        )
    }
}

private fun getMessageWidth(
    screenWidth: Int,
    messagesInRow: Int = 4,
    spaceBetween: Int = 4,
): Dp {
    if (messagesInRow == 0) return 0.dp
    val spacesCount = messagesInRow + 1 // Amount of spaces between each message
    val spaceForMessages = screenWidth - spacesCount * spaceBetween
    return (spaceForMessages / messagesInRow).dp
}