package com.orelzman.mymessages.presentation.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import com.orelzman.mymessages.R
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.flowlayout.SizeMode
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.orelzman.mymessages.presentation.BackPressHandler
import com.orelzman.mymessages.presentation.components.CustomScaffold
import com.orelzman.mymessages.presentation.components.bottom_bar.BottomBar
import com.orelzman.mymessages.presentation.components.multi_fab.MiniFloatingAction
import com.orelzman.mymessages.presentation.components.multi_fab.MultiFab
import com.orelzman.mymessages.presentation.destinations.DetailsFolderScreenDestination
import com.orelzman.mymessages.presentation.destinations.DetailsMessageScreenDestination
import com.orelzman.mymessages.presentation.main.components.FolderView
import com.orelzman.mymessages.presentation.main.components.MessageView
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@ExperimentalFoundationApi
@Composable
@Destination
fun MainScreen(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    BackPressHandler {
        navigator.popBackStack()
    }

    val navController = rememberAnimatedNavController()
    CustomScaffold(
        navController = navController,
        bottomBar = { destination ->
            BottomBar(
                currentDestination = destination,
                onBottomBarItemClick = {
                    navigator.navigate(it) {
                        launchSingleTop = true
                    }
                }
            )
        },
        floatingActionButton = {
            MultiFab(
                fabs = listOf(
                    MiniFloatingAction(
                        action = {
                            navigator.navigate(
                                DetailsMessageScreenDestination()
                            )
                        },
                        icon = painterResource(id = R.drawable.ic_new_message),
                        description = ""
                    ),
                    MiniFloatingAction(
                        action = {
                            navigator.navigate(
                                DetailsFolderScreenDestination()
                            )
                        },
                        icon = painterResource(id = R.drawable.ic_new_folder),
                        description = ""
                    )
                ), fabIcon = Icons.Filled.Add
            )
        },
        floatingActionButtonPosition = FabPosition.Center,

    )
//    Scaffold(
//        topBar = {
//            Box(
//                modifier = Modifier.fillMaxWidth(),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = if (state.activeCall?.number == "" || state.activeCall == null)
//                            stringResource(R.string.no_active_call)
//                        else
//                            state.activeCall.number,
//                        modifier = Modifier
//                            .padding(8.dp),
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                    if (state.callInBackground != null) {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.Center,
//                        ) {
//                            Button(
//                                modifier = Modifier
//                                    .border(
//                                        1.dp,
//                                        color = MaterialTheme.colorScheme.primary,
//                                        shape = RoundedCornerShape(32.dp)
//                                    )
//                                    .width(150.dp)
//                                    .height(36.dp),
//                                colors = ButtonDefaults.buttonColors(
//                                    containerColor =
//                                    if (state.activeCall == state.callOnTheLine) MaterialTheme.colorScheme.primary
//                                    else Color.Transparent
//                                ),
//                                onClick = { viewModel.setCallOnTheLineActive() }) {
//                                Text(
//                                    state.callOnTheLine?.number ?: "",
//                                    style = MaterialTheme.typography.bodySmall,
//                                    maxLines = 1,
//                                    overflow = TextOverflow.Ellipsis,
//                                    color = if (state.activeCall == state.callOnTheLine) MaterialTheme.colorScheme.onPrimary
//                                    else MaterialTheme.colorScheme.onBackground
//                                )
//                            }
//
//                            Button(
//                                modifier = Modifier
//                                    .border(
//                                        1.dp,
//                                        color = MaterialTheme.colorScheme.primary,
//                                        shape = RoundedCornerShape(32.dp)
//                                    )
//                                    .width(150.dp)
//                                    .height(36.dp),
//                                colors = ButtonDefaults.buttonColors(
//                                    containerColor =
//                                    if (state.activeCall == state.callInBackground) MaterialTheme.colorScheme.primary
//                                    else Color.Transparent
//                                ),
//                                onClick = { viewModel.setBackgroundCallActive() }) {
//                                Text(
//                                    state.callInBackground.number,
//                                    style = MaterialTheme.typography.bodySmall,
//                                    maxLines = 1,
//                                    overflow = TextOverflow.Ellipsis,
//                                    color = if (state.activeCall == state.callInBackground) MaterialTheme.colorScheme.onPrimary
//                                    else MaterialTheme.colorScheme.onBackground
//                                )
//
//                            }
//                        }
//                    }
//                }
//            }
//            Divider(
//                modifier = Modifier.padding(8.dp),
//                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
//            )
//        },
//        bottomBar = { destination ->
//            BottomBar(
//                currentDestination = destination,
//                onBottomBarItemClick = {
//                    navigator.navigate(it) {
//                        launchSingleTop = true
//                    }
//                }
//            )
//        },
//        floatingActionButton = {
//            MultiFab(
//                fabs = listOf(
//                    MiniFloatingAction(
//                        action = {
//                            navigator.navigate(
//                                DetailsMessageScreenDestination()
//                            )
//                        },
//                        icon = painterResource(id = R.drawable.ic_new_message),
//                        description = ""
//                    ),
//                    MiniFloatingAction(
//                        action = {
//                            navigator.navigate(
//                                DetailsFolderScreenDestination()
//                            )
//                        },
//                        icon = painterResource(id = R.drawable.ic_new_folder),
//                        description = ""
//                    )
//                ), fabIcon = Icons.Filled.Add
//            )
//        },
//        floatingActionButtonPosition = FabPosition.Center,
//        content = {
//            when (state.screenToShow) {
//                MainScreens.Stats -> StatsScreen()
//                MainScreens.UnhandledCalls -> UnhandledCallsScreen()
//                else -> Content(
//                    modifier = Modifier.padding(it),
//                    navigator = navigator,
//                    viewModel = viewModel
//                )
//            }
//        }
//    )


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    viewModel: MainViewModel
) {
    val state = viewModel.state
    val screen = LocalConfiguration.current
    val spaceBetweenMessages = 26
    val boxWidth =
        getMessageWidth(screenWidth = screen.screenWidthDp, spaceBetween = spaceBetweenMessages)

    val boxHeight = (boxWidth * 1.5f)
    val messagesOffset = remember { mutableStateOf(0f) }


    LaunchedEffect(key1 = viewModel) {
        viewModel.init()
    }

    LaunchedEffect(key1 = viewModel.state.screenToShow) {
        val route =
            when (viewModel.state.screenToShow) {
                MainScreens.DetailsMessage -> DetailsMessageScreenDestination(messageId = state.messageToEdit?.id)
                MainScreens.DetailsFolder -> DetailsFolderScreenDestination(folderId = state.folderToEdit?.id)
                else -> null
            }
        if (route != null) {
            navigator.navigate(route)
            viewModel.navigated()
        }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyRow(
                modifier = Modifier
                    .padding(8.dp),
                userScrollEnabled = true,
            ) {
                items(state.folders) { folder ->
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

            FlowRow(
                modifier = Modifier
                    .padding(start = 10.dp, end = 5.dp)
                    .fillMaxHeight(0.6F)
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
                viewModel.getFoldersMessages()
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

private fun getMessageWidth(screenWidth: Int, messagesInRow: Int = 4, spaceBetween: Int = 16): Dp {
    val spacesCount = messagesInRow + 1 // Amount of spaces between each message
    val spaceForMessages = screenWidth - spacesCount * spaceBetween
    return (spaceForMessages / messagesInRow).dp
}