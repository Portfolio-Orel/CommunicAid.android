@file:OptIn(ExperimentalFoundationApi::class)

package com.orelzman.mymessages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.orelzman.mymessages.presentation.components.CustomScaffold
import com.orelzman.mymessages.presentation.components.bottom_bar.BottomBar
import com.orelzman.mymessages.presentation.components.multi_fab.MiniFloatingAction
import com.orelzman.mymessages.presentation.components.multi_fab.MultiFab
import com.orelzman.mymessages.util.Screen

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun MyMessagesApp(
    navController: NavController,
    viewModel: MyMessagesViewModel = hiltViewModel()
) {
    val startRoute =
        if (!viewModel.isAuthorized) Screen.Login else Screen.Main

    CustomScaffold(
        startRoute = startRoute.route,
        navController = navController,
        bottomBar = { destination ->
            BottomBar(
                currentDestination = destination,
                onBottomBarItemClick = {
                    navController.navigate(it.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = false
                        }
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
                            navController.navigate(
                                Screen.DetailsMessage.route
                            ) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        },
                        icon = painterResource(id = R.drawable.ic_new_message),
                        description = ""
                    ),
                    MiniFloatingAction(
                        action = {
                            navController.navigate(
                                Screen.DetailsFolder.route
                            ) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        },
                        icon = painterResource(id = R.drawable.ic_new_folder),
                        description = ""
                    )
                ), fabIcon = Icons.Filled.Add
            )
        },
        floatingActionButtonPosition = FabPosition.Center, topBar = {}, content = {},

        )
}