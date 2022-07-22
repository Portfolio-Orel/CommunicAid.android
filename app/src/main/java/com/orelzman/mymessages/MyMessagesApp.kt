@file:OptIn(ExperimentalFoundationApi::class)

package com.orelzman.mymessages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.orelzman.mymessages.presentation.components.CustomScaffold
import com.orelzman.mymessages.presentation.components.bottom_bar.BottomBar
import com.orelzman.mymessages.presentation.components.multi_fab.MiniFloatingAction
import com.orelzman.mymessages.presentation.components.multi_fab.MultiFab
import com.orelzman.mymessages.presentation.details_folder.DetailsFolderScreen
import com.orelzman.mymessages.presentation.details_message.DetailsMessageScreen
import com.orelzman.mymessages.presentation.login.LoginScreen
import com.orelzman.mymessages.presentation.main.MainScreen
import com.orelzman.mymessages.presentation.stats.StatisticsScreen
import com.orelzman.mymessages.presentation.unhandled_calls.UnhandledCallsScreen
import com.orelzman.mymessages.util.Screen

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun MyMessagesApp(
    viewModel: MyMessagesViewModel = hiltViewModel()
) {
    val navHostController = rememberNavController()
    val navController = navHostController as NavController

    if (!viewModel.isAuthorized) {
        LoginScreen()
    } else {
        CustomScaffold(
            startRoute = Screen.Main.route,
            navController = navHostController,
            bottomBar = {
                BottomBar(navController = it)
            },
            floatingActionButton = {
                MultiFab(
                    fabs = listOf(
                        MiniFloatingAction(
                            action = { viewModel.signOut() },
                            icon = painterResource(id = R.drawable.ic_login),
                            description = stringResource(R.string.sign_out)
                        ),
                        MiniFloatingAction(
                            action = {
                                navController.navigate(
                                    Screen.DetailsFolder.route
                                ) {
                                    popUpTo(navHostController.graph.findStartDestination().id) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            },
                            icon = painterResource(id = R.drawable.ic_new_folder),
                            description = ""
                        ),
                        MiniFloatingAction(
                            action = {
                                navController.navigate(
                                    Screen.DetailsMessage.route
                                ) {
                                    popUpTo(navHostController.graph.findStartDestination().id) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            },
                            icon = painterResource(id = R.drawable.ic_new_message),
                            description = ""
                        ),
                    ),
                    iconCollapsed = painterResource(R.drawable.ic_arrow_left),
                    iconExpanded = painterResource(R.drawable.ic_close),
                )
            },
            topBar = {}
        ) {
            NavHost(navController = navHostController, startDestination = "main") {
                composable(route = Screen.Main.route) { MainScreen(navController = navHostController) }
                composable(route = Screen.Login.route) { LoginScreen() }
                composable(route = Screen.UnhandledCalls.route) { UnhandledCallsScreen() }
                composable(route = Screen.Statistics.route) { StatisticsScreen() }
                composable(route = Screen.DetailsMessage.route) {
                    DetailsMessageScreen(navController = navHostController)
                }
                composable(
                    route = Screen.DetailsMessage.route + "/{messageId}",
                    arguments = listOf(
                        navArgument("messageId") {
                            type = NavType.StringType
                            defaultValue = ""
                        }
                    )
                ) {
                    DetailsMessageScreen(
                        navController = navHostController,
                        messageId = it.arguments?.getString("messageId")
                    )
                }
                composable(route = Screen.DetailsFolder.route) {
                    DetailsFolderScreen(navController = navHostController)
                }
                composable(
                    route = Screen.DetailsFolder.route + "/{folderId}",
                    arguments = listOf(
                        navArgument("folderId") {
                            type = NavType.StringType
                            defaultValue = ""
                        }
                    )) {
                    DetailsFolderScreen(
                        navController = navHostController,
                        folderId = it.arguments?.getString("folderId")
                    )
                }
            }
        }
    }
}