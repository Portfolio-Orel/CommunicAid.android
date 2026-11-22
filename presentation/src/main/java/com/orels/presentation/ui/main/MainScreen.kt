package com.orels.presentation.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.orels.domain.util.Screen
import com.orels.presentation.ui.components.OnLifecycleEvent
import com.orels.presentation.ui.components.multi_fab.QuickActionsSpeedDial
import com.orels.presentation.ui.main.components.FoldersSheet
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

    val spaceBetweenMessages = 18
    val messageWidth =
        getMessageWidth(screenWidth = screen.screenWidthDp, spaceBetween = spaceBetweenMessages)
    val messageHeight = (messageWidth * 1.4f)

    val refreshSwipeState = rememberSwipeRefreshState(state.isRefreshing)

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

    var showFolders by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = showFolders) { showFolders = false }

    OnLifecycleEvent(onResume = viewModel::onResume)
    Box(modifier = modifier.fillMaxSize()) {
        SwipeRefresh(
            modifier = Modifier.fillMaxSize(),
            state = refreshSwipeState,
            onRefresh = viewModel::refresh,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MessagesContainer(
                    modifier = Modifier.weight(1f),
                    messages = state.selectedFoldersMessages,
                    onClick = { viewModel.onMessageClick(it) },
                    onLongClick = { message, context ->
                        viewModel.onMessageLongClick(
                            message,
                            context
                        )
                    },
                    spaceBetweenMessages = spaceBetweenMessages.dp,
                    messageHeight = messageHeight,
                    messageWidth = messageWidth,
                    borderColor = MaterialTheme.colorScheme.onBackground,
                    isLoading = state.isLoading
                )
            }
        }

        QuickActionsSpeedDial(
            modifier = Modifier.fillMaxSize(),
            folderTitle = state.selectedFolder?.title,
            onFoldersClick = {
                viewModel.onFoldersDropdownClick()
                showFolders = true
            },
            onAddMessage = { navController.navigate(Screen.DetailsMessage.route) },
            onAddFolder = { navController.navigate(Screen.DetailsFolder.route) },
            onUnhandledCalls = { navController.navigate(Screen.UnhandledCalls.route) },
            onStatistics = { navController.navigate(Screen.Statistics.route) },
        )

        FoldersSheet(
            visible = showFolders,
            folders = state.folders,
            selected = state.selectedFolder,
            onSelect = {
                viewModel.onFolderClick(it)
                showFolders = false
            },
            onEdit = {
                showFolders = false
                viewModel.editFolder(it)
            },
            onAddFolder = {
                showFolders = false
                navController.navigate(Screen.DetailsFolder.route)
            },
            onDismiss = { showFolders = false },
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
